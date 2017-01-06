package com.xrr.test;

import org.jpy.PyLib;
import org.jpy.PyModule;
import org.jpy.PyObject;

import com.persist.util.helper.FileLogger;

public class JpyTest {
	public static void main(String[] args){

		FileLogger mLogger = new FileLogger("/home/hadoop/VideoGrab/logs/JpyTest");

		String jpyConfig = "/home/hadoop/storm-projects/python-lib/jpyconfig.properties";
		System.setProperty("jpy.config", jpyConfig);
		
		//load python script
		PyLib.startPython("/home/sh/workplace/objectDetection/ssd/caffe/examples/sh_ssd/feature_extractor/");
		PyModule module = PyModule.importModule("feature_extractor");
		PyObject o = module.callMethod("detect", args[0]);
		String d = o.getStringValue();
		mLogger.log("return-result", d);
		mLogger.close();
	}
}
