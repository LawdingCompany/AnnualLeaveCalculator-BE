package com.lawding.calendar.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public record WorkPattern(Map<DayOfWeek, WorkTime> schedule) implements Serializable {

    // JSON -> 객체, 역직렬화 시 호출되는 정적 팩토리 메서드
    @JsonCreator
    public static WorkPattern of(Map<DayOfWeek, WorkTime> schedule) {
        return new WorkPattern(schedule == null ? Map.of() : new EnumMap<>(schedule));
    }

    // 객체 -> JSON, 직렬화 시 record 전체가 아니라 이 메서드의 반환값(Map)만 JSON으로 씀
    @JsonValue
    public Map<DayOfWeek, WorkTime> schedule() {
        return schedule;
    }

    public Optional<WorkTime> get(DayOfWeek day) {
        return Optional.ofNullable(schedule.get(day));
    }

    public int weeklyWorkingDays() {
        return schedule.size();
    }

    public long totalWeeklyMinutes() {
        return schedule.values().stream()
            .mapToLong(WorkTime::minutes)
            .sum();
    }

    public record WorkTime(
        @JsonProperty("start") LocalTime start,
        @JsonProperty("end") LocalTime end
    ) implements Serializable {

        @JsonCreator
        public WorkTime {
        }

        public long minutes() {
            if (start == null || end == null) {
                return 0;
            }
            return Duration.between(start, end).toMinutes();
        }
    }
}
