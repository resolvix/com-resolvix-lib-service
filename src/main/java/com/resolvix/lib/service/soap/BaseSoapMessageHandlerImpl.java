package com.resolvix.lib.service.soap;

import org.slf4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseSoapMessageHandlerImpl
    implements SOAPHandler<SOAPMessageContext>
{
    protected abstract Logger getLogger();

    @Override
    public Set<QName> getHeaders() {
        getLogger().debug("BaseSoapMessageHandlerImpl::getHeaders invoked.");
        return new HashSet<>();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        getLogger().debug("BaseSoapMessageHandlerImpl::handleMessage invoked.");
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        getLogger().debug("BaseSoapMessageHandlerImpl::handleFault invoked.");
        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
        getLogger().debug("BaseSoapMessageHandlerImpl::close invoked.");
    }
}
