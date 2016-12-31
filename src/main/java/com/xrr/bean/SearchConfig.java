package com.xrr.bean;

import java.io.Serializable;

public class SearchConfig implements Serializable{
	
	public String logDir = "/home/hadoop/VideoGrab/logs";
	public int urlSpoutParallel = 1;
	public int parseBoltParallel = 1;
	public int saveFeatureBoltParallel = 1;
	
	public String zks = "localhost:2181";
	public String topic = "searchTopic";
	public String zkRoot = "/opt/zookeeper_data/data";
	public String id = "feature-save";
	public String[] zkServers={"localhost"};
	public int zkPort = 2181;
	public String brokerList = "localhost:9092";
	public int workerNum = 1;
	
	public String hbaseQuorum = "localhost";
	public int hbasePort = 2181;
	public String hbaseTable = "imageFeature";
	
}
