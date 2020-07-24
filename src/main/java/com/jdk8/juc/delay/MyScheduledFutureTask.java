package com.jdk8.juc.delay;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author madongyu
 * @projectName jdk8-juc
 * @description: TODO
 * @date 2020/7/2011:35
 */
public class MyScheduledFutureTask implements Runnable, Delayed {

    /** 任务触发时间的纳秒值 */
    private long time;

    /** 循环间隔的纳秒值 */
    private final long period;

    /** 线程池中的队列 */
    private BlockingQueue queue;

    /** 执行任务 */
    private Runnable task;

    public MyScheduledFutureTask(Runnable r, long time, int period, BlockingQueue<Runnable> queue) {
        this.task = r;
        this.time = time;
        this.period = period;
        this.queue = queue;
    }

    /**
     * 自定义任务队列实现了堆数据结构，此方法用于堆插入或者删除时的堆排序
     */
    @Override
    public int compareTo(Delayed o) {
        MyScheduledFutureTask x = (MyScheduledFutureTask)o;
        long diff = time - x.time;
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 1;
        }
    }

    /**
     *  获取触发时间与当前时间的时间差
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - System.nanoTime(), NANOSECONDS);
    }

    @Override
    public void run() {
        //执行逻辑
        task.run();
        //任务执行结束后，将下次任务触发的时间增加一周期
        time += TimeUnit.SECONDS.toNanos((long)period);
        //重新往线程池队列中加入此任务
        queue.add(this);
    }

}
