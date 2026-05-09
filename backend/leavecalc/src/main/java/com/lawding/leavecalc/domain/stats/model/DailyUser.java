package com.lawding.leavecalc.domain.stats.model;

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
public class DailyUser {

    private String recordDate;
    private int web;
    private int ios;
    private int android;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("record_date")
    public String getRecordDate(){
        return recordDate;
    }
}
