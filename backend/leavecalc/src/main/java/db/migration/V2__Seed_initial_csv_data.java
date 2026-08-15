package db.migration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V2__Seed_initial_csv_data extends BaseJavaMigration {
    private static final CSVFormat CSV = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .get();

    @Override
    public void migrate(Context context) throws Exception {
        seedCategories(context.getConnection());
        seedDictionary(context.getConnection());
        seedFeedback(context.getConnection());
        resetSequence(context.getConnection(), "dictionary_category", "id");
        resetSequence(context.getConnection(), "dictionary", "id");
        resetSequence(context.getConnection(), "feedback", "id");
    }

    private void seedCategories(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO dictionary_category (id, name) VALUES (?, ?)");
             Reader reader = resource("dictionary_category.csv")) {
            statement.setLong(1, 99L);
            statement.setString(2, "기타");
            statement.addBatch();
            for (CSVRecord row : CSV.parse(reader)) {
                statement.setLong(1, Long.parseLong(row.get("id")));
                statement.setString(2, row.get("name"));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void seedDictionary(Connection connection) throws Exception {
        String sql = """
            INSERT INTO dictionary
              (id, category_id, question, content, deleted, deleted_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             Reader reader = resource("dictionary.csv")) {
            for (CSVRecord row : CSV.parse(reader)) {
                statement.setLong(1, Long.parseLong(row.get("id")));
                statement.setLong(2, Long.parseLong(row.get("category_id")));
                statement.setString(3, row.get("question"));
                statement.setString(4, row.get("content"));
                statement.setBoolean(5, parseBoolean(row.get("deleted")));
                setNullableTimestamp(statement, 6, row.get("deleted_at"));
                statement.setTimestamp(7, Timestamp.valueOf(row.get("created_at")));
                statement.setTimestamp(8, Timestamp.valueOf(row.get("updated_at")));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void seedFeedback(Connection connection) throws Exception {
        String sql = """
            INSERT INTO feedback
              (id, type, content, email, rating, calculation_id, status, platform, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             Reader reader = resource("feedback.csv")) {
            for (CSVRecord row : CSV.parse(reader)) {
                statement.setLong(1, Long.parseLong(row.get("id")));
                statement.setString(2, row.get("type"));
                setNullableString(statement, 3, row.get("content"));
                setNullableString(statement, 4, row.get("email"));
                setNullableInteger(statement, 5, row.get("rating"));
                setNullableString(statement, 6, row.get("calculation_id"));
                statement.setString(7, row.get("status"));
                statement.setString(8, row.get("platform"));
                statement.setTimestamp(9, Timestamp.valueOf(row.get("created_at")));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Reader resource(String name) {
        InputStream input = getClass().getResourceAsStream("/db/seed/" + name);
        if (input == null) throw new IllegalStateException("Seed CSV not found: " + name);
        return new InputStreamReader(input, StandardCharsets.UTF_8);
    }

    private void resetSequence(Connection connection, String table, String column) throws SQLException {
        String sql = "SELECT setval(pg_get_serial_sequence('" + table + "', '" + column
            + "'), COALESCE((SELECT MAX(" + column + ") FROM " + table + "), 1), true)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean parseBoolean(String value) {
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    private boolean isNull(String value) {
        return value == null || value.isBlank() || "NULL".equalsIgnoreCase(value);
    }

    private void setNullableString(PreparedStatement statement, int index, String value)
        throws SQLException {
        if (isNull(value)) statement.setNull(index, Types.VARCHAR);
        else statement.setString(index, value);
    }

    private void setNullableInteger(PreparedStatement statement, int index, String value)
        throws SQLException {
        if (isNull(value)) statement.setNull(index, Types.INTEGER);
        else statement.setInt(index, Integer.parseInt(value));
    }

    private void setNullableTimestamp(PreparedStatement statement, int index, String value)
        throws SQLException {
        if (isNull(value)) statement.setNull(index, Types.TIMESTAMP);
        else statement.setTimestamp(index, Timestamp.valueOf(value));
    }
}
