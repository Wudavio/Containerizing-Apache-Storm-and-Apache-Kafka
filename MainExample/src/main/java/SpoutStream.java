import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.topology.base.BaseRichSpout;

import java.util.Map;
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoutStream extends BaseRichSpout {
    private SpoutOutputCollector spoutOutputCollector;
    private ConsumerRecords<String, String> msgList;
    private static int msgNum = 0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector = spoutOutputCollector;
        try {
            System.out.println("=======first======Open");
            KafkaMainConsumer KMC = new KafkaMainConsumer();
        } catch(Exception e) {}
    }

    @Override
    public void nextTuple() {
        try {
            while (true) {
                msgList = KafkaMainConsumer.consumer.poll(Duration.ofMillis(1000));
                if (msgList != null && msgList.count() > 0) {
                    for (ConsumerRecord<String, String> record : msgList) {
                        System.out.println(msgNum+": key = " + record.key() + ", value = " + record.value()+" offset==="+record.offset());
                        String recordMsg = ": key = " + record.key() + ", value = " + record.value();
                        msgNum++;
                        // 傳送資料至 boltSecondPred
                        this.spoutOutputCollector.emit(new Values(recordMsg));
                    }
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("data"));//宣告輸出欄位描述
    }
}
