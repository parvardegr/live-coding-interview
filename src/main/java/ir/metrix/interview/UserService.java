package ir.metrix.interview;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${operation.delay}")
    private int operationDelay;

    public String fetchPhoneById(String userId) {
        try {
            Thread.sleep(operationDelay);  //0.5s
            return userId;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
