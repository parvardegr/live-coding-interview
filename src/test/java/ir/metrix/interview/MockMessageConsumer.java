package ir.metrix.interview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockMessageConsumer {

    @Autowired
    private MessageProcessorService service;

    @KafkaListener(
            topics = "${test.topic.name:test}",
            concurrency = "${partition.count}",
            batch = "true"
    )
    public void consume(List<Message> messages) {
        this.service.processMessages(messages);
    }
}
