package com.resolvix.lib.service.api;

public interface ServiceEventHandler<E extends Enum<E>> {

    void handleRequest(String contextId, String request);

    <V> void handleCheckpoint(String contextId, E checkpoint, V value);

    void handleException(String contextId, Exception exception);

    void handleResponse(String contextId, String response);
}
