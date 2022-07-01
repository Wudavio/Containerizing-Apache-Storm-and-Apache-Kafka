###### tags: `Documentation`

# Containerizing Apache-Storm and Apache-Kafka

## 介紹
Apache Storm是一個分散式計算框架，適合處理即時的資料流，並具有容錯機制(保證Message被處理)

```
Apache Storm Cluster的工作原理，是由Client提交Topology至Nimbus，Nimbus將Jar發送至所有的Supervisor節點，
當所有的Supervisor接收完Jar包括，Nimbus將任務發布Zookeeper，Supervisor收到Zookeeper分派的任務後，
啟動Work，Work啟動Executor，Executor執行Task(任務)，而Storm UI則負責監控整個Cluster。
```
本專案實作容器化的Apache Storm並將Spout串接Apache Kafka

以下是本專案實現流程(Database並未實作)

![2](https://user-images.githubusercontent.com/40749259/176949683-cb66d341-accc-47e3-868d-f247a7b3aea8.JPG)

**使用前將設定檔與程式碼中的IP換成欲部署的IPv4，並參考以下環境設定。**

## 環境需求
```
Docker version 20.10.14, build a224086

openjdk 11.0.15 2022-04-19
OpenJDK Runtime Environment (build 11.0.15+10-Ubuntu-0ubuntu0.20.04.1)
OpenJDK 64-Bit Server VM (build 11.0.15+10-Ubuntu-0ubuntu0.20.04.1, mixed mode, sharing)

apache-maven-3.8.6
apache-storm-2.3.0 <-from docker-compose
zookeeper
```
## Apache Maven
### 安裝
```
wget https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.zip
unzip apache-maven-3.8.6-bin.zip
```

### 為Maven新增全域變數
打開 ~/.bashrc 並增加以下內容：
```
export MAVEN_HOME="/apache_maven_path/apache-maven-3.8.6"
export PATH=$PATH:$MAVEN_HOME/bin
```
使全域變數生效
```
source .bashrc
```

### 修改設定檔案
修改**MainExample/config.xml**中的**your_host_ip**
```xml==
<!-- quantity of nimbus -->
nimbus.num: 1
<!-- nimbus IP address -->
nimbus.servers: your_host_ip

<!-- quantity of zookeeper -->
zookeeper.num: 1

<!-- zookeeper IP address -->
zookeeper.servers: your_host_ip

<!-- quantity of Worker -->
worker.num: 1

<!-- Worker RAM -->
worker.ram: 4096
```

### 修改程式碼中KafkaIP
修改**MainExample/src/main/java/KafkaMainProducer.java**中的your_host_ip
```java=
props.put("bootstrap.servers", "中的your_host_ip:9092");
```
修改**MainExample/src/main/java/KafkaMainComsumer.java**中的your_host_ip
```java=
private final static String BOOTSTRAP_SERVERS = "your_host_ip:9092";
```

### 編譯並打包程式碼
進入程式碼主目錄
```
cd MainExample
```
編譯並打包
```
mvn clean package
```
打包後會在MainExample底下出現target資料夾
可以找到整個Topology的主要Jar檔案**MainExample-1.0-SNAPSHOT.jar**

## 部署容器

### Kafka Dokcer-compose
打開MainExample中的Kafka資料夾
```
cd MainExample/kafka
```

修改**kafka/docker-compose.yml**中的**your_host_ip**
```yaml=
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://yoru_host_ip:9092,PLAINTEXT_INTERNAL://broker:29092
```

執行Apache Kafka
```
docker-compose up -d
```
### Storm Dokcer-compose

修改**storm.yaml**中的**your_host_ip**
```yaml=
storm.zookeeper.servers:
 - "your_host_ip"

nimbus.seeds: ["your_host_ip"]
storm.local.hostname: "your_host_ip"
```

進入根目錄並執行Apache Storm
```
docker-compose up -d
```
