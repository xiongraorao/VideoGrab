package com.xrr.bolts;

import java.util.Map;

import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HDFSHelper;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class SaveFeatureBolt extends BaseRichBolt{
	
	private final static String TAG = "SaveFeatureBolt";
	
	private OutputCollector mCollector;
	private HDFSHelper mHelper;
	private FileLogger mLogger;
    private int id;
    private long count = 0;
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		mCollector = collector;
		id = context.getThisTaskId();
		mLogger = new FileLogger(TAG+"@"+id);
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
