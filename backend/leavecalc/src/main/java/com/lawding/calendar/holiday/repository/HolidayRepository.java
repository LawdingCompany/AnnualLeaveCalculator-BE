package com.lawding.calendar.holiday.repository;

import com.lawding.calendar.holiday.model.Holiday;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class HolidayRepository {

    private final DynamoDbTable<Holiday> table;
    private static final String TABLE_NAME = "holidays";

    @Autowired
    public HolidayRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Holiday.class));
    }

    /**
     * 특정 연도의 공휴일 조회 ex) findByYear(2024) → "2024" 로 시작하는 항목 전체
     */
    public List<Holiday> findByYear(int year) {
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
            .filterExpression(Expression.builder()
                .expression("begins_with(holiday_date, :year)")
                .expressionValues(Map.of(
                    ":year", AttributeValue.builder().s(String.valueOf(year)).build()
                ))
                .build())
            .build();

        return table.scan(request)
            .items()
            .stream()
            .collect(Collectors.toList());
    }

    /**
     * 시작 연도 ~ 끝 연도 범위의 공휴일 조회 ex) findByYearRange(2023, 2025) → "2023-01-01" ~ "2025-12-31" 범위
     */
    public List<Holiday> findByYearRange(int startYear, int endYear) {
        String startDate = startYear + "-01-01";
        String endDate = endYear + "-12-31";

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
            .filterExpression(Expression.builder()
                .expression("holiday_date BETWEEN :startDate AND :endDate")
                .expressionValues(Map.of(
                    ":startDate", AttributeValue.builder().s(startDate).build(),
                    ":endDate", AttributeValue.builder().s(endDate).build()
                ))
                .build())
            .build();

        return table.scan(request)
            .items()
            .stream()
            .collect(Collectors.toList());
    }
}
