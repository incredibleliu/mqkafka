package com.mq.demo.activemq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.sql.Timestamp;

@Slf4j
@Component
@EnableTransactionManagement
public class Listener {

    @JmsListener(destination = "inbound.queue")
    @Transactional(rollbackFor = {KafkaNAException.class},
            timeout = 100000,
            value = "jtaTransactionManager")
    public String receiveMessage(final Message jsonMessage) throws JMSException {
        String messageData = null;
        String response = "ok";
        if(jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage)jsonMessage;
            messageData = textMessage.getText();
            log.info("### Received message " + messageData);

            //try catch NullPointerException
            try{
                throw new NullPointerException();
            } catch(NullPointerException e){
                e.printStackTrace();
            }

        }
        return response;
    }

}