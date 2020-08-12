package com.resolvix.lib.service.xml.ws;

import com.resolvix.service.event.handler.api.ServiceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class ServiceLoggingXmlWsSoapMessageHandlerImpl
    extends BaseXmlWsHandlerImpl<SOAPMessageContext>
    implements SOAPHandler<SOAPMessageContext>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoggingXmlWsSoapMessageHandlerImpl.class);

    private static final String DEFAULT_CHAR_SET_NAME = Charset.defaultCharset().name();

    private static final String NO_MESSAGE_SOURCE = "<none>";

    @Inject
    private ServiceEventHandler<?> serviceEventHandler;

    protected Logger getLogger() {
        return LOGGER;
    }

    private String toXml(SOAPMessage soapMessage, String charsetName)
        throws SOAPException, IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(byteArrayOutputStream);
        return byteArrayOutputStream.toString(charsetName);
    }

    private String toXml(SOAPMessage soapMessage)
        throws SOAPException, IOException
    {
        return toXml(soapMessage, DEFAULT_CHAR_SET_NAME);
    }

    @Override
    public Set<QName> getHeaders() {
        getLogger().debug("BaseSoapMessageHandlerImpl::getHeaders invoked.");
        return new HashSet<>();
    }

    protected boolean handleInboundMessage(SOAPMessageContext soapMessageContext)
        throws SOAPException, IOException
    {
        String contextId = computePropertyIfAbsent(
            soapMessageContext, MessageContext.Scope.APPLICATION,
            "contextId", ServiceLoggingXmlWsMessageHandlerImpl::getUuid);
        SOAPMessage soapMessage = soapMessageContext.getMessage();
        serviceEventHandler.handleRequest(
            contextId, (soapMessage != null) ? toXml(soapMessage) : NO_MESSAGE_SOURCE);
        return true;
    }

    protected boolean handleOutboundMessage(SOAPMessageContext soapMessageContext)
        throws SOAPException, IOException
    {
        String contextId = getProperty(soapMessageContext, "contextId", String.class);
        if (contextId == null)
            throw new ProtocolException("contextId not set.");
        SOAPMessage soapMessage = soapMessageContext.getMessage();
        serviceEventHandler.handleResponse(
            contextId, (soapMessage != null) ? toXml(soapMessage) : NO_MESSAGE_SOURCE);
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        getLogger().debug("BaseSoapMessageHandlerImpl::handleMessage invoked.");
        boolean isOutbound = ((Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();
        try {
            if (isOutbound)
                return handleOutboundMessage(soapMessageContext);
            else
                return handleInboundMessage(soapMessageContext);
        } catch (Exception e) {
            // not entirely sure what should happen here
            throw new ProtocolException(e);
        }
    }

    protected boolean handleFaultMessage(SOAPMessageContext soapMessageContext)
        throws SOAPException, IOException
    {
        String contextId = getProperty(soapMessageContext,"contextId", String.class);
        if (contextId == null)
            throw new ProtocolException("contextId not set.");
        SOAPMessage soapMessage = soapMessageContext.getMessage();
        serviceEventHandler.handleFault(
            contextId, (soapMessage != null) ? toXml(soapMessage) : NO_MESSAGE_SOURCE);
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        getLogger().debug("BaseXmlWsSoapMessageHandlerImpl::handleFault invoked.");
        try {
            return handleFaultMessage(soapMessageContext);
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public void close(MessageContext messageContext) {
        super.close(messageContext);
        getLogger().debug("BaseSoapMessageHandlerImpl::close invoked.");
    }
}
