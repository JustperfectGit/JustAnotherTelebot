import java.util.ArrayList;

public class User {
    private long id;
    private BotStates state;
    public ArrayList <Reminder> userReminders = new ArrayList<>();
    public ArrayList <MyTimer> userTimers = new ArrayList<>();
    private Reminder reminder;

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BotStates getState() {
        return state;
    }

    public void setState(BotStates state) {
        this.state = state;
    }

    public User(long id, BotStates state) {
        this.id = id;
        this.state = state;
    }
}
