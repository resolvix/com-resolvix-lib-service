package com.resolvix.lib.service;

import com.resolvix.lib.service.api.ServiceException;

/**
 * Base implementation of a service request handler.
 *
 * @param <Q> the request type
 * @param <R> the response type
 * @param <C> the context type
 */
public abstract class BaseServiceRequestHandlerImpl<Q, R, C> {

    protected BaseServiceRequestHandlerImpl() {
        //
    }

    protected abstract C initialise(Q q)
        throws ServiceException;

    protected abstract void validate(C c)
        throws ServiceException;

    protected void preprocess(C c)
        throws ServiceException { };

    protected abstract void process(C c)
        throws ServiceException;

    protected void postprocess(C c)
        throws ServiceException { }

    protected abstract R respond(C c);

    protected abstract R respond(C c, ServiceException e);

    public R execute(Q q) {
        C c = null;
        try {
            c = initialise(q);
            validate(c);
            preprocess(c);
            process(c);
            postprocess(c);
            return respond(c);
        } catch (ServiceException e) {
            return respond(c, e);
        }
    }
}
