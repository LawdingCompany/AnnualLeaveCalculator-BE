# Database migrations

PostgreSQL 스키마는 애플리케이션 시작 시 Flyway가 자동으로 적용합니다.

- `V1__create_initial_schema.sql`: 비어 있는 DB에 최초 최신 스키마 생성
- `V2__Seed_initial_csv_data.java`: 사전 카테고리, 사전, 피드백 CSV 초기 적재

적용 이력은 PostgreSQL의 `flyway_schema_history`에 기록됩니다. V1과 V2가 성공한 뒤에는
서버를 재시작하거나 재배포해도 다시 실행되지 않습니다.

## 이후 변경 규칙

이미 적용된 V1/V2 파일과 seed CSV는 수정하지 않습니다. 모든 변경은 다음 버전 migration을
추가합니다.

```sql
-- src/main/resources/db/migration/V3__add_example_column.sql
ALTER TABLE example ADD COLUMN new_column VARCHAR(100);
```

운영 설정은 `clean-disabled: true`, `baseline-on-migrate: false`, Hibernate는
`ddl-auto: validate`입니다. Flyway와 애플리케이션 어디에도 기존 테이블이나 데이터를 삭제하는
자동 로직은 없습니다. 최초 배포 전에 DB를 직접 비운 뒤 실행하고, 이후에는 새 버전 migration만
추가합니다.
