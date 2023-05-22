package com.tinslice.crusader.multitenant.strategies;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class URIPatternIdentificationStrategyTest {

    @Test
    void test_identifyTenant_withHostPattern_shouldIdentifyTenant() {
        Map<String, Object> config = new HashMap<>(2);
        config.put("regexp", "^\\/([^\\/.]*)(\\/.*)?");
        config.put("matchIndex", 1);

        URIPatternIdentificationStrategy strategy = new URIPatternIdentificationStrategy();
        strategy.initialize(config);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/tenant01/api/health");
        assertEquals("tenant01", strategy.identifyTenant(request));

        when(request.getRequestURI()).thenReturn("/tenant02/api/health");
        assertEquals("tenant02", strategy.identifyTenant(request));
    }

    @Test
    void setPattern_withNullPattern_shouldThrowException() {
        URIPatternIdentificationStrategy strategy = new URIPatternIdentificationStrategy();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.setPattern(null));
    }

    @Test
    void test_matcherInput_withURI_shouldReturnTenantHost() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        URIPatternIdentificationStrategy strategy = new URIPatternIdentificationStrategy();
        when(request.getRequestURI()).thenReturn("/tenant01/api/health");
        assertEquals("/tenant01/api/health", strategy.matcherInput(request));
    }

    @Test
    void test_initialize_withNullConfig_shouldThrowException() {
        URIPatternIdentificationStrategy strategy = new URIPatternIdentificationStrategy();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.initialize(null));
    }
}