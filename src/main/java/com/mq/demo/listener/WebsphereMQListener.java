package com.mq.demo.listener;

import com.mq.demo.avro.AvroTransformer;
import com.mq.demo.producer.AvroProducer;
import com.mq.demo.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.transaction.Transactional;

@Component
public class WebsphereMQListener implements MessageListener {

  private static final Logger log
          = LoggerFactory.getLogger(WebsphereMQListener.class);

  MessageProducer messageProducer;
  AvroProducer avroProducer;

  @Autowired
  public WebsphereMQListener(MessageProducer messageProducer,
                             AvroProducer avroProducer){
    this.messageProducer = messageProducer;
    this.avroProducer = avroProducer;

  }

//  @Autowired
//  AvroTransformer avroTransformer;



//  @Bean
//  public MessageProducer messageProducer() {
//    return new MessageProducer();
//  }

  @Override
  @Transactional
  public void onMessage(Message message) {
    try {
      if (message instanceof TextMessage) {
        TextMessage textMessage = (TextMessage) message;
        String stringMessage = textMessage.getText();
        System.out.println("onMessage receive: " + stringMessage  );

        //xml to json
//        XmlMapper xmlMapper = new XmlMapper();
//        JsonNode node = xmlMapper.readTree(stringMessage.getBytes());
//        ObjectMapper jsonMapper = new ObjectMapper();
//        String json = jsonMapper.writeValueAsString(node);
//        System.out.println("json: " + json);

        //xml to avro
//        AvroTransformer avroTransformer = new AvroTransformer();
//        System.out.println("avroTransformer: " + avroTransformer);
//        GenericRecord record = avroTransformer.xmlToGenericRecord(stringMessage);
//        System.out.println("########################");
//        System.out.println(record.toString());
//        System.out.println("########################");
//        //messageProducer.sendAvro(record);
//        avroProducer.sendMessage(record);

        //string to avro
        AvroTransformer avroTransformer = new AvroTransformer();
        avroProducer.sendMessage(avroTransformer.xmlToByteArray(stringMessage));

        //send to kafka
//        log.info("messageProducer = {}", messageProducer);
//        messageProducer.sendMessage(json);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}