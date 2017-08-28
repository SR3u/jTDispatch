package com.marasm.jtdispatch;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by vhq473 on 08.12.2016.
 */
public class SerialQueue implements DispatchQueue {
    Queue<Block> blocks = new LinkedBlockingQueue<>();
    Lock blocksLock = new ReentrantLock(true);
    Lock executionLock = new ReentrantLock(true);
    Thread thread;

    SerialQueue(String qid) {
    }

    public static DispatchQueue get(String qid) {
        DispatchQueuePool.lock.lock();
        DispatchQueue queue = DispatchQueuePool.getQueue(qid);
        if (queue == null) {
            queue = new SerialQueue(qid);
            DispatchQueuePool.setQueue(qid, queue);
        }
        DispatchQueuePool.lock.unlock();
        return queue;
    }

    public static DispatchQueue global() {
        return new SerialQueue("tmp");
    }

    @Override
    public void async(Block b) {
        synchronized (blocks) {
            blocks.add(b);
            commit();
        }
    }

    @Override
    public void sync(Block b) {
        async(() -> {
            synchronized (this) {
                b.action();
                this.notify();
            }
        });
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void commit() {
        blocksLock.lock();
        if (thread == null) {
            thread = new QThread(this);
            thread.start();
        }
        blocksLock.unlock();
    }

    private void threadStopped() {
        blocksLock.lock();
        thread = null;
        blocksLock.unlock();
    }

    private boolean isEmpty() {
        blocksLock.lock();
        boolean res = blocks.isEmpty();
        blocksLock.unlock();
        return res;
    }

    Block getNextBlock() {
        blocksLock.lock();
        Block b = blocks.poll();
        blocksLock.unlock();
        return b;
    }

    static class QThread extends Thread {
        SerialQueue queue;

        QThread(SerialQueue q) {
            queue = q;
        }

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                Block b = queue.getNextBlock();
                queue.executionLock.lock();
                if (b != null) {
                    b.action();
                }
                queue.executionLock.unlock();
            }
            queue.threadStopped();
        }
    }
}
