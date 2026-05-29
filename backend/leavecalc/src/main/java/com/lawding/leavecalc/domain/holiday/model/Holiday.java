package com.lawding.leavecalc.domain.holiday.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@DynamoDbBean
public class Holiday {

    private String holidayDate;
    private String holidayName;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("holiday_date")
    public String getHolidayDate() {
        return holidayDate;
    }

    @DynamoDbAttribute("holiday_name")
    public String getHolidayName() {
        return holidayName;
    }
}
