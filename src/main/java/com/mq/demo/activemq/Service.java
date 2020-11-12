package com.mq.demo.activemq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Service {

    @Retryable( value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void execute(){

            log.info("### retry...");
            //try catch NullPointerException
            try{
                throw new NullPointerException();
            } catch(NullPointerException e){
                System.out.println("### caught Exception ...");
            }
//            throw new NullPointerException();

    }

}
