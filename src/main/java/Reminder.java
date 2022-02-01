

public class Reminder {

    private long id;
    private String name;
    private Long dateMillis;
    private Long chatId;


    public Long getChatId() {return chatId;}

    public void setChatId(Long chatId) {this.chatId = chatId;}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(Long dateMillis) {
        this.dateMillis = dateMillis;
    }



}
