import java.util.Timer;

public class MyTimer extends Timer {

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    private long chatId;

    public MyTimer(long chatId) {
        this.chatId = chatId;
    }

}
