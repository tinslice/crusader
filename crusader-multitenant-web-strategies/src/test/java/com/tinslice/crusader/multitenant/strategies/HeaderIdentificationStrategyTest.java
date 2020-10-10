package com.tinslice.crusader.multitenant.strategies;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class HeaderIdentificationStrategyTest {

    @Test
    void test_identifyTenant_withHeaderName_shouldIdentifyTenant() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("TENANT")).thenReturn("tenant01");

        HeaderIdentificationStrategy strategy = new HeaderIdentificationStrategy();
        strategy.initialize(Collections.singletonMap("name", "TENANT"));
        assertEquals("tenant01", strategy.identifyTenant(request));
    }

    @Test
    void test_initializeStrategy_withEmptyConfig_shouldThrowException() {
        HeaderIdentificationStrategy strategy = new HeaderIdentificationStrategy();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.initialize(Collections.singletonMap("", "")));

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.initialize(Collections.singletonMap("name", null)));
        strategy.initialize(Collections.singletonMap("name", "TENANT"));
        assertEquals("TENANT", strategy.getHeaderName());
    }

    @Test
    void test_initializeStrategy_withValidConfig_shouldSetTenantHeader() {
        HeaderIdentificationStrategy strategy = new HeaderIdentificationStrategy();
        strategy.initialize(Collections.singletonMap("name", "TENANT"));
        assertEquals("TENANT", strategy.getHeaderName());
    }
}