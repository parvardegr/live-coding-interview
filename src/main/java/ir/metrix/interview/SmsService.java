package ir.metrix.interview;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsService {

    @Value("${operation.delay}")
    private int operationDelay;

    public void sendMessage(String phoneNumber, Message message) {
        try {
            Thread.sleep(operationDelay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessages(String phoneNumber, List<Message> messages) {
        try {
            int i = messages.size() / 100;
            int wait = (i + 1) * operationDelay;
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
