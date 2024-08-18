package ir.metrix.interview;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsService {

    public void sendMessage(String phoneNumber, Message message) {
        try {
            Thread.sleep(1000);  //1s
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: adopt to use this method instead
    public void sendMessages(String phoneNumber, List<Message> messages) {
        try {
            int i = messages.size() / 100;
            int wait = (i + 1) * 1000;
            Thread.sleep(wait);  // ~1s for each 100 messages
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
