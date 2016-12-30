package com.xrr.bolts.search;

import java.util.Map;

import com.persist.util.helper.FileLogger;
import com.xrr.bean.ImageInfo;
import com.xrr.util.ISearch;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class SearchBolt extends BaseRichBolt{
	
	private final static String TAG = "SearchBolt";
	private ISearch mSearcher;
	private OutputCollector mCollector;
	private FileLogger mLogger;
    private int id;
    
	public SearchBolt(ISearch is){
		this.mSearcher = is;
	}
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		mCollector = collector;
		mSearcher.prepare();//init hbase
		id = context.getThisTaskId();
		mLogger = new FileLogger(TAG+"@"+id);
		mLogger.log(TAG+"@"+id, "prepared");
		mSearcher.setLogger(mLogger, TAG+"@"+id+"_search");
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		ImageInfo imgInfo = (ImageInfo)input.getValue(1);
		if(imgInfo== null)
			return;
		String tag = null;
		if(imgInfo.url != null)
			tag = imgInfo.url;
        mLogger.log(TAG+"@"+id, "prepare to search: "+tag+" in Hbase");
        boolean status = mSearcher.search(imgInfo.hash);
        mLogger.log(TAG+"@"+id, "saved: "+tag+", status = "+status);
        mCollector.ack(input);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		mLogger.close();
		mSearcher.close();
	}
	
	

}
