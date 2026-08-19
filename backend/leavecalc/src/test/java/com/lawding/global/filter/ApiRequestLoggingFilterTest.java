package com.lawding.global.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ApiRequestLoggingFilterTest {

    private final ApiRequestLoggingFilter filter = new ApiRequestLoggingFilter();
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUpLogAppender() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(ApiRequestLoggingFilter.class)).addAppender(logAppender);
    }

    @AfterEach
    void tearDownLogAppender() {
        ((Logger) LoggerFactory.getLogger(ApiRequestLoggingFilter.class)).detachAppender(logAppender);
        logAppender.stop();
    }

    @Test
    void preservesResponseStatusAfterLogging() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(204);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(204);
        assertThat(logAppender.list.getLast().getFormattedMessage())
            .startsWith("[REQUEST_SUCCESS]");
        String traceId = response.getHeader(ApiRequestLoggingFilter.TRACE_ID_HEADER);
        assertThat(traceId).isNotBlank();
        assertThatCode(() -> UUID.fromString(traceId)).doesNotThrowAnyException();
        assertThat(logAppender.list.getLast().getMDCPropertyMap())
            .containsEntry(ApiRequestLoggingFilter.TRACE_ID, traceId);
    }

    @Test
    void classifiesClientErrorAsRequestFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(401);

        filter.doFilter(request, response, chain);

        assertThat(logAppender.list.getLast().getFormattedMessage())
            .startsWith("[REQUEST_FAILURE]");
    }

    @Test
    void logsRequestsEvenWhenFilterChainThrows() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/failure");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            throw new IllegalStateException("test failure");
        };

        org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> filter.doFilter(request, response, chain)
        ).isInstanceOf(IllegalStateException.class);

        assertThat(logAppender.list.getLast().getFormattedMessage())
            .startsWith("[SYSTEM_ERROR]");
    }
}
