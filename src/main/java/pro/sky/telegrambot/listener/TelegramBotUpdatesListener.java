package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    private NotificationTaskService notificationTaskService;

    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here

            Long chatId = update.message().chat().id();
            String text = update.message().text();
            Matcher matcher = PATTERN.matcher(text);

            if (update.message().text().equals(("/start"))) {
                SendMessage message = new SendMessage(chatId, "Здарова дружище!\uD83D\uDC4B \n");
                SendResponse response = telegramBot.execute(message);
                SendMessage message1 = new SendMessage(chatId, update.message().chat().firstName()
                        + " Запомни! ты теперь не помнишь, а я помню!\n "
                        + "Что бы я запомнил, напиши например:\n "
                        + "\"01.01.2022 20:00 Пойти покушать\" \n"
                        + " И ты точно не останешься голодным \uD83D\uDE0E ");
                SendResponse response1 = telegramBot.execute(message1);
            } else if (matcher.matches()) {
                String timeAndDate = matcher.group(1);
                String message = matcher.group(3);
                LocalDateTime localDateTime = LocalDateTime.parse(timeAndDate
                        , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                NotificationTask notificationTask = new NotificationTask (chatId, message, localDateTime);
                notificationTaskService.TaskAdd(notificationTask);
                telegramBot.execute(new SendMessage(chatId
                        , "Я запомнил! \n"
                        + "подскажу когда тебе нужно будет!\uD83E\uDEE1"));
            } else telegramBot.execute(new SendMessage(chatId
                    , "Неправильный формат! \n"
                    + "попробуй еще раз!"));
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
