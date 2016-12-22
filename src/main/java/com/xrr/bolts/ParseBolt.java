package com.xrr.bolts;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.persist.bean.grab.VideoInfo;
import com.persist.util.helper.FileLogger;
import com.xrr.bean.ObjectFeature;

/**
 * created by raorao 2016/12/22
 * function: parse kafka message in json format
 * @author ubuntu
 *
 */
public class ParseBolt extends BaseRichBolt{
	
	private final static String TAG = "ParseBolt";
    private Gson mGson;
    private OutputCollector mCollector;

    private FileLogger mLogger;
    private int id;
    private long count = 0;

	public void prepare(Map map, TopologyContext context, OutputCollector outputCollector) {
		// TODO Auto-generated method stub
		mGson = new Gson();
		mCollector = outputCollector;
		id = context.getThisTaskId();
		mLogger = new FileLogger(TAG+"@"+id);
		mLogger.log(TAG+"@"+id, "prepare to parse kafka message");
	}

	public void execute(Tuple tuple) {
		// TODO Auto-generated method stub
		String data = (String)tuple.getValue(0);
        count++;
        mLogger.log(TAG+"@"+id, "parse data: "+data);
        String url;
        ObjectFeature objfea;
        try
        {
            //resolve json string
        	objfea = mGson.fromJson(data, ObjectFeature.class);
            if(objfea != null)
            {
                url = objfea.url;
                mCollector.emit(new Values(url, objfea));
            }
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace(mLogger.getPrintWriter());
            mLogger.getPrintWriter().flush();
            e.printStackTrace();
        }
        finally
        {
            mLogger.log(TAG+"@"+id, "parse msg total="+count);
            mCollector.ack(tuple);
        }
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("url", "objfea"));
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		mLogger.close();
	}

}
