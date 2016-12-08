package com.marasm.jtdispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by vhq473 on 08.12.2016.
 */
public class DispatchQueuePool
{
    private static Map<String,DispatchQueue> queues = new HashMap<>();

    public final static Lock lock = new ReentrantLock();

    public static DispatchQueue getQueue(String qid)
    {
        synchronized (queues) {
            return queues.get(qid);
        }
    }

    public static void setQueue(String qid, DispatchQueue q)
    {
        synchronized (queues) {
            queues.put(qid,q);
        }
    }

    public static Set<String> getQueueIDs()
    {
        Set<String> queueIDs;
        lock.lock();
        queueIDs = queues.keySet();
        lock.unlock();
        return queueIDs;
    }
    public static int getQueuesCount()
    {
        return getQueueIDs().size();
    }

}
