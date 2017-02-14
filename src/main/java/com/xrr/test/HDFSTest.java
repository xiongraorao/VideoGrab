package com.xrr.test;

import com.persist.util.helper.HDFSHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import java.io.*;
import java.net.URI;
import java.lang.String;

/**
 * create by raorao 2016/12/19
 * @author ubuntu
 *
 */
public class HDFSTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HDFSHelper hh=new HDFSHelper("hdfs://192.168.1.1:9000/user/hadoop/grabbedImages");
		hh.upload("/home/hadoop/VideoGrab/images/dog-1.jpg", "dog-1.jpg");
		hh.upload("/home/hadoop/VideoGrab/images/dog-2.jpg", "dog-2.jpg");
		hh.upload("/home/hadoop/VideoGrab/images/dog-3.jpg", "dog-3.jpg");
		hh.upload("/home/hadoop/VideoGrab/images/dog-4.jpg", "dog-4.jpg");
		hh.upload("/home/hadoop/VideoGrab/images/dog-5.jpg", "dog-5.jpg");
		hh.close();
	}

}
