package ir.metrix.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class.getName());

    @Value("${message.count}")
    private int messageCount;
    private final AtomicLong startTime = new AtomicLong(-1);
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final Object lock = new Object();

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

        synchronized (lock) {
            if (processedCount.get() == 0) {
                startTime.set(System.currentTimeMillis());
                LOG.info("Start Processing...");
            }

            if (processedCount.addAndGet(messages.size()) >= messageCount) {
                long elapsedTime = System.currentTimeMillis() - startTime.get();
                LOG.info("Finish Processing Messages. count={}, elapsedTime={} ms", processedCount.get(), elapsedTime);
            }
        }
    }

}
