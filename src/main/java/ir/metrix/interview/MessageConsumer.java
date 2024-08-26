package ir.metrix.interview;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageConsumer {

    private final MessageProcessorService service;

    MessageConsumer(MessageProcessorService service) {
        this.service = service;
    }

    //TODO: metric for throughput (to show how many message per seconds we can handle)
    @KafkaListener(
            topics = "${topic.name}",
            concurrency = "${partition.count}",
            batch = "true"
    )
    public void consume(List<Message> messages) {

        this.service.processMessages(messages);

    }

}
