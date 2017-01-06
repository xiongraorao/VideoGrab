package com.xrr.test;

import com.persist.util.helper.FileLogger;

/**
 * Created by ubuntu on 17-1-5.
 */
public class JpyTest2 {
    public static void main(String[] args){
        String jpyConfig = "/home/hadoop/storm-projects/python-lib/lib.linux-x86_64-2.7/jpyconfig.properties";
        System.setProperty("jpy.config", jpyConfig);
        FileLogger mLogger = new FileLogger("/home/hadoop/VideoGrab/logs/JpyTest2");
        System.getProperties().list(mLogger.getPrintWriter());
        mLogger.log("java.libary.path: == ",System.getProperty("java.library.path"));
        mLogger.log("java.ext.dirs:== " , System.getProperty("java.ext.dirs"));
        mLogger.getPrintWriter().flush();
        ObjectDetectPython.setLogger(mLogger);
        String result = ObjectDetectPython.detect("/home/sh/workplace/objectDetection/ssd/caffe/examples/sh_ssd/feature_extractor/",
        "demo3","demo3_method",args[0]);

        //mLogger.log("result: ", result);
        mLogger.close();
    }

}
