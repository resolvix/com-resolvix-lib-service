package com.resolvix.lib.service;

import com.resolvix.lib.service.api.ServiceException;
import com.resolvix.lib.service.api.ServiceFault;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;

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

    protected abstract Logger getLogger();

    protected void begin(UUID contextId) {
        getLogger().info("BEGIN contextId: {}", contextId);
    }

    protected abstract C initialise(UUID contextId, Q q);

    protected abstract void validate(C c)
        throws ServiceException, ServiceFault;

    protected void preprocess(C c)
        throws ServiceException, ServiceFault { }

    protected abstract void process(C c)
        throws ServiceException, ServiceFault;

    protected void postprocess(C c)
        throws ServiceException, ServiceFault { }

    protected abstract R respond(C c)
        throws ServiceException, ServiceFault;

    protected abstract R respond(C c, ServiceException e)
        throws ServiceFault;

    protected abstract <E extends Exception> E fault(C c, ServiceFault sf)
        throws Exception;

    public R execute(Q q)
        throws Exception
    {
        UUID contextId = UUID.randomUUID();
        begin(contextId);
        MDC.put("contextId", contextId.toString());
        C c = null;
        try {
            c = initialise(contextId, q);
            validate(c);
            preprocess(c);
            process(c);
            postprocess(c);
            return respond(c);
        } catch (ServiceException se) {
            return respond(c, se);
        } catch (ServiceFault sf) {
            throw fault(c, sf);
        } finally {
            MDC.clear();
            end(contextId);
        }
    }

    protected void end(UUID contextId) {
        getLogger().info("END contextId: {}", contextId);
    }
}
