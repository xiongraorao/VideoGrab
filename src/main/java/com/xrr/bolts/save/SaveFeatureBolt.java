package com.xrr.bolts.save;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import clojure.lang.Obj;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.persist.bean.grab.VideoInfo;
import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HDFSHelper;
import com.persist.util.helper.BufferedImageHelper;
import com.xrr.bean.DetectObjectsInfo;
import com.xrr.bean.ObjectFeature;
import com.xrr.test.ObjectDetectPython;
import com.xrr.util.ISaveFeature;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * created by raorao 2016/12/22
 * function: save feature to hbase
 * @author ubuntu
 *
 */
public class SaveFeatureBolt extends BaseRichBolt{
	
	private final static String TAG = "SaveFeatureBolt";
	
	private OutputCollector mCollector;
	private FileLogger mLogger;
	private ISaveFeature mSaver;
	private ISaveFeature mSaver2;//save hash-url table for fast selection
	private String logDir;
    private int id;
    private long count = 0;
    private long count2 = 0;
    
    public SaveFeatureBolt(ISaveFeature sf, ISaveFeature sf2, String logDir){
    	mSaver = sf;
    	mSaver2 = sf2;
    	this.logDir = logDir;
    }

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		mCollector = collector;
		mSaver.prepare();//init hbase
		mSaver2.prepare();
		id = context.getThisTaskId();
		mLogger = new FileLogger(logDir+File.separator+TAG+"@"+id);
		mLogger.log(TAG+"@"+id, "prepared");
		mSaver.setLogger(mLogger,TAG+"@"+id+"_saveUrl");
		mSaver2.setLogger(mLogger, TAG+"@"+id+"_saveHash");
	}

	public void execute(Tuple tuple) {
		// TODO Auto-generated method stub
        ObjectFeature objFea = (ObjectFeature) tuple.getValue(1);
		ObjectFeature[] objFeas = (ObjectFeature[]) tuple.getValue(2);
        if(objFea == null || objFeas == null)
		{
			mLogger.log(TAG+"@"+id,"can not receive data from FeatureExtractBolt, return");
			return;
		}

        String tag = null;
        if(objFea.url !=null){
        	tag = objFea.url;
        }
		//objFea do not save to imgHash table for it has null hash value
        mLogger.log(TAG+"@"+id, "prepare to save: "+tag+" to urlTable");
        boolean status = mSaver.save(objFea);
		mLogger.log(TAG+"@"+id, "saved: "+tag+", save urlTable= "+status );
        if(status){
        	count++;
        	mLogger.log(TAG+"@"+id, ", saved origin image to urlTable records total = "+count);
        }

        //save objFeas to hbase
		for( int i = 0; i < objFeas.length; i ++){
			mLogger.log(TAG+"@"+id, "prepare to save: "+objFeas[i].url+" to urlTable");

//			//test objFeas is null or not!
//			for(int j = 0 ;j < objFeas.length ; j++){
//					mLogger.log(" ======ojbFeas array content test:  ",objFeas[j].parent_img + " " +
//							objFeas[j].video_id+" "+objFeas[j].hash+" "+objFeas[j].feature+" "+
//							objFeas[j].category+ " "+ objFeas[j].score+ " "+objFeas[j].location);
//				}

			boolean status2 = mSaver.save(objFeas[i]);
			mLogger.log(TAG+"@"+id, "saved: "+objFeas[i].url+", save urlTable= "+status2 );
			if(status2 && count%10 == 0){
				count++;
				mLogger.log(TAG+"@"+id, ", saved subImage to urlTable records total = "+count);
			}

			//save objFeas to hash-url table
			mLogger.log(TAG+"@"+id, "prepare to save: "+objFeas[i].hash+" to hashTable");
			boolean status3 = mSaver2.save2(objFeas[i]);
			mLogger.log(TAG+"@"+id, "saved: "+objFeas[i].hash+", status = "+status3);
			//show record total numbers every 10 records
			if(status3 && count2%10 == 0){
				count2++;
				mLogger.log(TAG+"@"+id, ", saved subImage to hashTable records total = "+count2);
			}

		}

        mCollector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		mLogger.close();
		mSaver.close();
	}

}
