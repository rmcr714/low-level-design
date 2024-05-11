package com.lowleveldesign.concurrency.uniqueidgenerator.service;


import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.ClockMovedBackException;
import com.lowleveldesign.concurrency.uniqueidgenerator.exceptions.NodeIdOutOfBoundException;

public interface SequenceIdGenerator {
    long generateId() throws ClockMovedBackException, NodeIdOutOfBoundException;
}