package com.resolvix.lib.javax.jax.ws.handler;

import org.slf4j.Logger;

import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

public abstract class BaseWsMessageHandlerImpl
    implements LogicalHandler<LogicalMessageContext>
{
    protected abstract Logger getLogger();

    @Override
    public boolean handleMessage(LogicalMessageContext logicalMessageContext) {
        getLogger().debug("BaseWsMessageHandlerImpl::handleMessage invoked.");
        return true;
    }

    @Override
    public boolean handleFault(LogicalMessageContext logicalMessageContext) {
        getLogger().debug("BaseWsMessageHandlerImpl::handleFault invoked.");
        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
        getLogger().debug("BaseWsMessageHandlerImpl::close invoked.");
    }
}
