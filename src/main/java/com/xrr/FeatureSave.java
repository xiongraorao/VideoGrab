package com.xrr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.persist.bean.grab.GrabConfig;
import com.persist.util.helper.FileHelper;
import com.persist.util.helper.Logger;
import com.xrr.bean.FeatureSaveConfig;
import com.xrr.bean.ObjectFeature;
import com.xrr.bolts.save.FeatureExtractBolt;
import com.xrr.bolts.save.ParseBolt;
import com.xrr.bolts.save.SaveFeatureBolt;
import com.xrr.util.ISaveFeature;
import com.xrr.util.SaveFeatureImpl;

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
 * create by raorao 2016/12/22
 * function: save features from kafka message
 * @author ubuntu
 *
 */

public class FeatureSave {
	
    private final static String TAG = "FeatureSave";

    private final static String URL_SPOUT = "url-spout";
    private final static String PARSE_BOLT = "parse-bolt";
    private final static String EXTRACT_BOLT = "feature-extract-bolt";
    private final static String SAVE_BOLT = "save-bolt";


	public static void main(String[] args) throws Exception{

        //String jpyConfig = "/home/hadoop/storm-projects/python-lib/lib.linux-x86_64-2.7/jpyconfig.properties";
        //System.setProperty("jpy.config", jpyConfig);

        String configPath = "feature_save_config.json";
        if(args.length > 0)
            configPath = args[0];

        //load config from file "config.json" in current directory
        FeatureSaveConfig baseConfig = new FeatureSaveConfig();
        Gson gson = new Gson();
        try
        {
            baseConfig = gson.fromJson(FileHelper.readString(configPath), FeatureSaveConfig.class);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
        }
        //reset log output stream to log file
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

        //save url columnFamily
        ISaveFeature is = new SaveFeatureImpl(baseConfig.hbaseQuorum, baseConfig.hbasePort, 
        		baseConfig.hbaseTable_url, baseConfig.hbaseColumnFamilies_url, baseConfig.hbaseColumns_url);
        //save hash-url for fast selection
        ISaveFeature is2 = new SaveFeatureImpl(baseConfig.hbaseQuorum, baseConfig.hbasePort, 
        		baseConfig.hbaseTable_hash, baseConfig.hbaseColumnFamilies_hash, baseConfig.hbaseColumns_hash);
        
        //construct topology builder
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(URL_SPOUT, new KafkaSpout(spoutConfig),baseConfig.urlSpoutParallel);
        builder.setBolt(PARSE_BOLT,new ParseBolt(baseConfig.logDir),baseConfig.parseBoltParallel).shuffleGrouping(URL_SPOUT);
        builder.setBolt(EXTRACT_BOLT, new FeatureExtractBolt(baseConfig.logDir),baseConfig.featureExtractBoltParallel).shuffleGrouping(PARSE_BOLT);
        builder.setBolt(SAVE_BOLT, new SaveFeatureBolt(is, is2,baseConfig.logDir),baseConfig.saveFeatureBoltParallel)
        .fieldsGrouping(EXTRACT_BOLT, new Fields("url"));
        
        //submit topology
        Config conf = new Config();
        conf.put("jpyConfig",baseConfig.jpyConfig);
        conf.put("pythonStartPath",baseConfig.pythonStartPath);
        conf.put("pythonModuleName",baseConfig.pythonModuleName);
        conf.put("pythonMethodName",baseConfig.pythonMethodName);
        conf.put("batchSize",baseConfig.batchSize+"");
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
