package ir.metrix.interview;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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

    private final MeterRegistry meterRegistry;
    private final Timer messageProcessingTimer;

    MessageConsumer(MessageProcessorService service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
        this.messageProcessingTimer = Timer.builder("kafka.message.processing.time")
                .description("Time taken to process messages")
                .tags("topic", "interview")
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = "${topic.name}",
            concurrency = "${partition.count}",
            batch = "true"
    )
    public void consume(List<Message> messages) {
        // todo: process the instrumentation on another thread
        meterRegistry.counter("kafka.message.received.batch", "topic", "interview").increment(); // batch
        meterRegistry.counter("kafka.message.received", "topic", "interview").increment(messages.size()); // message

        Timer.Sample sample = Timer.start(meterRegistry); // start process time
        this.service.processMessages(messages);
        sample.stop(messageProcessingTimer); // end process time

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
