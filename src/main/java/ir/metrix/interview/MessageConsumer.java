package ir.metrix.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: maximise the consumer performance in terms of how many message it can process per second
@Component
public class MessageConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class.getName());

    private final MessageProcessorService service;

    MessageConsumer(MessageProcessorService service) {
        this.service = service;
    }

    //TODO: metric for throughput (to show how many message per seconds we can handle)
    @KafkaListener(
            topics = "${topic.name}"
    )  //TODO: Adjust kafka listener configuration
    public void consume(Message message) {  //TODO: use batch poll

        this.service.processMessage(message);

    }

}
