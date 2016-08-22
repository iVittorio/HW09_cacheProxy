package ru.sbt.service;


import ru.sbt.proxy.cache.CachedInvocationHandler;

public class Main {
    public static void main(String[] args) {
        Service service =
                new CachedInvocationHandler(new ServiceImpl(),"/Users/i.viktor/IdeaProjects/cache-proxy/src/main/resources/")
                        .cache(new ServiceImpl());
        runHard(service);
        runVeryHard(service);
        workForChuckNorris(service);

    }

    private static void runHard(Service service) {
        System.out.println(service.doHardWork("work1",10));
        System.out.println(service.doHardWork("work2",10));
        System.out.println(service.doHardWork("work4",10));
        System.out.println(service.doHardWork("work1",10));
        System.out.println(service.doHardWork("work2",10));
        System.out.println(service.doHardWork("work1",10));
    }

    private static void runVeryHard(Service service) {
        System.out.println(service.veryHardWork("work1",20.));
        System.out.println(service.veryHardWork("work2",20.));
        System.out.println(service.veryHardWork("work1",20.));
        System.out.println(service.veryHardWork("work1",20.));
        System.out.println(service.veryHardWork("work2",20.));
    }

    private static void workForChuckNorris(Service service) {
        System.out.print(service.workForChuckNorris("work1", 100));
        System.out.print(service.workForChuckNorris("work2", 200));
        System.out.print(service.workForChuckNorris("work1", 100));
        System.out.print(service.workForChuckNorris("work2", 200));
        System.out.print(service.workForChuckNorris("work2", 200));

    }
}
