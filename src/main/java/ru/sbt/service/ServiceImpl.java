package ru.sbt.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public float veryHardWork(String workName, double num) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        return (float) num;
    }

    public List<String> workForChuckNorris(String workName, int size) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            list.add("String #" + i);
        }
        return list;
    }
}
