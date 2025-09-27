package com.lawding.leavecalc.domain.stats.service;

import com.lawding.leavecalc.domain.stats.dto.DailyUserDto;
import java.util.List;

public interface UserStatsService {

    List<DailyUserDto> getAllDailyUser();
}
