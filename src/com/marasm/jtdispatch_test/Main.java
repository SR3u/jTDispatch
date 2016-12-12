package com.marasm.jtdispatch_test;

import com.marasm.jtdispatch.ConcurrentQueue;
import com.marasm.jtdispatch.DispatchQueue;
import com.marasm.jtdispatch.DispatchQueuePool;
import com.marasm.jtdispatch.SerialQueue;

public class Main {

    public static void main(String[] args)
    {
        System.out.println("Default concurrent queue threads: "+ ConcurrentQueue.defaultConcurentThreadsCount());
        final DispatchQueue q = SerialQueue.get("com.marasm.jtdispatch_test.testSerialQueue-0");
        System.out.println("com.marasm.jtdispatch_test.testSerialQueue-0");
        q.async(()->{
            q.sync(()->{
                System.out.println("4");
            });
            System.out.println("3");
        });
        System.out.println("1");
        q.sync(()->{
            System.out.println("5");
        });
        System.out.println("com.marasm.jtdispatch_test.testConcurrentQueue-0 (4 threads)");
        final DispatchQueue cq = ConcurrentQueue.get("com.marasm.jtdispatch_test.testConcurrentQueue-0", 4);
        for (int i = 0; i < 4; i++)
        {
            final int idx = i;
            cq.async(()->{
                try {
                    System.out.println(idx+" sleep for 1 second");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        cq.sync(()->{
            System.out.println("5");
        });
        System.out.println("total queues: "+DispatchQueuePool.getQueuesCount());
        System.out.println(DispatchQueuePool.getQueueIDs());
    }
}
