package com.jdk8.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: TODO
 * @date 2020/6/1918:20
 */
public class ReentrantLockTest2 implements Runnable{

    private static  Lock lock = new ReentrantLock();




    public static void main(String[] args) throws InterruptedException {
        ReentrantLockTest2 test =new ReentrantLockTest2();
        Thread t1 = new Thread(test);
        Thread t2 = new Thread(test);
        t1.start();
        t2.start();
        t1.join();
        System.out.println(Thread.currentThread().getName()+" over");

    }


    @Override
    public void run() {
        try {
            if(lock.tryLock(4, TimeUnit.SECONDS)){
                System.out.println(Thread.currentThread().getName()+"-->");
                Thread.sleep(6000);
            }else{
                System.out.println(Thread.currentThread().getName()+" time out ");
            }
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }/*finally {
            lock.unlock();//会抛出锁对象的异常，因为没有获取锁在unlock的时候出异常，可以先判断一下是否存在在执行。
        }*/
    }
}
