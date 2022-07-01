import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class MainControl {

    public static String jarPath = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {

        int i = 0, j = 0, workerNum = 0, workerRAM = 0;
        String[] nimbusList = new String[1];
        String[] zookeeperList = new String[1];

        FileReader fr = new FileReader(jarPath + "/config.xml");
        BufferedReader br = new BufferedReader(fr);

        // 讀取 nimbus & zookeeper 數量與 IP
        while(br.ready()) {
            String info = br.readLine();
            if(!info.contains("!")) {
                if(info.indexOf("nimbus.num") != -1) {
                    String[] nimbus = info.split(": ");
                    nimbusList = new String[Integer.valueOf(nimbus[1])];

                } else if(info.indexOf("nimbus.servers") != -1) {
                    String[] nimbusIP = info.split(": ");
                    nimbusList[i] = nimbusIP[1];
                    i++;

                } else if(info.indexOf("zookeeper.num") != -1) {
                    String[] zookeeper = info.split(": ");
                    zookeeperList = new String[Integer.valueOf(zookeeper[1])];

                } else if(info.indexOf("zookeeper.servers") != -1) {
                    String[] zookeeperIP = info.split(": ");
                    zookeeperList[j] = zookeeperIP[1];
                    j++;

                } else if(info.indexOf("worker.num") != -1) {
                    String[] worker = info.split(": ");
                    workerNum = Integer.valueOf(worker[1]);

                } else if(info.indexOf("worker.ram") != -1) {
                    String[] worker = info.split(": ");
                    workerRAM = Integer.valueOf(worker[1]);
                }
            }
        }

        // 設定 Topology
        Config config = new Config();
        //config.setDebug(true);

        // 通常情况下 spout 的發射速度會快於下游的 bolt 的處理速度，當下游的 bolt 還有 TOPOLOGY_MAX_SPOUT_PENDING 個 tuple 没有處理完時，spout 會停下來等待，該配置作用於 spout 的每個 task。
        config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 10000);
        // 調整分配給每個 worker 的記憶體
        config.put(Config.WORKER_HEAP_MEMORY_MB, workerRAM);
        // 配置 nimbus 連接 ip 地址，比如：192.168.10.1
        config.put(Config.NIMBUS_SEEDS, Arrays.asList(nimbusList));

        System.out.println("nimbusList: " + nimbusList[0]);
        // config.put(Config.NIMBUS_SEEDS, Arrays.asList(new String[]{"192.168.xxx.xxx"}));
        // 配置 nimbus 連接 port，預設 6627
        config.put(Config.NIMBUS_THRIFT_PORT, 6627);
        // 配置 zookeeper 連接 ip 地址，可以使用集合存放多個
        config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(zookeeperList));
        // config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[]{"120.119.xxx.xxx", "192.168.xxx.xxx"}));

        // 配置 zookeeper 連接 port，預設 2181
        config.put(Config.STORM_ZOOKEEPER_PORT, 2181);

        // 設定 Worker 數量
        config.setNumWorkers(workerNum);

        // 建立 Topology
        TopologyBuilder builder = new TopologyBuilder();

        // 設定 Spout 為哪個執行檔，設定平行執行數量
        builder.setSpout("inputReader", new SpoutStream());
        builder.setBolt("Bolt11", new Bolt11()).localOrShuffleGrouping("inputReader");
        builder.setBolt("Bolt12", new Bolt12()).localOrShuffleGrouping("inputReader");
        builder.setBolt("Bolt21", new Bolt21())
            .localOrShuffleGrouping("Bolt11")
            .localOrShuffleGrouping("Bolt12");

        if(args != null && args.length > 0) {
            // 這裡print會出現在Dokcer-compose up
            for (int indexArgs = 0; indexArgs < args.length; indexArgs++) {
                System.out.println("args[" + indexArgs + "]" + args[indexArgs]);
            }
            /* 使用 StormSubmitter 提交拓樸時，需要將所需的 jar 提交到 nimbus 上去，
               如果不指定 jar 文件路徑，storm 默認會使用 System.getProperty("storm.jar") */
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());

            KafkaMainProducer producer1 = new KafkaMainProducer();
            producer1.producerSendMessage();
        } else {
            try {
                System.out.println("LocalCluster");
                LocalCluster cluster = new LocalCluster();
                cluster.submitTopology("MainControl", config, builder.createTopology());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
