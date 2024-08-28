package ir.metrix.interview;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = { "${test.topic.name:test}" })
class InterviewApplicationTests {

	@Value("${test.topic.name:test}")
	private String topic;

	@Value("${test.spring.kafka.consumer.max-poll-records:468}")
	private int pollSize;

	@Value("${test.message.count:468}")
	private int messageCount;

	@Autowired
	private KafkaTemplate<String, Message> kafkaTemplate;

	@MockBean
	private SmsService smsService;

	@MockBean
	private UserService userService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testMessageProcessor() throws InterruptedException {
		Message message = new Message("user0", "just another message!");
		kafkaTemplate.send(topic, message);
		Thread.sleep(5000);
		Mockito.verify(userService, Mockito.times(1)).fetchPhoneById("user0");
		Mockito.verify(smsService, Mockito.times(1)).sendMessages(
				Mockito.isNull(),
				Mockito.argThat(messages ->
						messages.get(0).getContent().equals(message.getContent()) &&
						messages.get(0).getUserId().equals(message.getUserId())
				)
		);
		kafkaTemplate.flush();
	}

	@Test
	void testBatchProcessing() throws InterruptedException {
		for (int i = 0; i < messageCount; i++) {
			Message message = new Message("user0", "just another message!");
			kafkaTemplate.send(topic, message);
		}
		Thread.sleep(5000);
		Mockito.verify(userService, Mockito.times(1)).fetchPhoneById("user0");
		Mockito.verify(smsService, Mockito.times(1)).sendMessages(
				Mockito.isNull(),
				Mockito.argThat(messages -> messages.size() == pollSize)
		);
		kafkaTemplate.flush();
	}
}
