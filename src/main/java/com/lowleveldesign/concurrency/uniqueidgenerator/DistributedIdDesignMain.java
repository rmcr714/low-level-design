package com.lowleveldesign.concurrency.uniqueidgenerator;


import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.ClockMovedBackException;
import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.NodeIdOutOfBoundException;
import com.lowleveldesign.concurrency.uniqueidgenerator.service.SnowflakeSequenceIdGenerator;

public class DistributedIdDesignMain {
    public static void main(String[] args)
        throws NodeIdOutOfBoundException, ClockMovedBackException {

        SnowflakeSequenceIdGenerator snowflakeSequenceIdGenerator = new SnowflakeSequenceIdGenerator();
        snowflakeSequenceIdGenerator.generateId();


    }
}