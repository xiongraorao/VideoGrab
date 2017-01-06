package com.xrr.test;

import clojure.lang.Obj;
import com.persist.util.helper.FileLogger;

/**
 * Created by ubuntu on 17-1-6.
 */
public class JpyTestInThread extends Thread {

    private FileLogger logger;
    private String imgUrl;

    public JpyTestInThread(FileLogger logger, String imgUrl) {
        this.logger = logger;
        this.imgUrl = imgUrl;
    }

    public static void main(String[] args){
        String jpyConfig = "/home/hadoop/storm-projects/python-lib/lib.linux-x86_64-2.7/jpyconfig.properties";
        System.setProperty("jpy.config", jpyConfig);

        FileLogger log = new FileLogger("/home/hadoop/VideoGrab/logs/JpyTestInThread");
        new JpyTestInThread(log,args[0]).start();
    }

    @Override
    public void run() {
        super.run();
        ObjectDetectPython.setLogger(this.logger);
        String result = ObjectDetectPython.detect("/home/sh/workplace/objectDetection/ssd/caffe/examples/sh_ssd/feature_extractor/",
                "feature_extractor","detect",this.imgUrl);
        this.logger.close();
    }
}
