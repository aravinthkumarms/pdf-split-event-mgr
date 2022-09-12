package com.aravinth.pdfsplitter.eventmgr.utils;


import com.aravinth.pdfsplitter.eventmgr.exception.RetryableHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class HTTPAgent {

    private static final List<HttpStatus> retryableHttpErrors=
            Arrays.asList(HttpStatus.BAD_GATEWAY,HttpStatus.SERVICE_UNAVAILABLE,HttpStatus.GATEWAY_TIMEOUT);

    private final RestTemplate restTemplate;

    @Autowired
    public HTTPAgent(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000,multiplier = 2)
    )
    public <T> ResponseEntity<T> get(String url,Class <T> responseType,Object... uriVaribales){
        ResponseEntity<T> response = null;
        try{
            response = restTemplate.getForEntity(url,responseType,uriVaribales);
        }catch (HttpServerErrorException e){
            log.error("Error during API call {}",e.getMessage());
            if(retryableHttpErrors.contains(e.getStatusCode()))
                throw new RetryableHttpException("HTTP server Error",e);
            throw e;
        }
        return response;
    }


    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000,multiplier = 2)
    )
    public <T> ResponseEntity<T> post(String url,Object requestObject,Class <T> responseType){
        ResponseEntity<T> response;
        try{
            response = restTemplate.postForEntity(url,requestObject,responseType);
        }catch (HttpServerErrorException  |ResourceAccessException e){
            log.error("Error during API call {}",e.getMessage());
            throw e;
        }
        return response;
    }
}
