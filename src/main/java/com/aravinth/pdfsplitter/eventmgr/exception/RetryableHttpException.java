package com.aravinth.pdfsplitter.eventmgr.exception;


import org.springframework.web.client.HttpServerErrorException;

public class RetryableHttpException extends RuntimeException{
    public RetryableHttpException (String message, HttpServerErrorException e){
        super(message ,e);
    }
}
