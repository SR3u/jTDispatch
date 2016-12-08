package com.marasm.jtdispatch;

import java.util.ArrayList;

/**
 * Created by vhq473 on 08.12.2016.
 */
public class ConcurentQueue implements DispatchQueue
{
    int maxConcurentThreads = 2;
    ArrayList<DispatchQueue> queues = new ArrayList<>();

    int currentidx = 0;

    private ConcurentQueue(String qid, int maxConcurentThreads)
    {
        this.maxConcurentThreads = maxConcurentThreads;
        for(int i = 0; i < maxConcurentThreads; i++)
        {
            queues.add(SerialQueue.get(qid+".executor_"+i));
        }
    }

    public static DispatchQueue get(String qid)
    {
        return get(qid,2);
    }
    public static DispatchQueue get(String qid, int maxConcurentThreads)
    {
        DispatchQueuePool.lock.lock();
        DispatchQueue queue = DispatchQueuePool.getQueue(qid);
        if(queue == null)
        {
            queue = new ConcurentQueue(qid, maxConcurentThreads);
            DispatchQueuePool.setQueue(qid,queue);
        }
        DispatchQueuePool.lock.unlock();
        return queue;
    }

    @Override
    public void async(Block b)
    {
        synchronized (queues)
        {
            queues.get(currentidx).async(b);
            currentidx++;
            currentidx = currentidx % maxConcurentThreads;
        }
    }

    @Override
    public void sync(Block b)
    {
        synchronized (queues)
        {
            sync(b,0);
        }
    }

    private void sync(Block b, int depth)
    {
        if(depth < maxConcurentThreads)
        {
            queues.get(depth).sync(()->{
                sync(b,depth+1);
            });
        }
        else
        {
            b.action();
        }
    }
}
