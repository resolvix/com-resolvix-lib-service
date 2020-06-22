package com.resolvix.lib.service.factory;

import com.resolvix.lib.service.DefaultServiceEventHandlerImpl;
import com.resolvix.lib.service.api.ServiceEventHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DefaultServiceEventHandlerFactoryImpl {

    @Produces
    public <E extends Enum<E>> ServiceEventHandler<E> getServiceEventHandler() {
        return new DefaultServiceEventHandlerImpl<>();
    }
}
