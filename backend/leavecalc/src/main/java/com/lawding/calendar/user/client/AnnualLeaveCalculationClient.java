package com.lawding.calendar.user.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnnualLeaveCalculationClient {

    private static final String CALCULATE_URL = "https://api.lawding.net/annual-leaves/calculate";

    private final RestClient restClient;

    public AnnualLeaveCalculationClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public BigDecimal calculateTotalLeaveDays(
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        LocalDate referenceDate
    ) {
        JsonNode response = restClient.post()
            .uri(CALCULATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Platform", "web")
            .header("X-Test", "true")
            .body(Map.of(
                "calculationType", basis.getCode(),
                "hireDate", hireDate,
                "referenceDate", referenceDate
            ))
            .retrieve()
            .body(JsonNode.class);

        if (response == null || response.get("calculationDetail") == null) {
            return BigDecimal.ZERO;
        }

        return sumTotalLeaveDays(response.get("calculationDetail"));
    }

    private BigDecimal sumTotalLeaveDays(JsonNode node) {
        BigDecimal childTotal = BigDecimal.ZERO;

        Iterator<JsonNode> children = node.elements();
        while (children.hasNext()) {
            JsonNode child = children.next();
            if (child.isContainerNode()) {
                childTotal = childTotal.add(sumTotalLeaveDays(child));
            }
        }

        if (childTotal.compareTo(BigDecimal.ZERO) > 0) {
            return childTotal;
        }

        if (node.has("totalLeaveDays") && node.get("totalLeaveDays").isNumber()) {
            return node.get("totalLeaveDays").decimalValue();
        }

        return BigDecimal.ZERO;
    }
}
