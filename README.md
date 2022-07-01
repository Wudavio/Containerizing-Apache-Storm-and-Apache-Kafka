###### tags: `Documentation`

# Containerizing Apache-Storm and Apache-Kafka

:::success

:bookmark: 書籤

[TOC]

:::

## 介紹
實作容器化的Apache Storm並將Spout串接Kafka

:::danger
使用前將設定檔與程式碼中的IP換成欲部署的IPv4，並參考以下環境設定。
:::
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

## 佈署容器

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