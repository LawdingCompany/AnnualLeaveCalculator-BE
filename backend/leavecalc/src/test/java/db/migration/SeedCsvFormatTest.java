package db.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

class SeedCsvFormatTest {
    private static final CSVFormat CSV = CSVFormat.DEFAULT.builder()
        .setHeader().setSkipHeaderRecord(true).get();

    @Test
    void parsesAllSeedFilesIncludingMultilineDictionaryContent() throws Exception {
        List<CSVRecord> categories = records("dictionary_category.csv");
        List<CSVRecord> dictionaries = records("dictionary.csv");
        List<CSVRecord> feedback = records("feedback.csv");

        assertThat(categories).hasSize(5);
        assertThat(categories).extracting(row -> row.get("name"))
            .contains("연차발생", "연차승인·시기조정");
        assertThat(dictionaries).hasSize(23);
        assertThat(dictionaries).allMatch(row -> row.isMapped("category_id"));
        assertThat(feedback).hasSize(56);
    }

    private List<CSVRecord> records(String name) throws Exception {
        try (var reader = new InputStreamReader(
            getClass().getResourceAsStream("/db/seed/" + name), StandardCharsets.UTF_8)) {
            return CSV.parse(reader).getRecords();
        }
    }
}
