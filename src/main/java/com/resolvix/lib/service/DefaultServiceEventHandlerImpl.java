package com.resolvix.lib.service;

import com.resolvix.lib.service.api.ServiceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Default;

public final class DefaultServiceEventHandlerImpl<E extends Enum<E>>
    implements ServiceEventHandler<E>
{
    private static final Logger LOGGER = LoggerFactory.getLogger("DefaultServiceEventHandlerImpl");

    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void handleRequest(String contextId, String request) {
        getLogger().info("DefaultServiceEventHandler [contextId]: {}", contextId);
        getLogger().info("DefaultServiceEventHandler [request]:\n{}", request);
    }

    @Override
    public <V> void handleCheckpoint(String contextId, E checkpoint, V value) {
        getLogger().info("DefaultServiceEventHandler [contextId]: {}", contextId);
        getLogger().info("DefaultServiceEventHandler [checkpoint]: {}", checkpoint.name());
        getLogger().info("DefaultServiceEventHandler [value]: {}", value);
    }

    @Override
    public void handleException(String contextId, Exception exception) {
        getLogger().error("DefaultServiceEventHandler [contextId]: {}", contextId);
        getLogger().error("DefaultServiceEventHandler [exception]: {}, {}",
            exception.getClass().getCanonicalName(), exception.getMessage());
        getLogger().error("DefaultServiceEventHandler [stacktrace]");
        for (StackTraceElement stackTraceElement : exception.getStackTrace())
            getLogger().error(stackTraceElement.toString());
    }

    @Override
    public void handleResponse(String contextId, String response) {
        getLogger().info("DefaultServiceEventHandler [contextId]: {}", contextId);
        getLogger().info("DefaultServiceEventHandler [request]:\n{}", response);
    }
}
