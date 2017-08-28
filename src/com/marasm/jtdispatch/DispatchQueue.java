package com.marasm.jtdispatch;

/**
 * Created by vhq473 on 08.12.2016.
 */

public interface DispatchQueue {
    static interface Block {
        void action();
    }


    static DispatchQueue get(String qid) {
        return SerialQueue.get(qid);
    }

    static DispatchQueue global() {
        return SerialQueue.global();
    }

    void async(Block b);

    void sync(Block b);
}
