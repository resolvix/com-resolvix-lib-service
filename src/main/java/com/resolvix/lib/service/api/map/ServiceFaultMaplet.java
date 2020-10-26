package com.resolvix.lib.service.api.map;

import com.resolvix.lib.service.api.ServiceFault;

import java.util.function.Function;

public class ServiceFaultMaplet<F extends ServiceFault, E extends Exception> {

    private Class<F> serviceFaultClass;

    private Function<F, E> serviceFaultTransformer;

    private ServiceFaultMaplet(
        Class<F> serviceFaultClass,
        Function<F, E> serviceFaultTransformer
    ) {
        this.serviceFaultClass = serviceFaultClass;
        this.serviceFaultTransformer = serviceFaultTransformer;
    }

    public static <C, F extends ServiceFault, E extends Exception> ServiceFaultMaplet<F, E> of(
        Class<F> serviceFaultClass,
        Function<F, E> serviceFaultTransformer
    ) {
        return new ServiceFaultMaplet<>(
            serviceFaultClass,
            serviceFaultTransformer);
    }

    public Class<F> getServiceFaultClass() {
        return serviceFaultClass;
    }

    public Function<F, E> getServiceFaultTransform() {
        return serviceFaultTransformer;
    }
}
