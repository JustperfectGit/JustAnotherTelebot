
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class TelegramBot extends TelegramLongPollingBot {

    BotStates botState = BotStates.FREEMINDED;
    private static String CHATID = "660392975";
    DateFormat df = new SimpleDateFormat("HH:mm dd MMMM yyyy");
    DateFormat df2 = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private ArrayList<MyTimer> timers = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();


    @Override
    public String getBotUsername() {
        return "JustAnother_TeleBot";
    }

    @Override
    public String getBotToken() {
        return "5035271549:AAFlnU9nCpBv6474-m4nmMbGWuJknWSs-hE";
    }

    @SneakyThrows
    public static void main(String[] args) {
        TelegramBot telegramBot = new TelegramBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        User temp = null;

        if (update.hasMessage()) {
            for (User u : users) {
                if (u.getId() == update.getMessage().getChatId()) {
                    temp = u;}
            }
            handleMessage(update.getMessage(), temp);
        } else if (update.hasCallbackQuery()) {
            for (User u : users) {
                if (u.getId() == update.getCallbackQuery().getMessage().getChatId()) {
                    temp = u;}
            }
            handleCallbackQuery(update.getCallbackQuery(), temp);
        }
    }

    @SneakyThrows
    public void handleCallbackQuery(CallbackQuery callbackQuery, User user) {

         if (callbackQuery.getData().equals("oneHourDelay")) {

            String str = callbackQuery.getMessage().getText();
            String[] strings = str.split("\n");
            str = strings[1];
            long longDate = new Date().getTime() + 3600000l;
            Date date = new Date(longDate);

            Reminder reminder = new Reminder();
            reminder.setName(str);
            reminder.setDateMillis(date.getTime());

            //проверяем есть ли такое дело в списке, если есть пишем что есть
            for (Reminder r : user.userReminders) {

                if (r.getName().equals(reminder.getName()) && reminder.getDateMillis() + 82800000l > r.getDateMillis()) {
                    execute(SendMessage.builder().
                            text("Вы уже отложили это напоминание").
                            chatId(callbackQuery.getMessage().getChatId().toString()).
                            build());
                    return;
                }
            }

            //если дела в списке нет, добавляем, сообщаем
            user.userReminders.add(reminder);

            execute(SendMessage.builder().
                    text("Напоминание \n" + str + "\nперенесено на\n" + df.format(date)).
                    chatId(callbackQuery.getMessage().getChatId().toString()).
                    build());

            MyTimer timer = new MyTimer(callbackQuery.getMessage().getChatId());
            MyTimerTask myTimerTask = new MyTimerTask(reminder, timer, callbackQuery.getMessage().getChatId());
            timer.schedule(myTimerTask, date.getTime() - new Date().getTime());
            user.userTimers.add(timer);
        } else if (callbackQuery.getData().equals("oneDayDelay")) {

            String str = callbackQuery.getMessage().getText();
            String[] strings = str.split("\n");
            str = strings[1];

            long longDate = new Date().getTime() + 86400000l;
            Date date = new Date(longDate);

            Reminder reminder = new Reminder();
            reminder.setName(str);
            reminder.setDateMillis(date.getTime());

            //проверяем есть ли такое дело в списке, если есть пишем что есть
            for (Reminder r : user.userReminders) {

                if (r.getName().equals(reminder.getName()) && reminder.getDateMillis() > r.getDateMillis()) {
                    execute(SendMessage.builder().
                            text("❗️Вы уже отложили это напоминание").
                            chatId(callbackQuery.getMessage().getChatId().toString()).
                            build());
                    return;
                }
            }

            //если дела в списке нет, добавляем, сообщаем
            user.userReminders.add(reminder);

            execute(SendMessage.builder().
                    text("Напоминание \n" + str + "\n➡️перенесено на\n" + df.format(date)).
                    chatId(callbackQuery.getMessage().getChatId().toString()).
                    build());

            MyTimer timer = new MyTimer(callbackQuery.getMessage().getChatId());
            MyTimerTask myTimerTask = new MyTimerTask(reminder, timer, callbackQuery.getMessage().getChatId());
            timer.schedule(myTimerTask, date.getTime() - new Date().getTime());
            user.userTimers.add(timer);
        }
    }

    //переделать логику, теперь из апдейта захватываем айди юзера
    @SneakyThrows
    public void handleMessage(Message message, User user) {

        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();

            //нужна ли тут проверка состояния бота?
            if (commandEntity.isPresent() && botState == BotStates.FREEMINDED) {
                String command = message
                        .getText()
                        .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                //проверяем по чатАйди, есть ли в списке юзеров наш юзер, если нет, добавляем
                switch (command) {
                    case "/start":
                        new Handler().startHandler(message);
                        for (User u : users) {
                            if (u.getId() == message.getChatId())
                                return;
                        }
                        users.add(new User(message.getChatId(), BotStates.FREEMINDED));
                        return;

                    //остановился тут
                    case "/set_new_reminder":
                        new Handler().setNewReminderHandler(message);
                        setStateForUser(message, BotStates.WAIT_FOR_REMINDER);
                        return;

                    case "/show_all":
                        showAllReminders(user);
                        return;

                    case "/clear":
                        for (int i = 0; i < user.userTimers.size(); i++) {
                            user.userTimers.get(i).cancel();
                        }
                        user.userTimers.removeAll(user.userTimers);
                        user.userReminders.removeAll(user.userReminders);

                        execute(SendMessage.builder().
                                text("Готово").
                                chatId(message.getChatId().toString()).
                                build());
                        return;

                    case "/delete":
                        execute(SendMessage.builder().
                                text("Введите номер дела, которое вы хотели бы удалить").
                                chatId(message.getChatId().toString()).
                                build());
                        setStateForUser(message, BotStates.WAIT_FOR_NUMBER);
                        return;
                }
            }
        } else if (message.getText().equals("Новое напоминание")) {
            new Handler().setNewReminderHandler(message);
            setStateForUser(message, BotStates.WAIT_FOR_REMINDER);
        } else if (message.getText().equals("Показать список напоминаний")) {
            showAllReminders(user);
        }

        //очистка списка дел
        else if (message.getText().equals("Очистить список напоминаний")) {
            for (int i = 0; i < user.userTimers.size(); i++) {
                user.userTimers.get(i).cancel();
            }
            user.userTimers.removeAll(user.userTimers);
            user.userReminders.removeAll(user.userReminders);

            execute(SendMessage.builder().
                    text("Готово").
                    chatId(message.getChatId().toString()).
                    build());
            //Нужна ли установка стейта??
            //botState = BotStates.FREEMINDED;
        } else if (message.getText().equals("Удалить одно из напоминаний")) {
            execute(SendMessage.builder().
                    text("Введите номер дела, которое вы хотели бы удалить").
                    chatId(message.getChatId().toString()).
                    build());
            setStateForUser(message, BotStates.WAIT_FOR_NUMBER);
        } else if (message.getText().equals("Случайный совет")) {
            execute(SendMessage.builder().
                    text(RandomAdvise.getAdvise()).
                    chatId(message.getChatId().toString()).
                    build());
            setStateForUser(message, BotStates.FREEMINDED);
        } else if (message.hasText() && user.getState() == BotStates.WAIT_FOR_REMINDER) {

            Reminder newReminder = new Reminder();
            newReminder.setName(message.getText());
            user.setReminder(newReminder);
            long dateMillis = new Date().getTime() + 86400000l;
            Date date = new Date(dateMillis);
            String dateStr = df2.format(date);

            execute(
                    SendMessage.builder().
                            text("Теперь установите дату и время в правильном формате, например: \n" + dateStr).
                            chatId(message.getChatId().toString()).
                            build());

            setStateForUser(message, BotStates.WAIT_FOR_DATE);
        } else if (message.hasText() && user.getState() == BotStates.WAIT_FOR_DATE) {
            try {
                Date date = convertStringToTime(message.getText());
                if (date.before(new Date())) {
                    execute(SendMessage.builder().
                            text("\uD83D\uDE14Пока я еще не научился делать напоминания в прошлом, попробуйте задать будущую дату.").
                            chatId(message.getChatId().toString()).
                            build());
                } else {
                    user.getReminder().setDateMillis(date.getTime());
                    user.userReminders.add(user.getReminder());

                    execute(SendMessage.builder().
                            text("\uD83D\uDCCD В список дел добавлено дело: \n \"" + user.getReminder().getName() + "\" \n \uD83D\uDCC5 Дата: " + df.format(date) + " года.").
                            chatId(message.getChatId().toString()).
                            build());

                    MyTimer timer = new MyTimer(message.getChatId());
                    MyTimerTask myTimerTask = new MyTimerTask(user.getReminder(), timer, message.getChatId());
                    timer.schedule(myTimerTask, date.getTime() - new Date().getTime());
                    user.userTimers.add(timer);

                    setStateForUser(message, BotStates.FREEMINDED);
                }
            } catch (Exception e) {
                e.printStackTrace();
                execute(SendMessage.builder().
                        text("Дата введена некорректно, попробуйте снова").
                        chatId(message.getChatId().toString()).
                        build());
            }
            //reminderToAdd = null;
        }

        else if (message.hasText() && user.getState() == BotStates.WAIT_FOR_NUMBER) {
            try {
                int i = Integer.parseInt(message.getText()) - 1;
                user.userReminders.remove(i);
                user.userTimers.get(i).cancel();
                user.userTimers.remove(i);
                execute(SendMessage.builder().
                        text("Дело № " + (i + 1) + " успешно удалено").
                        chatId(message.getChatId().toString()).
                        build());
                setStateForUser(message, BotStates.FREEMINDED);

//замена                botState = BotStates.FREEMINDED;
            } catch (Exception e) {
                e.printStackTrace();
                execute(SendMessage.builder().
                        text("Неверное значение").
                        chatId(message.getChatId().toString()).
                        build());
            }
        } else if (message.hasText()) {
            execute(SendMessage.builder().
                    text("Эта команда мне не понятна. \uD83D\uDE1E").
                    chatId(message.getChatId().toString()).
                    build());
        }
    }

    //преобразуем стринг дату от пользователя в дату
    public Date convertStringToTime(String dateTime) {
        String[] dateTimeArray = dateTime.split(" ");
        String[] time = dateTimeArray[0].split(":");
        String[] date = dateTimeArray[1].split("\\.");

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));

        Date dateToShow = cal.getTime();
        return dateToShow;
    }

    //Создаем инлайн-кнопки
    private List<List<InlineKeyboardButton>> getButtons() {

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons2 = new ArrayList<>();

        keyboardButtons.add(InlineKeyboardButton.builder().text("⏰ Отложить на час").callbackData("oneHourDelay").build());
        keyboardButtons2.add(InlineKeyboardButton.builder().text("\uD83D\uDCC5 Отложить на день").callbackData("oneDayDelay").build());

        buttons.add(keyboardButtons);
        buttons.add(keyboardButtons2);

        return buttons;
    }

    private void setStateForUser(Message message, BotStates state) {
        for (User u : users) {
            if (u.getId() == message.getChatId())
                u.setState(state);
        }
    }

    private void showAllReminders(User user) throws TelegramApiException {
        if (user.userReminders.size() != 0) {
            for (Reminder r : user.userReminders) {
                execute(SendMessage.builder().
                        text("Дело № " + (user.userReminders.indexOf(r) + 1) + "\nНазвание: " + r.getName() + "\nВремя: " + df.format(new Date(r.getDateMillis()))).
                        chatId(String.valueOf(user.getId())).
                        build());
            }
        } else {
            execute(SendMessage.builder().
                    text("Список дел пуст").
                    chatId(String.valueOf(user.getId())).
                    build());
        }
    }


    //Таймер-планировщик
    class MyTimerTask extends TimerTask {

        private Reminder reminder;
        private MyTimer timer;
        private long chatId;

        public MyTimerTask(Reminder reminder, MyTimer timer, long chatId) {
            this.reminder = reminder;
            this.timer = timer;
            this.chatId = chatId;
        }

        @SneakyThrows
        @Override
        public void run() {
            User temp = null;
            for (User u: users){
                if (u.getId() == chatId){
                    temp = u;
                }
            }
            execute(SendMessage.builder().
                    text("⏰Напоминаю вам о деле \n" + reminder.getName() +"\nЕсли хотите отложить его - выберите один из вариантов ниже").
                    chatId(String.valueOf(chatId)).
                    replyMarkup(InlineKeyboardMarkup.builder().keyboard(getButtons()).build()).
                    build());
            temp.userReminders.remove(reminder);
            temp.userTimers.remove(timer);
        }
    }
}

