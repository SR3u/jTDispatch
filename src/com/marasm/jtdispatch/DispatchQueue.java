package com.marasm.jtdispatch;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;


/**
 * Created by vhq473 on 08.12.2016.
 */

public interface DispatchQueue
{
    static interface Block {void action();}

    static DispatchQueue get(String qid) {return null;}

    void async(Block b);
    void sync(Block b);
    boolean isEmpty();
}
