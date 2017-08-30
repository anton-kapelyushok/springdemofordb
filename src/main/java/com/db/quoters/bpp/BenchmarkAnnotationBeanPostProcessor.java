package com.db.quoters.bpp;

import com.db.quoters.Benchmark;
import com.db.quoters.InjectRandomInt;
import org.apache.commons.lang.ArrayUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class BenchmarkAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {

        Class<?> type = o.getClass();

        boolean isProxy = Proxy.isProxyClass(type);

        if (isProxy) {
            type = ((OriginalClassHandler)o).getOriginalClass();
        }

        if (!type.isAnnotationPresent(Benchmark.class)) {
            return o;
        }

        Class<?>[] interfaces = type.getInterfaces();
        Class<?>[] interfacesWithOriginalClassHandler = (Class<?>[])ArrayUtils.add(interfaces, OriginalClassHandler.class);

        return Proxy.newProxyInstance(
                o.getClass().getClassLoader(),
                interfacesWithOriginalClassHandler,
                (proxy, method, args) -> {
                    if (method.getName().equals("getOriginalClass")) {
                        if (isProxy) {
                            return ((OriginalClassHandler) o).getOriginalClass();
                        } else {
                            return o.getClass();
                        }
                    }
                    System.out.println("Benchmark start");
                    Object result = method.invoke(o, args);
                    System.out.println("Benchmark end");
                    return result;
                }
        );
    }

}
