package ir.metrix.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageProcessorService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorService.class.getName());

    @Value("${message.count}")
    private int messageCount;
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicLong startTime = new AtomicLong(-1);
    private final Object lock = new Object();

    private final UserService userService;
    private final SmsService smsService;

    MessageProcessorService(UserService userService, SmsService smsService) {
        this.userService = userService;
        this.smsService = smsService;
    }

    public void processMessage(Message message) {
        String phoneNumber = userService.fetchPhoneById(message.getUserId());
        smsService.sendMessage(phoneNumber, message);

        synchronized (lock) {
            if (processedCount.get() == 0) {
                startTime.set(System.currentTimeMillis());
                LOG.info("Start Processing...");
            }

            if (processedCount.incrementAndGet() >= messageCount) {
                long elapsedTime = System.currentTimeMillis() - startTime.get();
                LOG.info("Finish Processing Messages. count={}, elapsedTime={} ms", processedCount.get(), elapsedTime);
            }
        }
    }

}
