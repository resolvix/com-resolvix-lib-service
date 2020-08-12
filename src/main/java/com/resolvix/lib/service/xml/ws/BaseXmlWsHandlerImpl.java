package com.resolvix.lib.service.xml.ws;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.function.Supplier;

abstract class BaseXmlWsHandlerImpl<C extends MessageContext>
    implements Handler<C>
{
    protected static String getUuid() {
        return UUID.randomUUID().toString();
    }

    protected static final String MESSAGE_CONTEXT_HANDLER_STACK_PROPERTY_KEY = getUuid();

    protected BaseXmlWsHandlerImpl() {
        //
    }

    private enum PropertyTypes {

        ABSENT,

        KEY_VALUE;
    }

    private interface Property {

        String getKey();

        PropertyTypes getType();
    }

    private static class AbsentProperty
        implements Property
    {
        private String key;

        AbsentProperty(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public PropertyTypes getType() {
            return PropertyTypes.ABSENT;
        }
    }

    private static class KeyValueProperty<V>
        implements Property
    {
        private String key;

        private Scope scope;

        private V value;

        KeyValueProperty(String key, Scope scope, V value) {
            this.key = key;
            this.scope = scope;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        public Scope getScope() {
            return scope;
        }

        public PropertyTypes getType() {
            return PropertyTypes.KEY_VALUE;
        }

        public V getValue() {
            return value;
        }
    }

    protected Deque<Property> createPropertyDeque(String key) {
        return new ArrayDeque<>();
    }

    @SuppressWarnings({"unchecked", "unused"})
    protected <V> V getProperty(C messageContext, String key, Class<V> classV) {
        return (V) messageContext.get(key);
    }

    protected <V> void setProperty(C messageContext, Scope scope, String key, V value) {
        @SuppressWarnings("unchecked")
        Deque<Property> q = (Deque<Property>) messageContext.computeIfAbsent(MESSAGE_CONTEXT_HANDLER_STACK_PROPERTY_KEY, this::createPropertyDeque);
        if (messageContext.containsKey(key))
            q.add(new KeyValueProperty<>(key, messageContext.getScope(key), messageContext.get(key)));
        else
            q.add(new AbsentProperty(key));
        messageContext.put(key, value);
        messageContext.setScope(key, scope);
    }

    @SuppressWarnings("unchecked")
    protected <V> V computePropertyIfAbsent(C messageContext, Scope scope, String key, Supplier<V> supplierV) {
        if (messageContext.containsKey(key))
            return (V) messageContext.get(key);

        V value = supplierV.get();
        setProperty(messageContext, scope, key, value);
        return value;
    }

    @SuppressWarnings("unchecked")
    protected <V> V computePropertyIfPresent(C messageContext, Scope scope, String key, Supplier<V> supplierV) {
        if (!messageContext.containsKey(key))
            return null;

        V value = supplierV.get();
        setProperty(messageContext, scope, key, value);
        return value;
    }

    @Override
    public void close(MessageContext messageContext) {
        @SuppressWarnings("unchecked")
        Deque<Property> q = (Deque<Property>) messageContext.get(MESSAGE_CONTEXT_HANDLER_STACK_PROPERTY_KEY);
        if (q == null)
            return;

        while (!q.isEmpty()) {
            Property p = q.pop();
            switch (p.getType()) {
                case ABSENT:
                    AbsentProperty ap = (AbsentProperty) p;
                    messageContext.remove(ap.getKey());
                    break;

                case KEY_VALUE:
                    KeyValueProperty<?> kvp = (KeyValueProperty<?>) p;
                    messageContext.put(kvp.getKey(), kvp.getValue());
                    break;

                default:
                    break;
            }
        }

        messageContext.remove(MESSAGE_CONTEXT_HANDLER_STACK_PROPERTY_KEY);
    }
}
