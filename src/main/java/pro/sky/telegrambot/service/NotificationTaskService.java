package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository repository;

    public NotificationTaskService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    public NotificationTask TaskAdd(NotificationTask notificationTask) {
        return repository.save(notificationTask);
    }

    public List<NotificationTask> findAllByDateAndTime(LocalDateTime localDateTime) {
        return repository.findByLocalDateTime(localDateTime);
    }

    public void TaskRemove(NotificationTask notificationTask) {
        repository.delete(notificationTask);
    }
}
