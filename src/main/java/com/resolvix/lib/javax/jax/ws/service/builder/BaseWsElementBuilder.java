package com.resolvix.lib.javax.jax.ws.service.builder;

/**
 * A base implementation of an element builder class for a root element, or
 * a subordinate element.
 *
 * If the builder is for a root element, the element builder subclass should
 * return an object of the root element type. If the element builder is for a
 * subordinate element, the subclass should return an object of the
 * superordinate element builder.
 *
 * @param <S> the element builder subclass type
 * @param <R> the return type
 */
public abstract class BaseWsElementBuilder<S extends BaseWsElementBuilder<S, R>, R> {

    public BaseWsElementBuilder() {
        //
    }

    /**
     * Returns the element builder instance.
     *
     * @return the builder object
     */
    public abstract S self();

    /**
     * Build the element and returns the return object.
     *
     * @return the return object
     */
    public abstract R build();
}
