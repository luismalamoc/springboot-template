package com.example.springboottemplate.exception;

/**
 * Exception thrown when a server returns a 5xx error code
 */
public class ServerErrorException extends RuntimeException {
    private final int statusCode;

    public ServerErrorException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
