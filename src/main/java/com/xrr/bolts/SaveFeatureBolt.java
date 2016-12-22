package com.xrr.bolts;

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
	private HDFSHelper mHelper;
	private FileLogger mLogger;
	private ISaveFeature mSaver;
    private int id;
    private long count = 0;
    
    public SaveFeatureBolt(ISaveFeature sf){
    	mSaver=sf;
    }
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		mCollector = collector;
		mSaver.prepare();//init hbase
		id = context.getThisTaskId();
		mLogger = new FileLogger(TAG+"@"+id);
		mLogger.log(TAG+"@"+id, "prepared");
		mSaver.setLogger(mLogger,TAG+"@"+id);
	}

	public void execute(Tuple tuple) {
		// TODO Auto-generated method stub
        ObjectFeature objFea = (ObjectFeature) tuple.getValue(1);
        if(objFea == null)
            return;
        count++;
        String tag = null;
        if(objFea.url !=null){
        	tag = objFea.url;
        }
        mLogger.log(TAG+"@"+id, "prepare to save: "+tag+"to Hbase");
        boolean status = mSaver.save(objFea);
        mLogger.log(TAG+"@"+id, "saved: "+objFea.url+", status = "+status);
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
