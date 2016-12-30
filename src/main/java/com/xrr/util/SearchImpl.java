package com.xrr.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HBaseHelper;
import com.xrr.bean.ObjectFeature;

/**
 * created by raorao 2016/12/29
 * @author ubuntu
 *
 */

public class SearchImpl implements ISearch{
	private HBaseHelper mHelper;
	private FileLogger mLogger;
    private String quorum;
    private int port;
    
    private String tableName;
    private String tag;
	public SearchImpl(String quorum, int port,
    		String tableName){
		this.quorum = quorum;
		this.port = port;
		this.tableName = tableName;
	}
	public void prepare() {
		// TODO Auto-generated method stub
        if(mHelper != null)
            return;
        mHelper = new HBaseHelper(quorum, port);
	}
	public void setLogger(FileLogger log, String tag) {
		// TODO Auto-generated method stub
		mLogger = log;
		this.tag =tag;
	}
	public boolean search(String hash) {
		// TODO Auto-generated method stub
		boolean status = false;
		if(mHelper != null){
			List<String> arr = new ArrayList<String>();
			arr.add("url,hash,"+hash);
			try {
				ResultScanner rs= mHelper.getByFilter(tableName, arr);
			    for (Result result : rs) {
				      System.out.println(Bytes.toString(result.getRow()));
				
				      for (KeyValue rowKV : result.raw()) {
				          mLogger.log(tag,"Row Name: " + new String(rowKV.getRow()) + " ");
				          mLogger.log(tag,"Timestamp: " + rowKV.getTimestamp() + " ");
				          mLogger.log(tag,"column Family: " + new String(rowKV.getFamily()) + " ");
				          mLogger.log(tag,"Column Name:  " + new String(rowKV.getQualifier()) + " ");
				          mLogger.log(tag,"Value: " + new String(rowKV.getValue()) + " ");
					      //output the image url
					      Result temp = null;
					      try {
					    	  temp = mHelper.getByRowColumn(tableName, new String(rowKV.getRow()), 
									  new String(rowKV.getFamily()),"url");
					      } catch (Exception e) {
							// TODO Auto-generated catch block
					    	  e.printStackTrace();
					      }	
					      for(KeyValue kv:temp.raw()){
					    	  mLogger.log(tag,"Img_rul: " + new String(kv.getValue()));
					      }
					      
				      }
				  }
			    mLogger.log(tag, "search success!");
			    status = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mLogger.log(tag, "search fail!");
			}
		}
		return status;
	}
	public void close() {
		// TODO Auto-generated method stub
    	if(mHelper != null)
        {
            mHelper.close();
            mHelper = null;
        }
    	if(mLogger !=null){
    		mLogger.close();
    	}
	}
}
