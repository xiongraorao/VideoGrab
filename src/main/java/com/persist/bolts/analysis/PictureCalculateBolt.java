package com.persist.bolts.analysis;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.persist.bean.analysis.PictureResult;
import com.persist.util.helper.FileLogger;
import com.persist.util.tool.analysis.Predict;
import java.util.List;
import java.util.Map;

/**
 * Created by taozhiheng on 16-8-11.
 *
 * load .so library from lib or absolute path,
 * predict images,
 * and trigger PictureNotifierBolt to publish
 *
 */
public class PictureCalculateBolt extends BaseRichBolt {

    private final static String TAG = "PictureCalculateBolt";

    private final static float DEFAULT_WARN_VALUE = 0.75f;
    private final static int DEFAULT_BUFFER_SIZE = 1000;
    private final static int DEFAULT_DURATION = 3000;
    private final static int DEFAULT_TICK = 10;

    private OutputCollector mCollector;
    private int mTick;

    private FileLogger mLogger;
    private int id;
    private long count = 0;

    private String so;
    private float warnValue;
    private int bufferSize;
    private long duration;

    public PictureCalculateBolt(String so)
    {
        this(so, DEFAULT_WARN_VALUE);
    }

    public PictureCalculateBolt(String so, float warnValue)
    {
        this(so, warnValue, DEFAULT_BUFFER_SIZE, DEFAULT_DURATION, DEFAULT_TICK);
    }

    public PictureCalculateBolt(String so, float warnValue, int bufferSize, long duration, int tick)
    {
        this.so = so;
        this.warnValue = warnValue;
        this.bufferSize = bufferSize;
        this.duration = duration;
        this.mTick = tick;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mLogger.close();
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        mCollector = collector;
        Predict.init(so);
        Predict.setWarnValue(warnValue);
        Predict.setBufferSize(bufferSize);
        Predict.setDuration(duration);
        id = context.getThisTaskId();
        mLogger = new FileLogger("picture-calculate@"+id);
        mLogger.log(TAG+"@"+id, "prepare");
    }

    /*
     * (non-Javadoc)this function will be called when using topologybuilder
     * @see backtype.storm.topology.base.BaseComponent#getComponentConfiguration()
     */
    @Override
    public Map<String, Object> getComponentConfiguration() {
        //set a tick tuple sender to flush compute buffer
        Config config = new Config();
        config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, mTick);
        return config;
    }

    public void execute(Tuple input) {
        boolean force = false;
        //receive tick tuple, flush compute buffer
        if(input.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && input.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID))
        {
            force = true;
            mLogger.log(TAG, "receive tick tuple, force to trigger predict");
        }
        //receive standard tuple
        else
        {
            force = input.getBoolean(0);
        }
        mLogger.log(TAG+"@"+id, "trigger triggerPredict, force="+force);
        List<PictureResult> list = Predict.triggerPredict(force);//emit all results to the next tuple
        if (list != null && list.size() > 0)
        {
            for (PictureResult result : list)
            {
                mCollector.emit(new Values(result));
                if (result.description != null) {
                    mLogger.log(TAG+"@"+id, "emit result: " + result.description.url + ", " + result.percent);
                }
            }
            count += list.size();
            mLogger.log(TAG+"@"+id, "total="+count);
        }
        else
        {
        	mLogger.log(TAG+"@"+id, "list is null");
        }
        mCollector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("result"));
    }
}
