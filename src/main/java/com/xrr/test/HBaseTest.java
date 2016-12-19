package com.xrr.test;

import com.persist.util.helper.HBaseHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.*;

/**
 * created by raorao 
 * test Hbase helper
 * @author ubuntu
 *
 */
public class HBaseTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		HBaseHelper hh=new HBaseHelper("localhost", 2181);
		String columnFamilies[]={"name","age","course"};
		hh.createTable("students",columnFamilies);
		hh.addRow("students", "1", "name", new String[]{"name"}, new String[]{"paul"});
		hh.addRow("students", "1", "age", new String[]{"age"}, new String[]{"20"});
		hh.addRow("students", "1", "course", new String[]{"math","physics","history"}, new String[]{"95","96","97"});
	}

}
