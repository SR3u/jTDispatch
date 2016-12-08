package com.marasm.jtdispatch_test;

import com.marasm.jtdispatch.DispatchQueue;
import com.marasm.jtdispatch.SerialQueue;

public class Main {

    public static void main(String[] args)
    {
        DispatchQueue q = SerialQueue.get("com.marasm.jtdispatch_test.testSerialQueue-0");

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

    }
}
