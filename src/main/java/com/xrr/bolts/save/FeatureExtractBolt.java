package com.xrr.bolts.save;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.persist.util.helper.BufferedImageHelper;
import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HDFSHelper;
import com.xrr.bean.DetectObjectsInfo;
import com.xrr.bean.ObjectFeature;
import com.xrr.test.ObjectDetectPython;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by raorao on 17-1-6.
 */
public class FeatureExtractBolt  extends BaseRichBolt{

    private final static String TAG = "FeatureExtractBolt";
    private OutputCollector mCollector;
    private HDFSHelper mHelper;
    private String pythonStartPath;
    private String pythonModuleName;
    private String pythonMethodName;
    private int bufferSize;//when save enough images url, then detect


    private FileLogger mLogger;
    private int id;
    private long count = 0;
    private int downloadCount = 0;
    private String logDir;
    private StringBuffer sb;
    private String[] localUrls;//buffer localurl

    public FeatureExtractBolt(String logDir){
        this.logDir = logDir;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        mCollector = collector;
        id = context.getThisTaskId();
        sb = new StringBuffer();
        mLogger = new FileLogger(logDir+ File.separator+TAG+"@"+id);
        mLogger.log(TAG+"@"+id, "prepare to extract feature");

        String jpyConfig = (String) stormConf.get("jpyConfig");
        System.setProperty("jpy.config", jpyConfig);

        pythonStartPath = (String) stormConf.get("pythonStartPath");
        pythonModuleName = (String) stormConf.get("pythonModuleName");
        pythonMethodName = (String) stormConf.get("pythonMethodName");
        mLogger.log("batchSize: ",stormConf.get("batchSize")+"");

        bufferSize = Integer.parseInt((String) stormConf.get("batchSize"));
        localUrls = new String[bufferSize];
    }


    @Override
    public void execute(Tuple tuple) {
        ObjectFeature objFea = (ObjectFeature)tuple.getValue(1);
        if(objFea == null)
            return;

        //initialize objFea even if it is just parent_image, no feature attibutes
        objFea.hash = "no data";
        objFea.feature = "no data";
        objFea.category = "no data";
        objFea.score = "no data";
        objFea.location = "no data";

        //download from hdfs to local disk
        mLogger.log(TAG+"@"+id, "start download url to local ");
        if(objFea.dir != null){
            mHelper = new HDFSHelper(objFea.dir);
        }
        else{
            mLogger.log(TAG+"@"+id, "the dir is null, return ");
            return ;
        }
        String localUrl = "/home/hadoop/VideoGrab/temp_image/"+objFea.fileName;
        if(mHelper.download(localUrl, objFea.fileName)){
            mLogger.log(TAG+"@"+id, "downloaded success!" );
            localUrls[downloadCount] = localUrl;
            downloadCount++;
            mLogger.log(TAG+"@"+id, "downloadCount number: "+downloadCount);
        }else{
            mLogger.log(TAG+"@"+id, "downloaded fail!");
            return;
        }

        //patching processing
        if(downloadCount<bufferSize)
        {
            mCollector.ack(tuple);
            return;
        }else{
            // batch detect in python
            mLogger.log(TAG+"@"+id, "downloadCount number= "+downloadCount+"buffersize= "+bufferSize);
            mLogger.log(TAG+"@"+id, "start detect objects and extract features! ");
            ObjectDetectPython.setLogger(mLogger);
            String detectResults = ObjectDetectPython.detect2(pythonStartPath,pythonModuleName,pythonMethodName, localUrls);
            downloadCount=0;
            if(detectResults != null) {
                mLogger.log(TAG+"@"+id, "detect success!");
                mLogger.log(TAG+"@"+id, "start parse detected results ");
                String[] results = detectResults.split("/");
                mLogger.log(TAG+"@"+id, "there are " + results.length + " objects detected ");
                Rectangle[] rects = new Rectangle[results.length];
                ObjectFeature[] objFeas = new ObjectFeature[results.length];
                BufferedImage[] bis = new BufferedImage[results.length];
                DetectObjectsInfo doi = new DetectObjectsInfo();
                Gson gson_result = new Gson();
                for(int i = 0; i < results.length; i++){

                    try{
                        doi = gson_result.fromJson(results[i],DetectObjectsInfo.class);
                        if(doi != null){

                            mLogger.log(TAG+"@"+id," parse doi success!");
                            //you must initialize objFeas[i] before using it, or it will throw null pointer Exception
                            objFeas[i] = new ObjectFeature();

                            objFeas[i].video_id = objFea.video_id;
                            objFeas[i].parent_img = objFea.parent_img;

                            objFeas[i].hash = doi.hash;
                            //transfer doi.feature to string
                            for(double iii:doi.feature){

                                sb.append(Double.toString(iii)+",");
                            }
                            objFeas[i].feature = sb.toString();
                            sb.delete(0,sb.length()-1);
                            objFeas[i].category = doi.category;
                            objFeas[i].score = doi.score + "";
                            objFeas[i].location = doi.location;
                            mLogger.log("parse-result: ","catagory: " + doi.category + "score: " + doi.score +
                                    "feature: " + doi.feature + "hash: " + doi.hash);
                            String[] coordinates = doi.location.split(",");
                            int x = Integer.parseInt(coordinates[0].trim());
                            int y = Integer.parseInt(coordinates[1].trim());
                            int w = Integer.parseInt(coordinates[2].trim())-x;
                            int h = Integer.parseInt(coordinates[3].trim())-y;

                            rects[i] = new Rectangle(x,y,w,h);
                            mLogger.log("parse-location: ","x = " + x + ", y = " + y +
                                    ", width = " + w + ", height = " + h);
                            bis[i] = BufferedImageHelper.segmentTest(localUrl,rects[i]);
                            //save the child images by coordinates
                            if(BufferedImageHelper.saveBufImg(bis[i],objFea.fileName.replace("0.png",(i+1) + ".png"), mHelper,mLogger)){
                                objFeas[i].url = objFea.dir + File.separator + objFea.fileName.replace("0.png",(i+1) + ".png");
                                mLogger.log("save-subImg: ", objFea.fileName.replace("0.png",(i+1) + ".png") + ", success! ");
                            }else
                                mLogger.log("save-subImg: ", objFea.fileName.replace("0.png",(i+1) + ".png") + ", fail! ");
                        }else{
                            mLogger.log(TAG+"@"+id," parse doi fail, doi is null!");
                            return;
                        }


                    }catch(JsonSyntaxException e){
                        e.printStackTrace(mLogger.getPrintWriter());
                        mLogger.getPrintWriter().flush();
                    }
                }
                mCollector.emit(new Values(objFea.url,objFea,objFeas));
                count++;
                mLogger.log(TAG+"@"+id, "extracted "+ count + " images");
                mLogger.log("parse-location: ","count = " + rects.length);
                mLogger.log("save-subImg: ", "count = " + bis.length);
                mLogger.log(TAG+"@"+id, "end parse detected results, then emit to next bolt");
                mCollector.ack(tuple);

            }else{
                mLogger.log(TAG+"@"+id, "detect fail!");
                mCollector.fail(tuple);
                return;
            }

        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("url", "objFea", "subObjFea"));
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mLogger.close();
        mHelper.close();
    }
}
