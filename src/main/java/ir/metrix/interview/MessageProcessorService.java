package ir.metrix.interview;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageProcessorService {

    private final UserService userService;
    private final SmsService smsService;

    MessageProcessorService(UserService userService, SmsService smsService) {
        this.userService = userService;
        this.smsService = smsService;
    }

    public void processMessage(Message message) {
        String phoneNumber = userService.fetchPhoneById(message.getUserId());
        smsService.sendMessage(phoneNumber, message);
    }

    public void processMessages(List<Message> messages) {
        String phoneNumber = userService.fetchPhoneById(messages.get(0).getUserId());
        smsService.sendMessages(phoneNumber, messages);
    }

}
