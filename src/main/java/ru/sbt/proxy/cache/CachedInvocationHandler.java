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
        if (!method.isAnnotationPresent(Cache.class)) return invoke(method, args);

        CacheType cacheType = method.getAnnotation(Cache.class).cacheType();

        if (cacheType == CacheType.IN_MEMORY) {
            return memoryCache(method, args);
        } else {
            return fileCache(method, args);
        }

    }

    private Object memoryCache(Method method, Object[] args) {
        if (!cacheMemory.containsKey(key(method, args))) {
            Object result = invoke(method, args);
            if (method.getReturnType() == List.class) {
                int listSize = method.getAnnotation(Cache.class).listSize();
                result = listCut(result, listSize);
            }
            cacheMemory.put(key(method, args), result);
        }
        return cacheMemory.get(key(method, args));
    }

    private Object fileCache(Method method, Object[] args) {
        String fileName = fileName(args, method);
        Object result;
        File file = new File(dir + fileName + ".ser");

        if (!file.exists()) {
            result = invoke(method, args);
            if (method.getReturnType() == List.class) {
                int listSize = method.getAnnotation(Cache.class).listSize();
                result = listCut(result, listSize);
            }
            serializator(result, fileName);
            return result;
        } else {
            return result = deserializator(fileName);
        }
    }


    private ArrayList<?> listCut(Object o, int listSize) {
        List<?> list = (List) o;

        if (list.size() <= listSize) {
            return new ArrayList<>(list);
        }
        list = list.subList(0, listSize);
        return new ArrayList<>(list);
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
        List<Object> key = new ArrayList<>();
        key.add(method);
        key.addAll(asList(args));
        return key;
    }

    private void serializator(Object o, String fileName) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    new FileOutputStream(dir + fileName + ".ser"));
            outputStream.writeObject(o);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T deserializator(String fileName) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(dir + fileName + ".ser"));
            return (T) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private String fileName(Object[] args, Method method) {

        return "Cache_" + method.getReturnType().getName() + "_" + args[0];
    }
}
