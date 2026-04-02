package com.pm.paymentservice.exception;

public class BankTimeoutException extends RuntimeException {

    public BankTimeoutException(){
        super();
    }

    public BankTimeoutException(String message){
        super(message);
    }


}
