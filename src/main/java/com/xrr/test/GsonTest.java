package com.xrr.test;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.persist.util.helper.FileLogger;

import java.io.File;

/**
 * Created by raorao on 17-2-13.
 */
public class GsonTest {
    public static void main(String[] args)
    {
        String[] imageUrls = {"/home/hadoop/VideoGrab/images/dog-1.jpg",
                "/home/hadoop/VideoGrab/images/car-4.jpg",
                "/home/hadoop/VideoGrab/images/horse-10.jpg",
                "/home/hadoop/VideoGrab/images/person-3.jpg",
                "/home/hadoop/VideoGrab/images/aeroplane-4.jpg",
                "/home/hadoop/VideoGrab/images/person-3.jpg",
                "/home/hadoop/VideoGrab/images/dog-2.jpg",
                "/home/hadoop/VideoGrab/images/dog-3.jpg",
                "/home/hadoop/VideoGrab/images/dog-4.jpg",
                "/home/hadoop/VideoGrab/images/dog-5.jpg"
        };
        Gson gson = new Gson();
        String res = gson.toJson(imageUrls,imageUrls.getClass());
        System.out.println(res);
        long t = System.currentTimeMillis();
        Jpy.test(res);
        System.out.println("time: " + (System.currentTimeMillis() - t));
    }
}

class Jpy{
    static void test(String imageUrls)
    {
        String jpyConfig = "/home/hadoop/storm-projects/python-lib/lib.linux-x86_64-2.7/jpyconfig.properties";
        System.setProperty("jpy.config", jpyConfig);
        FileLogger mLogger = new FileLogger("/home/hadoop/VideoGrab/logs/GsonTest");
        ObjectDetectPython.setLogger(mLogger);
        String result = ObjectDetectPython.detect("/home/sh/workplace/objectDetection/ssd/caffe/examples/sh_ssd/",
                "extract","detect2",imageUrls);
        mLogger.log("imageUrls: ",imageUrls);
        mLogger.log("result: ", result);
        mLogger.close();
    }
}
