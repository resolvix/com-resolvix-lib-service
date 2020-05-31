package com.resolvix.lib.javax.jax.ws.service;

import com.resolvix.lib.javax.jax.ws.service.api.ServiceException;
import com.resolvix.lib.javax.jax.ws.service.api.ServiceFault;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseServiceRequestHandlerImplUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceRequestHandlerImplUT.class);

    private static abstract class BaseLocalRequest { }

    private static class LocalWellFormedRequest
        extends BaseLocalRequest { }

    private static class LocalErroneousRequest
        extends BaseLocalRequest { }

    private static class LocalFaultyRequest
        extends BaseLocalRequest { }

    private static abstract class BaseLocalResponse { }

    private static class LocalSuccessfulResponse
        extends BaseLocalResponse { }

    private static class LocalUnsuccessfulResponse
        extends BaseLocalResponse { }

    private static class LocalFaultResponse
        extends Exception { }

    private static class LocalContext {

        private BaseLocalRequest localRequest;

        LocalContext(BaseLocalRequest localRequest) {
            this.localRequest = localRequest;
        }

        BaseLocalRequest getRequest() {
            return localRequest;
        }
    }

    private static class LocalServiceRequestHandlerImpl
        extends BaseWsServiceRequestHandlerImpl<BaseLocalRequest, BaseLocalResponse, LocalContext>
    {

        @Override
        protected LocalContext initialise(BaseLocalRequest localRequest) throws ServiceException, ServiceFault {
            return new LocalContext(localRequest);
        }

        @Override
        protected void validate(LocalContext localContext) throws ServiceException, ServiceFault {

        }

        @Override
        protected void process(LocalContext localContext) throws ServiceException, ServiceFault {
            BaseLocalRequest localRequest = localContext.getRequest();
            if (localRequest instanceof LocalErroneousRequest)
                throw new ServiceException();

            if (localRequest instanceof LocalFaultyRequest)
                throw new ServiceFault() { };
        }

        @Override
        protected BaseLocalResponse respond(LocalContext localContext) throws ServiceException, ServiceFault {
            return new LocalSuccessfulResponse();
        }

        @Override
        protected BaseLocalResponse respond(LocalContext localContext, ServiceException e) throws ServiceFault {
            return new LocalUnsuccessfulResponse();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <E extends Exception> E fault(LocalContext localContext, ServiceFault sf) throws Exception {
            return (E) new LocalFaultResponse();
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private LocalServiceRequestHandlerImpl requestHandler = new LocalServiceRequestHandlerImpl();

    @Test
    public void successfulRequest() throws Exception {
        LocalWellFormedRequest request = new LocalWellFormedRequest();
        BaseLocalResponse response = requestHandler.execute(request);
        Assert.assertThat(response, Matchers.instanceOf(LocalSuccessfulResponse.class));
    }

    @Test
    public void unsuccessfulRequest() throws Exception {
        LocalErroneousRequest request = new LocalErroneousRequest();
        BaseLocalResponse response = requestHandler.execute(request);
        Assert.assertThat(response, Matchers.instanceOf(LocalUnsuccessfulResponse.class));
    }

    @Test
    public void faultyRequest() throws Exception {
        thrown.expect(LocalFaultResponse.class);
        LocalFaultyRequest request = new LocalFaultyRequest();
        BaseLocalResponse response = requestHandler.execute(request);
    }
}