package com.lipi.notifica.database;

public class HttpNotOkException extends Exception {
    public final int status;
    public HttpNotOkException(int statusCode) {
        super("Http response is NOT OK with status code: " + statusCode);
        status = statusCode;
    }
}
