package com.db.quoters.bpp;

import com.db.quoters.Benchmark;
import com.db.quoters.Quoter;
import com.db.quoters.ShakespeareQuoter;
import com.db.quoters.Transaction;
import lombok.SneakyThrows;
import org.apache.commons.lang.ArrayUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class TransactionAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {

        Class<?> type = o.getClass();

        boolean isProxy = Proxy.isProxyClass(type);

        if (isProxy) {
            type = ((OriginalClassHandler)o).getOriginalClass();
        }

        if (!type.isAnnotationPresent(Transaction.class)) {
            return o;
        }

        Class<?>[] interfaces = type.getInterfaces();
        Class<?>[] interfacesWithOriginalClassHandler = (Class<?>[]) ArrayUtils.add(interfaces, OriginalClassHandler.class);

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
                    System.out.println("Transaction start");
                    Object result = method.invoke(o, args);
                    System.out.println("Transaction end");
                    return result;
                }
        );
    }

}
