package ru.sbt.service;

/**
 * Created by i.viktor on 19/08/16.
 */
public class ServiceImpl implements Service {

    public double doHardWork(String workName, int num) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        
        return num/5.0;
    }
}
