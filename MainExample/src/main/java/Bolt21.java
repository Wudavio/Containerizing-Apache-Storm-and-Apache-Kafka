import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.BasicOutputCollector;
public class Bolt21 extends BaseBasicBolt {

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        // 取得 bolt 資料
        String sourceComponent =tuple.getSourceComponent();
        String msg = tuple.getString(0);
        System.out.println(sourceComponent+" to bolt21: " + msg + "CCC");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    /**
     * topology結束時執行
     */
    @Override
    public void cleanup() {

    }
}
