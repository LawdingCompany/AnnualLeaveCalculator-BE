package com.lawding.stats.service;

import com.lawding.stats.dto.DailyUserDto;
import java.util.List;

public interface UserStatsService {

    List<DailyUserDto> getAllDailyUser();
}
