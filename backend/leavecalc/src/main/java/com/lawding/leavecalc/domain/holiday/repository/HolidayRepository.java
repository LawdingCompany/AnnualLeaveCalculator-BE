package com.lawding.leavecalc.domain.holiday.repository;

import com.lawding.leavecalc.domain.holiday.model.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class HolidayRepository {

    private final DynamoDbTable<Holiday> table;
    private static final String TABLE_NAME = "holidays";

    @Autowired
    public HolidayRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Holiday.class));
    }
}
