package com.lawding.notification.repository;

import com.lawding.notification.entity.UserNotification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<UserNotification> findByIdAndUser_Id(Long id, Long userId);
    boolean existsByUser_IdAndEventKey(Long userId, String eventKey);
    void deleteByUser_Id(Long userId);
}
