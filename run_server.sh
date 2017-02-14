#!/bin/bash
# -*- coding: UTF-8 -*-

set -x

if [[ $1 == "-vg" ]]
then
	echo "start VideoGrabTopology named" $2
	storm jar /home/hadoop/VideoGrab/VideoGrab.jar com.xrr.VideoGrab videograb_config.json $2
elif [[ $1 == "-sf" ]]
then
	echo "start SaveFeatureTopology named" $2
	storm jar /home/hadoop/VideoGrab/VideoGrab.jar com.xrr.FeatureSave feature_save_config.json $2
elif [[ $1 == "-s" ]]
then
	echo "start SearchTopology named" $2
	storm jar /home/hadoop/VideoGrab/VideoGrab.jar com.xrr.Search search_config.json $2
elif [[ $1 == "-create" ]]
then
	echo "create kafka Topic named" $2
	kafka-topics.sh --create --zookeeper zk01:2181,zk02:2181,zk03:2181/kafka --replication-factor 2 --partition 2 --topic $2
elif [[ $1 == "-send" ]]
then
	echo "Send KafkaMessage to topic named" $2
	kafka-console-producer.sh --broker-list zk01:9092,zk02:9092,zk03:9092 --topic $2
elif [[ $1 == "-consume" ]]
then
	echo "Consume KafkaMessage of topic named" $2
	kafka-console-consumer.sh --zookeeper zk01:2181,zk02:2181,zk03:2181/kafka --topic $2 --from-beginning
elif [[ $1 == "-delete" ]]
then
	echo "DeleteTopic named" $2
	kafka-topics.sh --delete --zookeeper zk01:2181,zk02:2181,zk03:2181/kafka --topic $2
elif [[ $1 == "-list" ]]
then
	echo "view topics..."
	kafka-topics.sh --list --zookeeper zk01:2181,zk02:2181,zk03:2181/kafka
elif [[ $1 == '-ul' ]]
then
	echo "upload file to server"
	scp /home/raorao/workspace/VideoGrab/*.json hadoop@zk02:/home/hadoop/VideoGrab
	scp /home/raorao/workspace/VideoGrab/help.txt hadoop@zk02:/home/hadoop/VideoGrab
	scp /home/raorao/workspace/VideoGrab/run_server.sh hadoop@zk02:/home/hadoop/VideoGrab
	#scp /home/raorao/workspace/VideoGrab/target/ever-1.0-SNAPSHOT.jar hadoop@zk02:/home/hadoop/VideoGrab/VideoGrab.jar
	scp /home/raorao/workspace/VideoGrab/classes/artifacts/ever_jar/ever.jar hadoop@zk02:/home/hadoop/VideoGrab/VideoGrab.jar
elif [[ $1 == '-dl' ]]
then
	echo "download file from server"
	scp hadoop@zk02:/home/hadoop/VideoGrab/*.json /home/raorao/workspace/VideoGrab
	scp hadoop@zk02:/home/hadoop/VideoGrab/help.txt /home/raorao/workspace/VideoGrab
	scp hadoop@zk02:/home/hadoop/VideoGrab/run_server.sh /home/raorao/workspace/VideoGrab/run_server.sh
	scp hadoop@zk02:/home/hadoop/VideoGrab/VideoGrab.jar /home/raorao/workspace/VideoGrab/target/ever-1.0-SNAPSHOT.jar
elif [[ $1 == '-dlog' ]]
then
	echo "download log file from server"
	scp hadoop@zk02:/home/hadoop/VideoGrab/logs/* /home/raorao/workspace/VideoGrab/logs
else
	echo "Usage:"
	echo "-vg topology_name"
	echo "	start VideoGrabTopology"
	echo "-rebuild"
	echo "	clean and install project using mvn"
	echo "-create topic_name"
	echo "	create kafka topic"
	echo "-send topic_name"
	echo "	send message to this specifide topic"
	echo "-consume topic_name"
	echo " consume message..."
	echo "-delete topic_name"
	echo "	delete kafkatopic"
	echo "-list"
	echo "	view all kafka topics"
	echo "-ul"
	echo "	upload files to server"
	echo "-dl"
	echo "	download files from server"
fi

