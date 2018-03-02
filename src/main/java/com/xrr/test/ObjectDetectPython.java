package com.xrr.test;

import clojure.lang.Obj;
import com.google.gson.Gson;
import com.persist.util.helper.BufferedImageHelper;
import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HDFSHelper;
import org.jpy.PyLib;
import org.jpy.PyModule;
import org.jpy.PyObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by ubuntu on 17-1-5.
 */
public class ObjectDetectPython {

    private static PyModule module;
    private static String detect = "detect";
    private static String detect2 = "detect2";
    private static FileLogger log;

    public static void setLogger(FileLogger logger){
        ObjectDetectPython.log = logger;
    }

    public static void init(String path, String module, String detect)
    {
        if(ObjectDetectPython.module != null)
            return;
        try{
            log.log("ObjectDetect: ", "start init object detection...");
            PyLib.startPython(path);
            log.log("ObjectDetect: is Running: ",PyLib.isPythonRunning()+"");
            ObjectDetectPython.module = PyModule.importModule(module);
            ObjectDetectPython.detect = detect;
        }catch (Exception e){
            e.printStackTrace(log.getPrintWriter());
            log.getPrintWriter().flush();
        }

    }

    public static String detect(String pythonStartPath,String moduleName, String methodName, String filepath)
    {
        log.log("ObjectDetect: ","start detect");
        log.log("ObjectDetect: args: ",pythonStartPath + " " + moduleName + " " + methodName + " " + filepath );
        if(ObjectDetectPython.module == null){
            ObjectDetectPython.init(pythonStartPath,moduleName,methodName);
        }
        PyObject o = ObjectDetectPython.module.callMethod(detect, filepath);
        if (o == null)
            return null;
        //log.log("ObjectDetect: ", "detect-result: " + o.getStringValue());
        log.log("ObjectDetect: ", "detect-result: " + true);
        return o.getStringValue();
    }

    public static String detect2(String pythonStartPath,String moduleName, String methodName, String[] files)
    {
        log.log("ObjectDetect: ","start detect");
        log.log("ObjectDetect: "+ "args: ",pythonStartPath + " " + moduleName + " " + methodName + " files counts= " + files.length );
        if(ObjectDetectPython.module == null){
            ObjectDetectPython.init(pythonStartPath,moduleName,methodName);
        }
        Gson gson = new Gson();
        String filesList = gson.toJson(files,files.getClass());
        log.log("ObjectDetect: ","filesList: "+ filesList);
        long start = System.currentTimeMillis();
        PyObject o = ObjectDetectPython.module.callMethod(detect2, filesList);
        long end = System.currentTimeMillis();
        if (o == null)
        {
            log.log("ObjectDetect: ", "return no data");
            return null;
        }
        //log.log("ObjectDetect: ", "detect-result: " + o.getStringValue());
        log.log("ObjectDetect: ", "detect-result: " + true);
        log.log("ObjectDetect: ", "detect cost time: " + (end-start));
        return o.getStringValue();
    }

    public static void main(String[] args){
        FileLogger mLogger = new FileLogger("/home/hadoop/VideoGrab/logs/ObjectDetectlog");
        String[] ss = {"/home/sh/workplace/objectDetection/ssd/caffe/examples/images/bird-1.jpg","/home/sh/workplace/objectDetection/ssd/caffe/examples/images/horse-1.jpg", "/home/sh/workplace/objectDetection/ssd/caffe/examples/images/car-1.jpg"};
        String result = ObjectDetectPython.detect2("/home/sh/workplace/objectDetection/ssd/caffe/examples/sh_ssd/","extract","detect2",ss);
        mLogger.log("result",result);


    }
}
