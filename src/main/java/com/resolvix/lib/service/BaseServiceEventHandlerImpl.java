package com.resolvix.lib.service;

import com.resolvix.lib.service.api.ServiceEventHandler;

public abstract class BaseServiceEventHandlerImpl<E extends Enum<E>>
    implements ServiceEventHandler<E> { }
