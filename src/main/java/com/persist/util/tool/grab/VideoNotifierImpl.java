package com.persist.util.tool.grab;

import com.persist.util.helper.FileLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by taozhiheng on 16-7-20.
 *
 */
public class VideoNotifierImpl implements IVideoNotifier {

    private final static String TAG = "PictureNotifierImpl";

    private Jedis mJedis;
    private String host;
    private int port;
    private String password;
    private String[] channels;

    private FileLogger mLogger;

    public VideoNotifierImpl(String host, int port, String password, String[] channels)
    {
        if(host == null || password == null)
            throw  new RuntimeException("Redis host or password must not be null");
        this.host = host;
        this.port = port;
        this.password = password;
        this.channels = channels;
        mLogger = new FileLogger("redis");
    }

    private void initJedis()
    {
        if(mJedis != null)
            return;
        JedisPool pool = null;
        try {
            // JedisPool依赖于apache-commons-pools1
            JedisPoolConfig config = new JedisPoolConfig();
            pool = new JedisPool(config, host, port, 6000, password);
            mJedis = pool.getResource();
            mLogger.log(TAG, "get redis");
        } catch (Exception e) {
            e.printStackTrace(mLogger.getPrintWriter());
            mLogger.getPrintWriter().flush();
        }
    }

    public void prepare() {
        initJedis();
    }

    public Jedis getJedis()
    {
        return mJedis;
    }

    public void notify(String msg) {
        if(mJedis == null) {
            initJedis();
        }

        if(mJedis != null && channels != null)
        {
            for (String channel : channels)
            {
                try
                {
                    mJedis.publish(channel, msg);
                }
                catch (JedisConnectionException e)
                {
                    e.printStackTrace(mLogger.getPrintWriter());
                    mLogger.getPrintWriter().flush();
                    stop();
                }
            }
        }
    }

    public void stop() {
        if(mJedis != null)
        {
            mJedis.close();
            mJedis = null;
            mLogger.log(TAG, "close redis");
        }
    }
}
