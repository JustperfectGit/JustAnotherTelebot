import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public class Handler extends TelegramBot{

    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    @SneakyThrows
    public void startHandler(Message message) {

        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow kbf = new KeyboardRow();
        KeyboardRow kbs = new KeyboardRow();
        kbf.add("Новое напоминание");
        kbs.add("Показать список напоминаний");
        kbf.add("Очистить список напоминаний");
        kbs.add("Удалить одно из напоминаний");
        kbf.add("Случайный совет");
        keyboard.add(kbf);
        keyboard.add(kbs);
        replyKeyboardMarkup.setKeyboard(keyboard);

        execute(SendMessage.builder().
                text("\uD83D\uDD14Привет! Я - JustAnotherTeleBot. \nМоя задача - напоминать о важном. Установите новое напоминание и я сообщу о нём точно в срок.").
                chatId(message.getChatId().toString()).
                build());
        Thread.sleep(3000);
        execute(SendMessage.builder().
                text("P.S. А еще у меня есть совет на любой жизненный случай! \nВот только он на английском языке... \uD83E\uDD14").
                chatId(message.getChatId().toString()).
                replyMarkup(replyKeyboardMarkup).
                build());
    }

    @SneakyThrows
    public void setNewReminderHandler(Message message){


        execute(SendMessage.builder().
                text("Введите название для нового дела:").
                chatId(message.getChatId().toString()).
                build());

        return;
    }

}
