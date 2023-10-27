package com.care4u.exception;

public class OutOfStockException extends RuntimeException{

    public OutOfStockException(String message) {
        super(message);
    }

}