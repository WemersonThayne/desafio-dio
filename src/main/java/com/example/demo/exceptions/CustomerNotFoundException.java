package com.example.demo.exceptions;

public class CustomerNotFoundException extends  RuntimeException {

    public  CustomerNotFoundException(String msg){
        super(msg);
    }

}
