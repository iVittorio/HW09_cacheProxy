package ru.sbt.service;

import ru.sbt.proxy.cache.Cache;

/**
 * Created by i.viktor on 19/08/16.
 */
public interface Service {
    @Cache
    double doHardWork(String workName, int num);
}
