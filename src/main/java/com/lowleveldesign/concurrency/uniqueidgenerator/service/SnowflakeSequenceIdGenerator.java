package com.lowleveldesign.concurrency.uniqueidgenerator.service;



import static com.lowleveldesign.concurrency.uniqueidgenerator.common.Constants.NODE_ID_BIT_LEN;
import static com.lowleveldesign.concurrency.uniqueidgenerator.common.Constants.SEQUENCE_BIT_LEN;

import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.ClockMovedBackException;
import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.NodeIdOutOfBoundException;
import java.time.Instant;

/**
 * This class generates a unique sortable sequence number or id
 * It is 64 bit long
 * <p>
 * The first 41 bits representing the time in milliseconds. U would doubt why in code we shifting 22 places instead of 41
 * That is because if u take the current epoch time in millis and then left shift 22 and then convert to binary u get 63 bits
 * </p>
 * The second 10 bits represent the node id i.e. the server unique identifier or some random number
 * The third 12 bit is a sequence number which can be between 0 and 4096 and is reset every millisecond
 * <p>
 * So say 4096 requests are given at the same time. So we would get
 * eg current_epoch_time_in_decimal left shifted 22 places  - node id in decimal left shifted 10 places - sequence number between (0,1..4096)
 * <p>
 *  If we don't get like 4096 request per millisecond then the time stamp will increment and we get a new unique integer -happy and most real path
 * <p>
 *  This solution is scalable as we can deploy many such servers and load balance between them when we get too many requests
 * <p>
 *  LOGS are commented out , can use to see it in action
 * **/


public class SnowflakeSequenceIdGenerator implements SequenceIdGenerator {

    private final int generatingNodeId = 23;


    private final int maxSequence = (int) Math.pow(2,SEQUENCE_BIT_LEN);  //Gives 4096
    private final int maxNodeVal = (int) Math.pow(2,NODE_ID_BIT_LEN);    //Gives 1024
    private final long EPOCH_START = 1710141772000L; //simulate the server start time i.e. 2nd May 2024


    private volatile long currentSequence = -1L;
    private final Object lock = new Object();
    private volatile long lastTimestamp = -1L;


    public void checkNodeIdBounds() throws NodeIdOutOfBoundException {
        if (generatingNodeId < 0 || generatingNodeId > maxNodeVal) {
            throw new NodeIdOutOfBoundException("Node id is < 0 or > " + maxNodeVal);
        }
    }

    @Override
    public long generateId() throws ClockMovedBackException, NodeIdOutOfBoundException {
        checkNodeIdBounds();
        synchronized (lock) {
            long currentTimeStamp = getTimeStamp();
            if (currentTimeStamp < lastTimestamp) {
                throw new ClockMovedBackException("Clock moved back");
            }
            if (currentTimeStamp == lastTimestamp) {

                /**
                 * This is used to check if the currentSequence is less than the max allowed sequence value of 4096
                 * So the current thing is we can generate 4096 incremental sequences per millisecond
                 * So say we got 4098 requests in a single millisecond which btw is almost impossible
                 * Then this block would be entered 4096 times as the lastTimeStamp would be same.
                 * But when the sequence goes beyond 4096 it means we have exhausted all the sequence
                 * numbers and should increment time and reset the sequence number back to 0;
                 * **/
//                System.out.println(" The times are same ");
                currentSequence = (currentSequence + 1) ;
                if (currentSequence > maxSequence) {
                    //System.out.println("Sequence being reset ");
                    currentTimeStamp = waitNextMillis(currentTimeStamp);
                }
            } else {
//                System.out.println(" Different times ");
                currentSequence = 0;
            }
            lastTimestamp = currentTimeStamp;
            long id = currentTimeStamp << (NODE_ID_BIT_LEN + SEQUENCE_BIT_LEN);
//            System.out.println("The time is "+id+" the nodeID is "+generatingNodeId+" the sequence number is "+currentSequence);
            long nodeId = ((long) generatingNodeId << SEQUENCE_BIT_LEN);
            id |= nodeId;
            id |= currentSequence;
//            System.out.println("id is "+id);
            return id;
        }
    }

    private long getTimeStamp() {
        return Instant.now().toEpochMilli() - EPOCH_START;
    }

    private long waitNextMillis(long currentTimeStamp) {
        while (currentTimeStamp == lastTimestamp) {
            currentTimeStamp = getTimeStamp();
        }
        return currentTimeStamp;
    }

}