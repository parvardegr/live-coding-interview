package ir.metrix.interview;

public class Message {
    private String userId;
    private String content;

    Message() {
    }

    Message(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
