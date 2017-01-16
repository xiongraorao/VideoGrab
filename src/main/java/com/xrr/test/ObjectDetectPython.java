package com.xrr.test;

import clojure.lang.Obj;
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
            log.log("is Running: ",PyLib.isPythonRunning()+"");
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
        log.log("args: ",pythonStartPath + " " + moduleName + " " + methodName + " " + filepath );
        if(ObjectDetectPython.module == null){
            ObjectDetectPython.init(pythonStartPath,moduleName,methodName);
        }
        PyObject o = ObjectDetectPython.module.callMethod(detect, filepath);
        if (o == null)
            return null;
        log.log("ObjectDetect: ", "detect-result: " + o.getStringValue());
        return o.getStringValue();
    }

}
