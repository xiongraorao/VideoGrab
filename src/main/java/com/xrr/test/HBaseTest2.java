package com.xrr.test;

import com.persist.util.helper.FileLogger;
import com.xrr.bean.ObjectFeature;
import com.xrr.util.ISaveFeature;
import com.xrr.util.SaveFeatureImpl;

/**
 * Created by ubuntu on 17-1-8.
 */
public class HBaseTest2 {

    public static void main(String[] args){
        ISaveFeature mSaver;
        FileLogger mLogger = new FileLogger("/home/hadoop/VideoGrab/logs/HbaseTest2");
        String quorum = "localhost";
        String tag = "HbaseTest2";
        int port = 2181;
        String tableName = "table_for_test";
        String[] columnFamilies = {"url","feature"};
        String[][] columns = {{"video_id", "parent_img"},{"hash", "feature", "catagory", "score", "location"}};
        mSaver = new SaveFeatureImpl(quorum,port,tableName,columnFamilies,columns);

        ObjectFeature obj = new ObjectFeature("http://www.google.com","video_id","nowtime","no_parentImg","web-site",
                "0.88","hashcode","locations","features","dirs","fileName");
        mLogger.log(tag,"begin save data");
        mSaver.setLogger(mLogger,tag);
        mSaver.prepare();
        mSaver.save(obj);
        mSaver.close();
        mLogger.close();

    }
}
