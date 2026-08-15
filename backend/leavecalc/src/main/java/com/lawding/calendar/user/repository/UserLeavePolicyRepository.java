package com.lawding.calendar.user.repository;

import com.lawding.calendar.user.entity.UserLeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserLeavePolicyRepository extends JpaRepository<UserLeavePolicy,Long> {

    void deleteByUser_Id(Long userId);

    @Query("select p.userId from UserLeavePolicy p")
    List<Long> findAllUserIds();
}
