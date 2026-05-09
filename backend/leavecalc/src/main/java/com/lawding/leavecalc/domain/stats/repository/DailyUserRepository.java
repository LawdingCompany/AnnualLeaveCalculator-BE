package com.lawding.leavecalc.domain.stats.repository;

import com.lawding.leavecalc.domain.stats.model.DailyUser;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DailyUserRepository {

    private final DynamoDbTable<DailyUser> table;

    private static final String TABLE_NAME = "daily_user";

    @Autowired
    public DailyUserRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(DailyUser.class));
    }

    public List<DailyUser> scanAll() {
        return table.scan().items().stream().toList();
    }
}
