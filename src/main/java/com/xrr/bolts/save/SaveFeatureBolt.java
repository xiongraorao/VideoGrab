package com.xrr.bolts.save;

import java.io.File;
import java.util.Map;

import com.persist.bean.grab.VideoInfo;
import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HDFSHelper;
import com.xrr.bean.ObjectFeature;
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
    
    public SaveFeatureBolt(ISaveFeature sf,ISaveFeature sf2, String logDir){
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
        if(objFea == null)
            return;
        String tag = null;
        String tag2 = null;
        if(objFea.url !=null){
        	tag = objFea.url;
        	tag2 = objFea.hash;
        }
        mLogger.log(TAG+"@"+id, "prepare to save: "+tag+" to Hbase");
        boolean status = mSaver.save(objFea);
        mLogger.log(TAG+"@"+id, "saved: "+tag+", status = "+status);
        if(status){
        	count++;
        	mLogger.log(TAG+"@"+id, ", saved to urlTable records total = "+count);
        }
        mLogger.log(TAG+"@"+id, "prepare to save: "+tag2+" to Hbase");
        boolean status2 = mSaver2.save2(objFea);
        mLogger.log(TAG+"@"+id, "saved: "+tag2+", status = "+status2);
        //show record total numbers every 10 records
        if(status2){
        	count2++;
        	mLogger.log(TAG+"@"+id, ", saved to hashTable records total = "+count2);
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
