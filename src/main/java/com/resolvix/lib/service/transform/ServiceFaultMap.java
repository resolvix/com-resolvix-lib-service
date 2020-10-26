package com.resolvix.lib.service.transform;

import com.resolvix.lib.service.api.ServiceFault;
import com.resolvix.lib.service.api.map.ServiceFaultMaplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ServiceFaultMap {

    protected Map<Class<? extends ServiceFault>, Function<? extends ServiceFault, ? extends Exception>> serviceFaultExceptionMap;

    private ServiceFaultMap(
        Map<Class<? extends ServiceFault>, Function<? extends ServiceFault, ? extends Exception>> serviceFaultExceptionMap
    ) {
        this.serviceFaultExceptionMap = serviceFaultExceptionMap;
    }

    public static final ServiceFaultMap of(
        List<ServiceFaultMaplet<?, ?>> serviceFaultMaplets) {
        Map<Class<? extends ServiceFault>, Function<? extends ServiceFault, ? extends Exception>> serviceFaultExceptionMap
            = new HashMap<>();
        serviceFaultMaplets.stream()
            .forEach((ServiceFaultMaplet m) -> {
                serviceFaultExceptionMap.put(
                    m.getServiceFaultClass(),
                    m.getServiceFaultTransform()
                );
            });
        return new ServiceFaultMap(serviceFaultExceptionMap);
    }

    public <F extends ServiceFault, E extends Exception> E map(F serviceFault) {
        Function<F, E> transformer
            = (Function<F, E>) serviceFaultExceptionMap.get(serviceFault.getClass());
        if (transformer == null)
            throw new IllegalStateException(serviceFault);
        return transformer.apply(serviceFault);
    }
}
