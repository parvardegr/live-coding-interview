package ir.metrix.interview;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String fetchPhoneById(String userId) {
        try {
            Thread.sleep(500);  //0.5s
            return userId;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
