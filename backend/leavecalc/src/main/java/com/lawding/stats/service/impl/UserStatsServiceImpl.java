package com.lawding.stats.service.impl;

import com.lawding.stats.dto.DailyUserDto;
import com.lawding.stats.repository.DailyUserRepository;
import com.lawding.stats.service.UserStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserStatsServiceImpl implements UserStatsService {

    private final DailyUserRepository repository;

    @Override
    public List<DailyUserDto> getAllDailyUser() {
        return repository.scanAll().stream()
            .map(DailyUserDto::from)
            .toList();
    }
}
