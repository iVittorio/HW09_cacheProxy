package ru.sbt.service;

import java.io.Serializable;

/**
 * Created by i.viktor on 19/08/16.
 */
public class ServiceImpl implements Service, Serializable {

    public double doHardWork(String workName, int num) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        
        return num/5.0;
    }

    public float veryHardWork(String workName, double num) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        return (float) num;
    }
}
