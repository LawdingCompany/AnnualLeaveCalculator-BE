package com.lawding.leavecalc.domain.appversion.repository;

import com.lawding.leavecalc.domain.appversion.model.AppVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class AppVersionRepository {

    // DynamoDbTable : 쿼리 실행기
    private final DynamoDbTable<AppVersion> table;
    private static final String TABLE_NAME = "app_version_policy";

    @Autowired
    public AppVersionRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AppVersion.class));
    }

    public Optional<AppVersion> findByPlatform(String platform) {
        return Optional.ofNullable(
            table.getItem(Key.builder().partitionValue(platform).build())
        );
    }

    public List<AppVersion> scanAll(){
        return table.scan().items().stream().toList();
    }
    public void save(AppVersion appVersion) {
        table.putItem(appVersion);
    }

    public void delete(String platform) {
        table.deleteItem(
            Key.builder().partitionValue(platform).build()
        );
    }
}
