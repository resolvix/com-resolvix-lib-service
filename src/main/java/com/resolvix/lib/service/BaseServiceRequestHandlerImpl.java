package com.resolvix.lib.service;

import com.resolvix.lib.service.api.ServiceException;
import com.resolvix.lib.service.api.ServiceFault;

/**
 * Base implementation of a service request handler.
 *
 * @param <Q> the request type
 * @param <R> the response type
 * @param <F> the fault type
 * @param <C> the context type
 */
public abstract class BaseServiceRequestHandlerImpl<Q, R, F extends ServiceFault, C> {

    protected BaseServiceRequestHandlerImpl() {
        //
    }

    protected abstract C initialise(Q q)
        throws ServiceException, ServiceFault;

    protected abstract void validate(C c)
        throws ServiceException, ServiceFault;

    protected void preprocess(C c)
        throws ServiceException, ServiceFault { };

    protected abstract void process(C c)
        throws ServiceException, ServiceFault;

    protected void postprocess(C c)
        throws ServiceException, ServiceFault { }

    protected abstract R respond(C c)
        throws ServiceException, ServiceFault;

    protected abstract R respond(C c, ServiceException e)
        throws ServiceFault;

    public R execute(Q q)
        throws ServiceFault
    {
        C c = null;
        try {
            c = initialise(q);
            validate(c);
            preprocess(c);
            process(c);
            postprocess(c);
            return respond(c);
        } catch (ServiceException se) {
            return respond(c, se);
        } catch (ServiceFault sf) {
            throw sf;
        }
    }
}
