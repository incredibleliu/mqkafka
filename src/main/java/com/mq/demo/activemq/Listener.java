package com.mq.demo.activemq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Slf4j
@Component
//@EnableTransactionManagement
@RequiredArgsConstructor
public class Listener {

    private final Service service;

    @JmsListener(destination = "inbound.queue")
    //@Transactional(rollbackFor = {KafkaNAException.class}, timeout = 100000, value = "jtaTransactionManager")
    //@Transactional(timeout = 100000, value = "jtaTransactionManager")
    public String receiveMessage(final Message jsonMessage) throws JMSException {
        String messageData = null;
        String response = "ok";
        if(jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage)jsonMessage;
            messageData = textMessage.getText();
            log.info("### Received message " + messageData);

//            //try catch NullPointerException
//            try{
//                throw new NullPointerException();
//            } catch(NullPointerException e){
//                e.printStackTrace();
//            }

            service.execute();

        }
        return response;
    }

}