package com.jdk8.juc;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: TODO
 * @date 2020/7/1014:52
 */
public class BlockingQueue {

    public static void main(String[] args) {
        ArrayBlockingQueue fairQueue = new ArrayBlockingQueue(1000,true);

    }
}
