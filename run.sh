#!/bin/bash
# -*- coding: UTF-8 -*-

set -x

if [[ $1 == "-a" ]]
then
	echo "start AnalyzerTopology named" $2
	storm jar target/ever-1.0-SNAPSHOT.jar com.persist.VideoAnalyzer analyzer_config.json $2
elif [[ $1 == "-d" ]]
then
	echo "start DetectTopology named" $2
	storm jar Search.jar com.persist.DetectTopology detect_config.json $2
elif [[ $1 == "-g" ]]
then
	echo "start GrabTopology named" $2
	storm jar target/ever-1.0-SNAPSHOT.jar com.persist.VideoGrabber grabber_config.json $2
elif [[ $1 == "-vg" ]]
then
	echo "start VideoGrabTopology named" $2
	storm jar target/ever-1.0-SNAPSHOT.jar com.xrr.VideoGrab videograb_config.json $2
elif [[ $1 == "-sf" ]]
then
	echo "start SaveFeatureTopology named" $2
	storm jar target/ever-1.0-SNAPSHOT.jar com.xrr.FeatureSave feature_save_config.json $2
elif [[ $1 == "-ic" ]]
then
	echo "start ImageCheck ServiceTopology named" $2
	storm jar target/ever-1.0-SNAPSHOT.jar com.persist.ImageCheck check.json $2
elif [[ $1 == "-iq" ]]
then
	echo "start ImageQuery ..."
	storm jar target/ever-1.0-SNAPSHOT.jar com.persist.ImageQuery localhost 3772 image-check url_file.txt
elif [[ $1 == "-rebuild" ]]
then	echo "------rebuild this project..."
	mvn clean install -DskipTests=true
	mvn compile
	mvn package
elif [[ $1 == "-pack" ]]
then
	echo "------pack this project to jar..."
	mvn package
elif [[ $1 == "-startKafka" ]]
then
	echo "start kafka service..."
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-server-start.sh config/server.properties&
	cd -
elif [[ $1 == "-stopKafka" ]]
then
	echo "stop kafka service..."
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-server-stop.sh config/server.properties
	cd -
elif [[ $1 == "-create" ]]
then
	echo "create kafka Topic named" $2
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic $2
	cd -
elif [[ $1 == "-send" ]]
then
	echo "Send KafkaMessage to topic named" $2
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-console-producer.sh --broker-list localhost:9092 --topic $2
elif [[ $1 == "-consume" ]]
then
	echo "Consume KafkaMessage of topic named" $2
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic $2 --from-beginning
elif [[ $1 == "-deleteTopic" ]]
then
	echo "DeleteTopic named" $2
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic $2
	cd -
elif [[ $1 == "-listTopic" ]]
then
	echo "view topics..."
	cd /opt/software/kafka_2.11-0.10.0.0
	bin/kafka-topics.sh --list --zookeeper localhost:2181
	cd -
else
	echo "Usage:"
	echo "-g topology_name"
	echo "	start GrabTopology"
	echo "-a topology_name"
	echo "	start AnalyzerTopology"
	echo "-d topology_name"
	echo "	start DetectTopology"
	echo "-ic topology_name"
	echo "	start ImageCheck service"
	echo "-iq"
	echo "	execute ImageQuery client"
	echo "-rebuild"
	echo "	clean and install project using mvn"
	echo "-pack"
	echo "	pack the project to jar file"
	echo "-create topic_name"
	echo "	create kafka topic"
	echo "-send topic_name"
	echo "	send message to this specifide topic"
	echo "-consume topic_name"
	echo " consume message..."
	echo "-deleteTopic topic_name"
	echo "	delete kafkatopic"
	echo "-listTopic"
	echo "	view all kafka topics"
	
fi

