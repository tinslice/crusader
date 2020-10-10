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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class HostIdentificationStrategyTest {

    @Test
    void test_identifyTenant_withTenantHostsAsString_shouldIdentifyTenant() {
        HostIdentificationStrategy strategy = new HostIdentificationStrategy();
        strategy.initialize(Collections.singletonMap("tenant01", "tenant01.first.domain, tenant01.second.domain"));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("host")).thenReturn("tenant01.first.domain");
        assertEquals("tenant01", strategy.identifyTenant(request));

        when(request.getHeader("host")).thenReturn("tenant01.second.domain");
        assertEquals("tenant01", strategy.identifyTenant(request));
    }

    @Test
    void test_identifyTenant_withTenantHostsAsStringArray_shouldIdentifyTenant() {
        HostIdentificationStrategy strategy = new HostIdentificationStrategy();
        strategy.initialize(Collections.singletonMap("tenant01", new String[] {"tenant01.first.domain", "tenant01.second.domain"}));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("host")).thenReturn("tenant01.first.domain");
        assertEquals("tenant01", strategy.identifyTenant(request));

        when(request.getHeader("host")).thenReturn("tenant01.second.domain");
        assertEquals("tenant01", strategy.identifyTenant(request));
    }

    @Test
    void test_setConfiguration_withNullConfig_shouldThrowException() {
        HostIdentificationStrategy strategy = new HostIdentificationStrategy();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> strategy.setConfiguration(null));
    }

    @Test
    void test_initializeStrategy_withStringHosts_shouldSetTenantConfig() {
        HostIdentificationStrategy strategy = new HostIdentificationStrategy();
        strategy.initialize(Collections.singletonMap("tenant01", "tenant01.first.domain, tenant01.second.domain,"));
        assertTrue(strategy.getConfiguration().containsKey("tenant01.first.domain"));
        assertTrue(strategy.getConfiguration().containsKey("tenant01.second.domain"));
    }

    @Test
    void test_initializeStrategy_withStringHostsAndEmptyHosts_shouldSetOnlyNonEmptyHosts() {
        HostIdentificationStrategy strategy = new HostIdentificationStrategy();
        strategy.initialize(Collections.singletonMap("tenant01", "tenant01.first.domain, tenant01.second.domain,,,"));
        assertEquals(2, strategy.getConfiguration().size());
    }
}