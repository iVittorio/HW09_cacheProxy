package ru.sbt.proxy.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by i.viktor on 19/08/16.
 */
public class CachedInvocationHandler implements InvocationHandler {
    private final Map<Object, Object> resultByArg = new HashMap<Object, Object>();
    private final Object delegate;

    public static <T> T cache(Object delegate) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                delegate.getClass().getInterfaces(),
                new CachedInvocationHandler(delegate)
                );
    }

    public CachedInvocationHandler(Object delegate) {
        this.delegate = delegate;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(!method.isAnnotationPresent(Cache.class)) return invoke(method, args);

        if(!resultByArg.containsKey(key(method, args))) {
            System.out.println("Delegation of " + method.getName());
            Object result = invoke(method, args);
            resultByArg.put(key(method, args), result);
        }
        return resultByArg.get(key(method, args));
    }

    private Object invoke(Method method, Object[] args) {
        try {
            return method.invoke(delegate, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Object key(Method method, Object[] args) {
        List<Object> key = new ArrayList<Object>();
        key.add(method);
        key.addAll(asList(args));
        return key;
    }
}
