package com.lawding.leavecalc.domain.stats.service.impl;

import com.lawding.leavecalc.domain.stats.dto.DailyUserDto;
import com.lawding.leavecalc.domain.stats.repository.DailyUserRepository;
import com.lawding.leavecalc.domain.stats.service.UserStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserStatsServiceImpl implements UserStatsService {

    private final DailyUserRepository dailyUserRepository;

    @Override
    public List<DailyUserDto> getAllDailyUser() {
        return dailyUserRepository.findAll().stream()
            .map(DailyUserDto::from)
            .toList();
    }
}
