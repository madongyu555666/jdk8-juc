package com.jdk8.juc;

import java.util.concurrent.Semaphore;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: TODO
 * @date 2020/7/108:52
 */
public class SemaphoreTest {


    public static void main(String[] args) {
        Semaphore semaphore=new Semaphore(5);

        try {
            //申请许可
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //放弃许可
            semaphore.release();
        }
    }
}
