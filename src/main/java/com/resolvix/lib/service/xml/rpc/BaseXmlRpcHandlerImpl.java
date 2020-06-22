package com.resolvix.lib.service.xml.rpc;

import org.slf4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;

public abstract class BaseXmlRpcHandlerImpl
    implements Handler
{
    protected abstract Logger getLogger();

    @Override
    public void init(HandlerInfo handlerInfo) {
        getLogger().debug("BaseRpcHandlerImpl::init invoked.");
    }

    @Override
    public QName[] getHeaders() {
        getLogger().debug("BaseRpcHandlerImpl::getHeaders invoked.");
        return new QName[0];
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        getLogger().debug("BaseRpcHandlerImpl::handleRequest invoked.");
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        getLogger().debug("BaseRpcHandlerImpl::handleResponse invoked.");
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        getLogger().debug("BaseRpcHandlerImpl::handleFault invoked.");
        return true;
    }

    @Override
    public void destroy() {
        getLogger().debug("BaseRpcHandlerImpl::destroy invoked.");
    }
}
