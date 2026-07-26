package com.autocarepro.exception;

/** Thrown for expected business-rule violations (bad input, duplicate data, etc.). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
