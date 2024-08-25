package ir.metrix.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MessageConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class.getName());

    private final MessageProcessorService service;

    MessageConsumer(MessageProcessorService service) {
        this.service = service;
    }

    //TODO: metric for throughput (to show how many message per seconds we can handle)
    @KafkaListener(
            topics = "${topic.name}",
            concurrency = "${partition.count}"
//            batch = "true"
    )
    public void consume(Message message) {

        this.service.processMessage(message);

    }

}
