项目需求：

- 从视频文件、实时视频流、摄像头抓取图片
- 利用图像分割算法对图片进行分割，得到一张张图片和对应的特征，图片保存在hdfs中，特征保存在hbase中。（算法由孙浩完成，直接在java中调用函数就可以）

---

### VideoGrab Topology主要组件

- KafkaSpout，接受kafka消息请求
- ResolveBolt，解析json，将相同url消息发至同一GrabBolt
- GrabBolt，控制抓取子进程：启动，暂停，继续，停止,并且将分割之后的局部图片保存在hdfs中，将照片的特征信息发送到kafka消息队列中

### 运行：
``` bash
> $ mvn clean compile package //先用maven进行编译，打包
> $ storm jar target/ever-1.0-SNAPSHOT.jar com.xrr.VideoGrab videograb_config.json topology_name //这个地方注意storm的启动目录（此命令在项目根目录）
```

### 可用配置(videograb_config.json，必选项前有(!)，可选项前有(defaultValue))：
- (1)urlSpoutParallel，int，KafkaSpout并行度
- (2)resolveBoltParallel，int， ResolveBolt并行度
- (3)grabBoltParallel，int，GrabBolt并行度
- (!)grabLimit，int，抓取子进程数量上限
- (!)cmd，string，启动抓取子进程的shell命令(注意jar路径和最后的空格)：“storm jar target/ever-1.0-SNAPSHOT.jar com.xrr.VideoGrabThread”
- (!)zks，string，kafka consumer的zookeeper地址(如"zk01:2181,zk02:2181")
- (!)topic，string，抓取视频接收的kafka topic
- (!)zkRoot，string，kafka消息存放位置的标识
- (!)id，string，kafka consumer的id
- (!)zkServers，string[]，zookeeper集群(如["zk01", "zk02"])
- (!)zkPort，int，zookeeper端口
- (!)brokerList，string，发送kafka消息的broker list(如"zk01:9092,zk02:9092")
- (!)sendTopic，string，抓帧后发送图片信息的topic
- (!)redisHost，string，redis主机名或ip地址
- (!)redisPort，int，redis端口
- (!)redisPassword，string，redis密码
- (1.0)frameRate，double，抓取帧率(如0.5 - 每2s抓一帧)
- (3)workerNum，int，worker数量，即进程数量

---

## FeatureSave Topology主要组件
 - KafkaSpout， 接受kafka消息请求
 - ParseBolt， 解析json数据，将相同的url的消息发送给同一个SaveFeatureBolt
 - SaveFeatureBolt， 保存每张图片的url，video_id，feature等信息

### 运行
``` bash
$ storm jar target/ever-1.0-SNAPSHOT.jar com.xrr.FeatureSave feature_save_config.json topology_name
```

### 可用配置（feature_save_config.json）
- (1)urlSpoutParallel，int，KafkaSpout并行度
- (2)parseBoltParallel，int， ParseBolt并行度
- (3)saveFeatureBoltParallel，int，saveFeatureBolt并行度
- (4)zks，String，kafka consumer的zookeeper地址(如"localhost:2181")
- (5)topic，String，kafkaSpout 接受消息的topic名字（必须和videograb_config.json 中的sendTopic字段值相同）
- (6)zkRoot，string，kafka消息存放位置的标识
- (7)id，string，kafka consumer的id
- (8)zkServers，string[]，zookeeper集群(如["zk01", "zk02"])
- (9)zkPort，int，zookeeper端口
- (10)brokerList，string，发送kafka消息的broker list(如"zk01:9092,zk02:9092")
- (11)workerNum，int，worker数量，即进程数量
- (12)hbaseQuorum，string，hbase数据库的host ip
- (13)hbasePort，int，hbase数据库的端口
- (14)hbaseTable，string，将要存入数据的表名
- (15)hbaseColumnFamily，string，表的列族名
- (16)hbaseColumns，String[]，表的列名
---

### 项目依赖：
> * apache-storm-0.9.6
> * hadoop-2.6.4
> * hbase-1.1.5
> * kafka_2.11-0.10.0.0
> * zookeeper-3.4.8

### 注意事项：

> - 使用mave编译程序之前，将maven的安装路径bin目录加入到系统PATH环境变量中
> - 安装完项目依赖后，依次启动zookeeper，storm(**注意在项目工作目录下启动storm，否则需要修改videograb_config文件中的cmd命令**，先启动grab topology，然后启动featureSave topology)，kafka（注意创建好两个topic，然后启动grab topology的kafka producer）
> - 注意根据自己本机的安装目录，重修配置videograb_config.json和feature_save_config.json这两个配置文件
> - 根据本机环境修改run.sh文件，可以方便执行命令
