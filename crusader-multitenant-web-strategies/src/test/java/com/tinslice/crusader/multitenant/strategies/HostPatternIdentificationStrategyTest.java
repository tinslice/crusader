package com.tinslice.crusader.multitenant.strategies;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class HostPatternIdentificationStrategyTest {

    @Test
    void test_identifyTenant_withHostPattern_shouldIdentifyTenant() {
        Map<String, Object> config = new HashMap<>(2);
        config.put("regexp", "^api\\.(.*)\\.domain");
        config.put("matchIndex", 1);

        HostPatternIdentificationStrategy strategy = new HostPatternIdentificationStrategy();
        strategy.initialize(config);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("host")).thenReturn("api.tenant01.domain");
        assertEquals("tenant01", strategy.identifyTenant(request));

        when(request.getHeader("host")).thenReturn("api.tenant02.domain");
        assertEquals("tenant02", strategy.identifyTenant(request));
    }

    @Test
    void test_setPattern_withNullPattern_shouldThrowException() {
        HostPatternIdentificationStrategy strategy = new HostPatternIdentificationStrategy();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.setPattern(null));
    }

    @Test
    void test_matcherInput_withHost_shouldReturnTenantHost() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HostPatternIdentificationStrategy strategy = new HostPatternIdentificationStrategy();
        when(request.getHeader("host")).thenReturn("tenant01.domain");
        assertEquals("tenant01.domain", strategy.matcherInput(request));
    }

    @Test
    void test_initialize_withNullConfig_shouldThrowException() {
        HostPatternIdentificationStrategy strategy = new HostPatternIdentificationStrategy();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.initialize(null));
    }
}