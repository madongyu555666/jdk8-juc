package com.jdk8.juc.delay;

import com.jdk8.juc.BlockingQueue;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: TODO
 * @date 2020/7/2011:23
 */
public class MyDelayQueue extends AbstractQueue {

    /** 堆数据结构 */
    private MyScheduledFutureTask[] queue = new MyScheduledFutureTask[16];

    /** 队列元素个数 */
    private int size = 0;

    /** 锁，用于队列新增和删除时保持线程安全 */
    private final transient ReentrantLock lock = new ReentrantLock();

    /** 用于实现队列延迟取出元素 */
    private final Condition available = lock.newCondition();

    /**
     * 领导线程，可理解为正在获取节点的线程
     * 和锁、Condition一起，
     * 控制队列延迟获取节点时，线程的等待和唤醒
     */
    private Thread leader = null;


    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean add(Object o) {
        lock.lock();

        try {
            //队列空间不足时，扩展
            if(size >= queue.length-1){
                queue = Arrays.copyOf(queue, queue.length*2);
            }

            MyScheduledFutureTask task = (MyScheduledFutureTask) o;
            //queue没有任务时，直接往数组第一个放入任务
            if(size == 0){
                queue[size++] = task;
            }
            //queue已经有任务时，在堆尾部增加任务，并实行堆上浮操作
            else {
                size++;
                siftUp(size-1, task);
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean offer(Object o) {
        return false;
    }

    @Override
    public Object poll() {
        return null;
    }

    @Override
    public Object peek() {
        return null;
    }


    public MyScheduledFutureTask take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            for (;;) {
                //取出堆中的头节点
                MyScheduledFutureTask first = queue[0];
                //如果堆中没有节点，则挂起线程
                if (first == null) {
                    available.await();
                } else {
                    //获取节点任务的等待时间
                    long delay = first.getDelay(NANOSECONDS);
                    //如果已经不需要等待，直接返回节点任务，并将下一个节点视为头节点进行堆排序
                    if (delay <= 0) {
                        return finishPoll(first);
                    }
                    //下面代码线程等待时不再持有无用的first对象，直接释放它
                    first = null;

                    //leader不为空,则某个awaitNanos线程已经在取任务，挂起线程
                    if (leader != null) {
                        available.await();
                    }
                        //leader为空，此时没有线程在取任务
                    else {
                        //设置leader为当前线程
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        //调用awaitNanos方法等待固定时间后，将被唤醒
                        try {
                            available.awaitNanos(delay);
                        } finally {
                            //任务等待完毕，leader线程被唤醒，下个循环将返回节点，此时将leader设置为null
                            if (leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }
        } finally {
            //队列非空时，unlock前 随机唤醒等待条件上的任一队列
            if (queue[0] != null) {
                available.signal();
            }
            lock.unlock();
        }
    }


    /**
     * 删除f节点，将堆中尾节点设置为头节点，然后进行下沉排序
     */
    private MyScheduledFutureTask finishPoll(MyScheduledFutureTask f) {
        int s = --size;
        MyScheduledFutureTask x = queue[s];
        queue[s] = null;
        if (s != 0) {
            siftDown(0, x);
        }
        return f;
    }


    /**
     * 在堆尾部增加节点，实行堆排序的上浮操作
     */
    private void siftUp(int k, MyScheduledFutureTask key) {
        //如果子节点比父节点大，则替换
        while (k > 0) {
            int parent = (k - 1) / 2;
            MyScheduledFutureTask e = queue[parent];
            if (key.compareTo(e) >= 0) {
                break;
            }
            queue[k] = e;
            k = parent;
        }

        queue[k] = key;
    }


    /**
     * 从堆的顶部拿取节点，实现堆排序的下沉操作
     */
    private void siftDown(int k, MyScheduledFutureTask key) {
        int half = size / 2;
        while (k < half) {
            int child = (k*2) + 1;
            MyScheduledFutureTask c = queue[child];
            int right = child + 1;
            if (right < size && c.compareTo(queue[right]) > 0) {
                c = queue[child = right];
            }
            if (key.compareTo(c) <= 0) {
                break;
            }
            queue[k] = c;
            k = child;
        }

        queue[k] = key;
    }


}
