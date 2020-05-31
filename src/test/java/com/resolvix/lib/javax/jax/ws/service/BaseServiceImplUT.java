package com.resolvix.lib.javax.jax.ws.service;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseServiceImplUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImplUT.class);

    private static class LocalServiceImpl
        extends BaseWsServiceImpl
    {

        LocalServiceImpl() {
            super();
        }
    }

    private LocalServiceImpl service = new LocalServiceImpl();

    @Test
    public void initialised() {
        Assert.assertThat(service, Matchers.notNullValue(LocalServiceImpl.class));
    }
}
