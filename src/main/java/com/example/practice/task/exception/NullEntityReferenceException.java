package com.example.practice.task.exception;

public class NullEntityReferenceException extends RuntimeException {
    public NullEntityReferenceException() {
    }

    public NullEntityReferenceException(String message) {
        super(message);
    }
}
