package ir.metrix.interview;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@SpringBootApplication
public class InterviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewApplication.class, args);
	}

	@Value("${topic.name}")
	private String topicName;
	@Value("${message.count}")
	private int messageCount;
	@Value("${partition.count}")
	private int partitionCount;
	@Value("${replica.factor}")
	private int replicaFactor;

	@Bean
	public NewTopic topic() {
		return TopicBuilder
				.name(topicName)
				.partitions(partitionCount)
				.replicas(replicaFactor)
				.build();
	}

	@Bean
	@Profile("!test")
	public ApplicationRunner runner(KafkaTemplate<String, Message> template) {
		return args -> {
			int count = messageCount;
			for (int i = 0; i < count; i++) { //we have only 2 users
				String key = "user" + (i % 2);

				// Calculate hash of the key to determine the partition
				int partition = Math.abs(key.hashCode()) % partitionCount;

				Message message = new Message(key, "just a message!");
				ProducerRecord<String, Message> record = new ProducerRecord<>(topicName, partition, key, message);
				template.send(record);
			}
		};
	}

}
