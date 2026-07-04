package com.lawding.calendar.user.scheduler;

import com.lawding.calendar.user.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {

    private final UserServiceImpl userService;

    @Scheduled(cron = "0 15 0 * * *", zone = "Asia/Seoul")
    public void hardDeleteDueUsers() {
        userService.hardDeleteUsersDue(LocalDateTime.now());
    }
}
