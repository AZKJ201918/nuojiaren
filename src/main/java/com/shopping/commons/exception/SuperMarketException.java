package com.shopping.commons.exception;

public class SuperMarketException extends Exception {



    public SuperMarketException(String message) {
        super(message);
    }


    public int getStatusCode() {
        return 500;
    }
}
