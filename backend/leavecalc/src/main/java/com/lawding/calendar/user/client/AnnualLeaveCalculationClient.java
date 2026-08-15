package com.lawding.calendar.user.client;

import com.lawding.calendar.user.client.dto.AnnualLeaveCalculationRequest;
import com.lawding.calendar.user.client.dto.AnnualLeaveCalculationResponse;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import java.time.LocalDate;
import java.util.List;
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

    public AnnualLeaveCalculationResponse calculate(
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth,
        LocalDate referenceDate
    ) {
        String fiscalYear = basis == LeaveAccrualBasis.FISCAL_YEAR
            ? String.format("%02d-01", fiscalYearBaseMonth)
            : null;
        AnnualLeaveCalculationResponse response = restClient.post()
            .uri(CALCULATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Platform", "web")
            .body(new AnnualLeaveCalculationRequest(
                basis.getCode(), fiscalYear, hireDate, referenceDate, List.of(), List.of()))
            .retrieve()
            .body(AnnualLeaveCalculationResponse.class);

        if (response == null || response.calculationDetail() == null
            || response.calculationDetail().totalLeaveDays() == null
            || !hasAvailablePeriod(response)) {
            throw new IllegalStateException("Invalid annual leave calculation response");
        }
        return response;
    }

    private boolean hasAvailablePeriod(AnnualLeaveCalculationResponse response) {
        var detail = response.calculationDetail();
        if (detail.availablePeriod() != null) return true;
        return detail.monthlyDetail() != null && detail.monthlyDetail().availablePeriod() != null
            && detail.proratedDetail() != null && detail.proratedDetail().availablePeriod() != null;
    }
}
