package com.tinslice.crusader.multitenant.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Factory class for instantiating {@link TenantIdentificationStrategy} implementations.
 */
public class TenantIdentificationStrategyFactory {
    private static final Logger logger = LoggerFactory.getLogger(TenantIdentificationStrategyFactory.class);

    private static final String EMPTY_STRING = "";
    private static final String CLASS_PROPERTY = "class";

    public static TenantIdentificationStrategy instance(String strategyName, Map<String, Object> strategyConfig)
            throws InstantiationException {
        try {
            Class<? extends TenantIdentificationStrategy> clazz = TenantIdentificationStrategyFactory.getClass(strategyName, strategyConfig);
            TenantIdentificationStrategy strategy = TenantIdentificationStrategyFactory.createInstance(clazz);
            strategy.initialize(strategyConfig);
            return strategy;
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.error(String.format("Failed to instantiate strategy '%s'", strategyName), e);
            throw new InstantiationException(String.format("Failed to instantiate strategy '%s'", strategyName));
        }
    }

    private static Class<? extends TenantIdentificationStrategy> getClass(String strategyName, Map<String, Object> strategyConfig)
            throws ClassNotFoundException {
        String clazz = null;
        if (strategyConfig != null && strategyConfig.containsKey(CLASS_PROPERTY)) {
            clazz = (String) strategyConfig.get(CLASS_PROPERTY);
        }

        if (clazz != null && !EMPTY_STRING.equals(clazz)) {
            return (Class<? extends TenantIdentificationStrategy>) Class.forName(clazz);
        }

        switch (strategyName) {
            case HeaderIdentificationStrategy.NAME:
                return HeaderIdentificationStrategy.class;
            case HostIdentificationStrategy.NAME:
                return HostIdentificationStrategy.class;
            case HostPatternIdentificationStrategy.NAME:
                return HostPatternIdentificationStrategy.class;
            case URIPatternIdentificationStrategy.NAME:
                return URIPatternIdentificationStrategy.class;
            default:
                throw new ClassNotFoundException(String.format(
                        "Property '%s' not found for identification strategy '%s'.", CLASS_PROPERTY, strategyName));
        }
    }

    private static TenantIdentificationStrategy createInstance(Class<? extends TenantIdentificationStrategy>  clazz)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getGenericParameterTypes().length == 0) {
                return (TenantIdentificationStrategy) constructor.newInstance();
            }
        }

        throw new InstantiationException(String.format("Default constructor is undefined for class '%s'",clazz.getName()));
    }
}
