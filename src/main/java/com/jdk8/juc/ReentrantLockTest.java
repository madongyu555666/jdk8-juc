package com.jdk8.juc;
import java.util.concurrent.locks.Condition;
import	java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: ReentrantLock重入锁的练习
 * @date 2020/6/1918:03
 */
public class ReentrantLockTest {



    private Lock lock = new ReentrantLock();

    public void testMethod(){
        //Condition condition = lock.newCondition();
        try {
            boolean b = lock.tryLock();
            if(b){
                Thread.sleep(50000);
                System.out.println("ThreadName=" + Thread.currentThread().getName());
                /*for (int i = 0; i < 5; i++) {
                    System.out.println("ThreadName=" + Thread.currentThread().getName()+ (" " + (i + 1)));
                }*/
            }

        }catch (Exception e ){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }


}
