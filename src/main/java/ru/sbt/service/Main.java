package ru.sbt.service;


import static ru.sbt.proxy.cache.CachedInvocationHandler.cache;



public class Main {
    public static void main(String[] args) {
        Service service = cache(new ServiceImpl());
        run(service);
        
    }

    private static void run(Service service) {
        System.out.println(service.doHardWork("work1",10));
        System.out.println(service.doHardWork("work2",10));
        System.out.println(service.doHardWork("work4",10));
        System.out.println(service.doHardWork("work1",10));
        System.out.println(service.doHardWork("work2",10));
        System.out.println(service.doHardWork("work1",10));
    }
}
