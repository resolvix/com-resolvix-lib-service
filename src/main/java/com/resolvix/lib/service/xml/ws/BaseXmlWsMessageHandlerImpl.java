package com.resolvix.lib.service.xml.ws;

import com.resolvix.lib.service.api.ServiceEventHandler;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;

public abstract class BaseXmlWsMessageHandlerImpl<E extends Enum<E>>
    implements LogicalHandler<LogicalMessageContext>
{
    private static final String NO_MESSAGE_SOURCE = "<none>";

    @Inject
    private ServiceEventHandler<E> serviceEventHandler;

    protected abstract Logger getLogger();

    protected String getSource(Source source)
        throws Exception
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        Writer out = new StringWriter();
        StreamResult streamResult = new StreamResult();
        streamResult.setWriter(out);
        transformer.transform(source, streamResult);
        return streamResult.getWriter().toString();
    }

    protected boolean handleInboundMessage(LogicalMessageContext logicalMessageContext)
        throws Exception
    {
        String contextId = UUID.randomUUID().toString();
        logicalMessageContext.put("contextId", contextId);
        LogicalMessage logicalMessage = logicalMessageContext.getMessage();
        Source source = logicalMessage.getPayload();
        serviceEventHandler.handleRequest(
            contextId, (source != null) ? getSource(source) : NO_MESSAGE_SOURCE);
        return true;
    }

    protected boolean handleOutboundMessage(LogicalMessageContext logicalMessageContext)
        throws Exception
    {
        String contextId = (String) logicalMessageContext.get("contextId");
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
