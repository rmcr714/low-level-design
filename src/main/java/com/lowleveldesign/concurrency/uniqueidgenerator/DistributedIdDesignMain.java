package com.lowleveldesign.concurrency.uniqueidgenerator;


import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.ClockMovedBackException;
import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.NodeIdOutOfBoundException;
import com.lowleveldesign.concurrency.uniqueidgenerator.service.SnowflakeSequenceIdGenerator;
import java.util.HashSet;
import java.util.Set;

public class DistributedIdDesignMain {
    public static void main(String[] args)
        throws NodeIdOutOfBoundException, ClockMovedBackException {

        SnowflakeSequenceIdGenerator snowflakeSequenceIdGenerator = new SnowflakeSequenceIdGenerator();

        Set<Long> set = new HashSet<>();

        for(int i = 0;i<5000;i++) {
            long value = snowflakeSequenceIdGenerator.generateId();
            set.add(value);
        }

          //Used to check that we generate random numbers
          System.out.println(set.size());



    }
}