package com.xrr.bean;

import java.io.Serializable;

/**
 * create by raorao 2016/12/22
 * function:Feature Save Topology Config 
 * @author ubuntu
 *
 */

public class FeatureSaveConfig implements Serializable{
	
	public String logDir = "/home/hadoop/VideoGrab/logs";
	public int urlSpoutParallel = 2;
	public int parseBoltParallel = 2;
	public int saveFeatureBoltParallel = 2;
	
	public String zks = "localhost:2181";
	public String topic = "sendImageTopic";
	public String zkRoot = "/opt/zookeeper_data/data";
	public String id = "feature-save";
	public String[] zkServers={"localhost"};
	public int zkPort = 2181;
	public String brokerList = "localhost:9092";
	public int workerNum = 1;
	
	public String hbaseQuorum = "localhost";
	public int hbasePort = 2181;
	public String hbaseTable = "imageUrl";
	public String hbaseTable_hash = "imageHash";
	public String hbaseColumnFamily = "url";
	public String hbaseColumnFamily_hash = "hash";
	public String[] hbaseColumns = {"video_id","parent_img","hash","time"};
	public String[] hbaseColumns_hash = {"url"};
	
	public FeatureSaveConfig(){
		
	}
	

}
