package pro.sky.telegrambot.scannerDb;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationCrone {
    private NotificationTaskService notificationTaskService;
    private TelegramBot telegramBot;

    public NotificationCrone(NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void getNotificationByDb() {
        notificationTaskService.findAllByDateAndTime(LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES))
                .forEach(notification -> {
                    telegramBot.execute(new SendMessage(notification.getChatId()
                            , notification.getMessage()));
                    notificationTaskService.TaskRemove(notification);
                });
    }
}
