package com.xrr.test;

import com.persist.util.helper.HBaseHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.*;

/**
 * created by raorao 2016/12/19
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
		hh.addRow("students", "2", "name", new String[]{"name"}, new String[]{"CKhuntoria"});
		hh.addRow("students", "2", "age", new String[]{"age"}, new String[]{"21"});
		hh.addRow("students", "2", "course", new String[]{"math","physics","history"}, new String[]{"98","99","99"});

		
		Result r = hh.getByRowColumn("students", "1", "course", "math");
		//output result
	    for (KeyValue rowKV : r.raw()) {
	      System.out.print("Row Name: " + new String(rowKV.getRow()) + " ");
	      System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
	      System.out.print("column Family: " + new String(rowKV.getFamily()) + " ");
	      System.out.print("Column Name:  " + new String(rowKV.getQualifier()) + " ");
	      System.out.println("Value: " + new String(rowKV.getValue()) + " ");
	    }
	    
	    List<String> arr = new ArrayList<String>();
	    arr.add("course,math,98");
	    ResultScanner r2 = hh.getByFilter("students",arr);
	    
	    for (Result result : r2) {
	      System.out.println(Bytes.toString(result.getRow()));
	
	      for (KeyValue rowKV : result.raw()) {
	          System.out.print("Row Name: " + new String(rowKV.getRow()) + " ");
	          System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
	          System.out.print("column Family: " + new String(rowKV.getFamily()) + " ");
	          System.out.print("Column Name:  " + new String(rowKV.getQualifier()) + " ");
	          System.out.println("Value: " + new String(rowKV.getValue()) + " ");
	      }
	  }
	      
		
	}

}
