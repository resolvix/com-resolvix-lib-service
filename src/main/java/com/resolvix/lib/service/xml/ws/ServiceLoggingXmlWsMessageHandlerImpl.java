package com.resolvix.lib.service.xml.ws;

import com.resolvix.service.event.handler.api.ServiceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import java.io.StringWriter;
import java.io.Writer;

public class ServiceLoggingXmlWsMessageHandlerImpl
    extends BaseXmlWsHandlerImpl<LogicalMessageContext>
    implements LogicalHandler<LogicalMessageContext>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoggingXmlWsMessageHandlerImpl.class);

    private static final String NO_MESSAGE_SOURCE = "<none>";

    private static final TransformerFactory getTransformerFactory() {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            return tf;
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final TransformerFactory TRANSFORMER_FACTORY = getTransformerFactory();

    @Inject
    private ServiceEventHandler<?> serviceEventHandler;

    protected Logger getLogger() {
        return LOGGER;
    }

    protected String getSource(Source source)
        throws TransformerException
    {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        Writer out = new StringWriter();
        StreamResult streamResult = new StreamResult();
        streamResult.setWriter(out);
        transformer.transform(source, streamResult);
        return streamResult.getWriter().toString();
    }

    protected boolean handleInboundMessage(LogicalMessageContext logicalMessageContext)
        throws TransformerException
    {
        String contextId = computePropertyIfAbsent(
            logicalMessageContext, MessageContext.Scope.APPLICATION,
            "contextId", ServiceLoggingXmlWsMessageHandlerImpl::getUuid);
        LogicalMessage logicalMessage = logicalMessageContext.getMessage();
        Source source = logicalMessage.getPayload();
        serviceEventHandler.handleRequest(
            contextId, (source != null) ? getSource(source) : NO_MESSAGE_SOURCE);
        return true;
    }

    protected boolean handleOutboundMessage(LogicalMessageContext logicalMessageContext)
        throws TransformerException
    {
        String contextId = getProperty(logicalMessageContext,"contextId", String.class);
        if (contextId == null)
            throw new ProtocolException("contextId not set.");
        LogicalMessage logicalMessage = logicalMessageContext.getMessage();
        Source source = logicalMessage.getPayload();
        serviceEventHandler.handleResponse(
            contextId, (source != null) ? getSource(source) : NO_MESSAGE_SOURCE);
        return true;
    }

    @Override
    public boolean handleMessage(LogicalMessageContext logicalMessageContext) {
        getLogger().debug("BaseWsMessageHandlerImpl::handleMessage invoked.");
        boolean isOutbound = ((Boolean) logicalMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();
        try {
            if (isOutbound)
                return handleOutboundMessage(logicalMessageContext);
            else
                return handleInboundMessage(logicalMessageContext);
        } catch (Exception e) {
            // not entirely sure what should happen here
            throw new ProtocolException(e);
        }
    }

    protected boolean handleFaultMessage(LogicalMessageContext logicalMessageContext)
        throws TransformerException
    {
        String contextId = getProperty(logicalMessageContext,"contextId", String.class);
        if (contextId == null)
            throw new ProtocolException("contextId not set.");
        LogicalMessage logicalMessage = logicalMessageContext.getMessage();
        Source source = logicalMessage.getPayload();
        serviceEventHandler.handleFault(
            contextId, (source != null) ? getSource(source) : NO_MESSAGE_SOURCE);
        return true;
    }

    @Override
    public boolean handleFault(LogicalMessageContext logicalMessageContext) {
        getLogger().debug("BaseWsMessageHandlerImpl::handleFault invoked.");
        try {
            return handleFaultMessage(logicalMessageContext);
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public void close(MessageContext messageContext) {
        super.close(messageContext);
        getLogger().debug("BaseWsMessageHandlerImpl::close invoked.");
    }
}