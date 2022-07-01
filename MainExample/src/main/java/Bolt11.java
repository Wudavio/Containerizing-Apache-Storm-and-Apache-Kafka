import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.topology.BasicOutputCollector;


import java.util.HashMap;
import java.util.Map;
import org.apache.storm.topology.base.BaseBasicBolt;

public class Bolt11 extends BaseBasicBolt {

    Map<String,Integer> map = new HashMap<String,Integer>();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        try {
            String msg = tuple.getString(0);
            String bolt11Msg = msg + "AAA";
            System.out.println(bolt11Msg);
            basicOutputCollector.emit(new Values(bolt11Msg));
        } catch(Exception e) {}
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("bolt11Msg"));//定義傳給下一個bolt的欄位描述
    }

    /**
     * topology結束時執行
     */
    @Override
    public void cleanup() {

    }
}
