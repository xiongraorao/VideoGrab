package com.xrr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.persist.util.helper.FileHelper;
import com.persist.util.helper.Logger;
import com.xrr.bean.SearchConfig;
import com.xrr.bolts.search.Search_ParseBolt;
import com.xrr.bolts.search.SearchBolt;
import com.xrr.util.ISearch;
import com.xrr.util.SearchImpl;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

/**
 * created by raorao 2016/12/28
 * function: search image by image
 * @author ubuntu
 *
 */

public class Search {
	
    private final static String TAG = "Search";
    private final static String URL_SPOUT = "url-spout";
    private final static String PARSE_BOLT = "parse-bolt";
    private final static String SEARCH_BOLT = "search-bolt";
    
	public static void main(String[] args) throws Exception{

        
        String configPath = "search_config.json";
        if(args.length > 0)
            configPath = args[0];

        //load config from file "config.json" in current directory
        SearchConfig baseConfig = new SearchConfig();
        Gson gson = new Gson();
        try
        {
            baseConfig = gson.fromJson(FileHelper.readString(configPath), SearchConfig.class);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
        }
        
		// TODO Auto-generated method stub
        try {
            Logger.setOutput(new FileOutputStream(baseConfig.logDir+File.separator+TAG, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.setDebug(false);
        }
        Logger.log(TAG, "configPath:"+configPath);
        Logger.log(TAG, gson.toJson(baseConfig));
        
        //construct kafka spout config
        //brokerZkpath should be zkRootPath
        BrokerHosts brokerHosts = new ZkHosts(baseConfig.zks);
        SpoutConfig spoutConfig = new SpoutConfig(
                brokerHosts, baseConfig.topic, baseConfig.zkRoot, baseConfig.id);
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.zkServers = Arrays.asList(baseConfig.zkServers);
        spoutConfig.zkPort = baseConfig.zkPort;
        spoutConfig.forceFromStart = false;
        
        ISearch is = new SearchImpl(baseConfig.hbaseQuorum, baseConfig.hbasePort, baseConfig.hbaseTable);
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(URL_SPOUT, new KafkaSpout(spoutConfig),baseConfig.urlSpoutParallel);
        builder.setBolt(PARSE_BOLT,new Search_ParseBolt(baseConfig.logDir),baseConfig.parseBoltParallel).shuffleGrouping(URL_SPOUT);
        builder.setBolt(SEARCH_BOLT, new SearchBolt(is, baseConfig.logDir),baseConfig.saveFeatureBoltParallel)
        .fieldsGrouping(PARSE_BOLT, new Fields("url"));
        
        //submit topology
        Config conf = new Config();
        if (args.length > 1) {
            conf.setNumWorkers(baseConfig.workerNum);
            conf.setDebug(false);
            StormSubmitter.submitTopology(args[1], conf, builder.createTopology());
        } else {
            conf.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("featureSave", conf, builder.createTopology());
        }
        Logger.close();
        
	}

}
