package com.lowleveldesign.concurrency.uniqueidgenerator.exceptions;

public class ClockMovedBackException extends Exception {
    public ClockMovedBackException(String message) {
        super(message);
    }
}