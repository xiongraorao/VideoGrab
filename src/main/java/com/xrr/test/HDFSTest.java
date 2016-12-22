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
		HDFSHelper hh=new HDFSHelper("hdfs://localhost:9000/user/hadoop");
		hh.upload("/home/ubuntu/mbuntu-0.jpg", "1.jpg");
		hh.close();
		hh.download("/home/hadoop/mbuntu-0-from-hdfs.jpg", "1.jpg");
		hh.close();
	}

}
