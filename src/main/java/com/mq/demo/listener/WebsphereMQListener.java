//package com.mq.demo.listener;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.jms.Message;
//import javax.jms.MessageListener;
//import javax.jms.TextMessage;
//import javax.transaction.Transactional;
//import java.sql.Timestamp;
//
//@Component
//@Slf4j
//public class WebsphereMQListener implements MessageListener {
//
//  @Override
//  @Transactional
//  public void onMessage(Message message) {
//    Timestamp ts = null;
//    try {
//      if (message instanceof TextMessage) {
//        ts = new Timestamp(System.currentTimeMillis());
//        TextMessage textMessage = (TextMessage) message;
//        String stringMessage = textMessage.getText();
//        log.info("onMessage receive at {} : {}", ts, stringMessage);
//
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//}