package com.mq.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@EnableJms
@EnableTransactionManagement
@RestController
@SpringBootApplication
public class ActiveMqApplication {

    @Autowired
    JmsTemplate jmsTemplate;

    @GetMapping("send")
    String send(){
        try{
            for(int i=0; i<1; i++){
                jmsTemplate.convertAndSend("inbound.queue", "hello");
            }
            return "OK";
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }


    public static void main(String[] args) {
        log.info("ActiveMqApplication starts...");
        SpringApplication.run(ActiveMqApplication.class, args);
    }

}
