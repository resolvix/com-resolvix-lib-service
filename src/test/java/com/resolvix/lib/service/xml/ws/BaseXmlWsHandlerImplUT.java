package com.resolvix.lib.service.xml.ws;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class BaseXmlWsHandlerImplUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXmlWsHandlerImplUT.class);

    @Mock
    private MessageContext messageContext;

    private String contextId;

    private class LocalXmlWsHandlerImpl
        extends BaseXmlWsHandlerImpl<MessageContext>
    {
        public String getMessageContextHandlerStackPropertyKey() {
            return MESSAGE_CONTEXT_HANDLER_STACK_PROPERTY_KEY;
        }

        public boolean handleInboundMessage(MessageContext messageContext) {
            String contextId = computePropertyIfAbsent(messageContext, Scope.APPLICATION, "contextId", LocalXmlWsHandlerImpl::getUuid);
            BaseXmlWsHandlerImplUT.this.contextId = contextId;
            LOGGER.debug("handleInboundMessage [contextId]: {}", contextId);
            return true;
        }

        public boolean handleOutboundMessage(MessageContext messageContext) {
            String contextId = getProperty(messageContext, "contextId", String.class);
            LOGGER.debug("handleOutboundMessage [contextId]: {}", contextId);
            return true;
        }

        @Override
        public boolean handleMessage(MessageContext messageContext) {
            Boolean isOutbound = (Boolean) messageContext.getOrDefault(
                MessageContext.MESSAGE_OUTBOUND_PROPERTY, Boolean.FALSE);
            if (!isOutbound.booleanValue())
                return handleInboundMessage(messageContext);
            else
                return handleOutboundMessage(messageContext);
        }

        public boolean handleFaultMessage(MessageContext messageContext) {
            String contextId = getProperty(messageContext, "contextId", String.class);
            LOGGER.debug("handleFaultMessage [contextId]: {}", contextId);
            return true;
        }

        @Override
        public boolean handleFault(MessageContext messageContext) {
            return handleFaultMessage(messageContext);
        }
    }

    private Map<String, Object> properties;

    private Map<String, Scope> scopes;

    private LocalXmlWsHandlerImpl localXmlWsHandler;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.properties = new HashMap<>();
        this.scopes = new HashMap<>();
        this.localXmlWsHandler = new LocalXmlWsHandlerImpl();

        //
        //  Configure properties -related methods
        //

        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                String key = (String) invocationOnMock.getArgument(0);
                if (properties.containsKey(key))
                    return properties.get(key);

                Function<String, ?> function = (Function<String, ?>) invocationOnMock.getArgument(1);
                Object value = function.apply(key);
                properties.put(key, value);
                return value;
            }
        }).when(messageContext).computeIfAbsent(any(String.class), any(Function.class));

        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return properties.get((String) invocationOnMock.getArgument(0));
            }
        }).when(messageContext).get(any(String.class));

        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object object = properties.get((String) invocationOnMock.getArgument(0));
                if (object != null)
                    return object;
                return invocationOnMock.getArgument(1);
            }
        }).when(messageContext).getOrDefault(any(String.class), any(Object.class));

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                properties.put(
                    (String) invocationOnMock.getArgument(0),
                    invocationOnMock.getArgument(1));
                return null;
            }
        }).when(messageContext).put(any(String.class), any(Object.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                String key = (String) invocationOnMock.getArgument(0);
                properties.remove(key);
                scopes.remove(key);
                return null;
            }
        }).when(messageContext).remove(any(String.class));

        //
        //  Configure scope -related methods
        //

        doAnswer(new Answer<Object>() {

            @Override
            public Scope answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Scope) scopes.get((String) invocationOnMock.getArgument(0));
            }
        }).when(messageContext).getScope(any(String.class));

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                scopes.put(
                    (String) invocationOnMock.getArgument(0),
                    invocationOnMock.getArgument(1));
                return null;
            }
        }).when(messageContext).setScope(any(String.class), any(Scope.class));
    }

    @Test
    public void handleMessage() {
        assertThat(
            localXmlWsHandler.handleMessage(messageContext),
            equalTo(true));

        assertThat(
            properties.keySet(),
            containsInAnyOrder(
                localXmlWsHandler.getMessageContextHandlerStackPropertyKey(),
                "contextId"));

        scopes.put(MessageContext.MESSAGE_OUTBOUND_PROPERTY, Scope.APPLICATION);
        properties.put(MessageContext.MESSAGE_OUTBOUND_PROPERTY, true);

        assertThat(
            localXmlWsHandler.handleMessage(messageContext),
            equalTo(true));

        assertThat(
            properties.keySet(),
            containsInAnyOrder(
                localXmlWsHandler.getMessageContextHandlerStackPropertyKey(),
                MessageContext.MESSAGE_OUTBOUND_PROPERTY,
                "contextId"));

        localXmlWsHandler.close(messageContext);

        assertThat(properties.keySet(), contains(MessageContext.MESSAGE_OUTBOUND_PROPERTY));
        assertThat(scopes.keySet(), contains(MessageContext.MESSAGE_OUTBOUND_PROPERTY));
    }

    @Test
    public void handleFault() {
        assertThat(
            localXmlWsHandler.handleMessage(messageContext),
            equalTo(true));

        assertThat(
            properties.keySet(),
            containsInAnyOrder(
                localXmlWsHandler.getMessageContextHandlerStackPropertyKey(),
                "contextId"));

        scopes.put(MessageContext.MESSAGE_OUTBOUND_PROPERTY, Scope.APPLICATION);
        properties.put(MessageContext.MESSAGE_OUTBOUND_PROPERTY, true);

        assertThat(
            localXmlWsHandler.handleFault(messageContext),
            equalTo(true));

        assertThat(
            properties.keySet(),
            containsInAnyOrder(
                localXmlWsHandler.getMessageContextHandlerStackPropertyKey(),
                MessageContext.MESSAGE_OUTBOUND_PROPERTY,
                "contextId"));

        localXmlWsHandler.close(messageContext);

        assertThat(properties.keySet(), contains(MessageContext.MESSAGE_OUTBOUND_PROPERTY));
        assertThat(scopes.keySet(), contains(MessageContext.MESSAGE_OUTBOUND_PROPERTY));
    }
}
