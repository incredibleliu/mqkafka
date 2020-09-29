package com.mq.demo.producer;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Future;

@Component
public class MessageProducer {

    private static final Logger log
            = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String, String> stringKafkaTemplate;

//    @Autowired
//    private KafkaProducer<String, GenericRecord> avroKafkaProducer;

    @Value(value = "${kafka.topic.name}")
    private String topicName;

//    @Value(value = "${partitioned.topic.name}")
//    private String partitionedTopicName;
//
//    @Value(value = "${filtered.topic.name}")
//    private String filteredTopicName;
//
//    @Value(value = "${greeting.topic.name}")
//    private String greetingTopicName;

    //send json
    public void sendMessage(String message) {


//        throw(new RuntimeException("for test"));

        //json
      log.info("kafkaTemplate = {}", stringKafkaTemplate);
      ListenableFuture<SendResult<String, String>> future = stringKafkaTemplate.send(topicName, message);

      future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

        @Override
        public void onSuccess(SendResult<String, String> result) {
          System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                  .offset() + "]");
        }

        @Override
        public void onFailure(Throwable ex) {
          System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
        }
      });

    }

    //send string
    public void sendAvro(GenericRecord record) {


////        throw(new RuntimeException("for test"));
//
//        //Date object
//        Date date = new Date();
//        //getTime() returns current time in milliseconds
//        long time = date.getTime();
//        //Passed the milliseconds to constructor of Timestamp class
//        Timestamp ts = new Timestamp(time);
//        System.out.println("Current Time Stamp: "+ts);
//
//        Future<RecordMetadata> future = avroKafkaProducer.send(new ProducerRecord<String, GenericRecord>(topicName, ts + "", record));
//
////        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
////
////            @Override
////            public void onSuccess(SendResult<String, String> result) {
////                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
////                        .offset() + "]");
////            }
////
////            @Override
////            public void onFailure(Throwable ex) {
////                System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
////            }
////        });

    }

//    public void sendMessageToPartition(String message, int partition) {
//      kafkaTemplate.send(partitionedTopicName, partition, null, message);
//    }
//
//    public void sendMessageToFiltered(String message) {
//      kafkaTemplate.send(filteredTopicName, message);
//    }

}