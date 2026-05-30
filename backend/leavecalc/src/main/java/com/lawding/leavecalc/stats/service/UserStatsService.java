package com.lawding.leavecalc.stats.service;

import com.lawding.leavecalc.stats.dto.DailyUserDto;
import java.util.List;

public interface UserStatsService {

    List<DailyUserDto> getAllDailyUser();
}
