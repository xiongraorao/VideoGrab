## VideoGrab

项目需求：

- 从视频文件、实时视频流、摄像头抓取图片
- 利用图像分割算法对图片进行分割，得到一张张图片和对应的特征，图片保存在hdfs中，特征保存在hbase中。（算法由杨帆完成，直接在java中调用函数就可以）

---

Topology主要组件

Video Grab Topology:

- KafkaSpout，接受kafka消息请求
- ResolveBolt，解析json，将相同url消息发至同一GrabBolt
- GrabBolt，控制抓取子进程：启动，暂停，继续，停止,并且将分割之后的局部图片保存在hdfs中，将照片的特征信息发送到kafka消息队列中

Feature save Topolgy:

- KakfaSpout,接受kafka消息请求
- RecordBolt,将照片的特征信息保存在hbase中

---


## 运行：
``` bash
$ mvn clean compile package
$ bin/storm jar target/ever-1.0-SNAPSHOT.jar com.xrr.VideoGrab videograb_config.json topology_name
```

## 可用配置(grabber_config.json，必选项前有(!)，可选项前有(defaultValue))：
> * (1)urlSpoutParallel，int，KafkaSpout并行度
> * (3)resolveBoltParallel，int， ResolveBolt并行度
> * (3)grabBoltParallel，int，GrabBolt并行度
> * (!)grabLimit，int，抓取子进程数量上限
> * (!)cmd，string，启动抓取子进程的shell命令(注意jar路径和最后的空格)：“java -Djava.ext.dirs=$STORM_HOME/lib/ -cp Video.jar com.persist.GrabThread ”
> * (!)zks，string，kafka consumer的zookeeper地址(如"zk01:2181,zk02:2181")
> * (!)topic，string，抓取视频接收的kafka topic
> * (!)zkRoot，string，kafka消息存放位置的标识
> * (!)id，string，kafka consumer的id
> * (!)zkServers，string[]，zookeeper集群(如["zk01", "zk02"])
> * (!)zkPort，int，zookeeper端口
> * (!)brokerList，string，发送kafka消息的broker list(如"zk01:9092,zk02:9092")
> * (!)sendTopic，string，抓帧后发送图片信息的topic
> * (!)redisHost，string，redis主机名或ip地址
> * (!)redisPort，int，redis端口
> * (!)redisPassword，string，redis密码
> * (1.0)frameRate，double，抓取帧率(如0.5 - 每2s抓一帧)
> * (3)workerNum，int，worker数量，即进程数量

---
注意事项：

> - storm在项目工作目录下启动

---