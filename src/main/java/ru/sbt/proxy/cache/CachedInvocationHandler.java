package ru.sbt.proxy.cache;

import java.io.*;
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
    private final Map<Object, Object> cacheMemory = new HashMap<Object, Object>();
    private final Object delegate;
    private final String dir;

    public <T> T cache(Object delegate) {
        return (T) Proxy.newProxyInstance(delegate.getClass().getClassLoader(),
                delegate.getClass().getInterfaces(),
                new CachedInvocationHandler(delegate, dir)
                );
    }

    public CachedInvocationHandler(Object delegate, String dir) {
        this.delegate = delegate;
        this.dir = dir;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(!method.isAnnotationPresent(Cache.class)) return invoke(method, args);

        CacheType cacheType = method.getAnnotation(Cache.class).cacheType();

        if(cacheType == CacheType.IN_MEMORY) {
            return memoryCache(method, args);
        } else return fileCache(method, args);
//        if(!resultByArg.containsKey(key(method, args))) {
//            System.out.println("Delegation of " + method.getName());
//            Object result = invoke(method, args);
//            resultByArg.put(key(method, args), result);
//        }
//        return resultByArg.get(key(method, args));
    }

    private Object memoryCache(Method method, Object[] args) {
        if(!cacheMemory.containsKey(key(method, args))) {
            Object result = invoke(method, args);
            cacheMemory.put(key(method, args), result);
        }
        return cacheMemory.get(key(method, args));
    }

    private Object fileCache(Method method, Object[] args) {
        Map<Object, Object> cacheFile = new HashMap<Object, Object>();
        cacheFile.putAll(((Map<Object, Object>) deserializator(method)));
        if(!cacheFile.containsKey(key(method, args))) {
            Object result = invoke(method, args);
            cacheFile.put(key(method, args), result);
        }
        return cacheFile.get(key(method, args));
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

    private void serializator(Method method,Map<Object, Object> result){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(dir + fileName(method) + ".ser"));
            outputStream.writeObject(result);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private Object deserializator(Method method){
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dir + fileName(method) + ".ser"));
            return inputStream.readObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String fileName(Method method) {
        if(method.getAnnotation(Cache.class).fileName().equals("")) return method.getName();
        else return method.getAnnotation(Cache.class).fileName();
    }
}
