package com.mq.demo.activemq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class Service {

    @Retryable( value = RuntimeException.class,
                maxAttempts = 3,
                backoff = @Backoff(delay = 10000) )
    @Transactional
    public void execute(){

            log.info("### retry...");

            //try catch NullPointerException
//            try{
//                throw new NullPointerException();
//            } catch(Throwable e){
//                e.printStackTrace();
//            }

            try{
                throw new CheckedException();
            } catch(Throwable e){
                System.out.println("### caught CheckedException ...");
            }

            //throw new NullPointerException();

            //NotComponent.execute();

    }

}
