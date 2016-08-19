package ru.sbt.service;

import ru.sbt.proxy.cache.Cache;
import ru.sbt.proxy.cache.CacheType;

/**
 * Created by i.viktor on 19/08/16.
 */
public interface Service {
    @Cache(cacheType = CacheType.IN_FILE)
    double doHardWork(String workName, int num);

    @Cache(cacheType = CacheType.IN_MEMORY)
    float veryHardWork(String workName, double num);
}
