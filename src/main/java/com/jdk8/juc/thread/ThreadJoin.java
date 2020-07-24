package com.jdk8.juc.thread;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: TODO
 * @date 2020/7/2311:13
 */
public class ThreadJoin implements Runnable{

    int i=0;

    public static void main(String[] args) throws InterruptedException {
        ThreadJoin threadJoin=new ThreadJoin();
        Thread thread1=new Thread(threadJoin);
        Thread thread2=new Thread(threadJoin);
        Thread thread3=new Thread(threadJoin);
        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();

    }

    @Override
    public void run() {
       i++;
        System.out.println(i);
    }
}
