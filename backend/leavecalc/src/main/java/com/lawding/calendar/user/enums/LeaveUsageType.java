package com.lawding.calendar.user.enums;

public enum LeaveUsageType {
    QUARTER_DAY(0.25),
    HALF_DAY(0.5),
    FULL_DAY(1.0),
    HOURLY(0.0);

    private final double dayIncrement;

    LeaveUsageType(double dayIncrement) {
        this.dayIncrement = dayIncrement;
    }

    public double getDayIncrement() {
        return dayIncrement;
    }

    public boolean isDayBased() {
        return this != HOURLY;
    }
}
