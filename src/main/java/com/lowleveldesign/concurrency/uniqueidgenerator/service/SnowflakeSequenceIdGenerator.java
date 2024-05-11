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
 *
 * **/


public class SnowflakeSequenceIdGenerator implements SequenceIdGenerator {

    private int generatingNodeId = 250;

    private final int maxSequence = (int) Math.pow(2, SEQUENCE_BIT_LEN);
    private final int maxNodeVal = (int) Math.pow(2, NODE_ID_BIT_LEN);
    private final long EPOCH_START = 1710141772000l; //simulate the server start time i.e. 2nd May 2024


    private volatile long currentSequence = -1L;
    private volatile long isSequenceOverflow = 0;
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
                 * Then this block would be entered 4098 times as the lastTimeStamp is same.
                 * But when the sequence goes beyond 4096 the & operation gives value greater than 0
                 * and this indicates that we have exhausted all the sequence numbers and should increment time and
                 * reset the sequence number back to 0;
                 *
                 * **/
                currentSequence += 1;
                isSequenceOverflow = currentSequence & maxSequence;
                if (isSequenceOverflow != 0) {
                    currentTimeStamp = waitNextMillis(currentTimeStamp);
                    currentSequence = 0;
                }
            } else {
                currentSequence = 0;
            }
            lastTimestamp = currentTimeStamp;
            long id = currentTimeStamp << (NODE_ID_BIT_LEN + SEQUENCE_BIT_LEN);
            long nodeId = ((long) generatingNodeId << 2);
            id |= nodeId;
            id |= currentSequence;
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