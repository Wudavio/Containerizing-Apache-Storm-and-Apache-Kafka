import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaMainProducer {
	private Producer<String, String> producer;

    public KafkaMainProducer() {
        Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.137.63:9092");
		props.put("acks", "all");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.producer = new KafkaProducer<String, String>(props);
    }

	public void producerSendMessage() {
		for (int i = 0; i < 100; i++) {
			long time = System.currentTimeMillis();
			this.producer.send(new ProducerRecord<String, String>("exampleTopicName", ""+i, ""+time));
		}
		this.producer.close();
	}
}
