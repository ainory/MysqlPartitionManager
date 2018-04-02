package com.ainory.dev.partition;

import com.ainory.dev.partition.worker.PartitionManagerWorker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ainory on 2016. 9. 13..
 */
public class PartitionManagerMain {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        PartitionManagerWorker partitionManagerWorker = (PartitionManagerWorker) applicationContext.getBean("partitionManagerWorker");
        partitionManagerWorker.executeRunner();
    }
}
