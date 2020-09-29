package com.mq.demo.producer;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class AvroProducer {

    private static final Logger log
            = LoggerFactory.getLogger(AvroProducer.class);

    @Value(value = "${kafka.topic.name}")
    private String topicName;

//    @Autowired
//    private final KafkaTemplate<String, GenericRecord> avroKafkaTemplate;
//
//    @Autowired
//    public AvroProducer(KafkaTemplate<String, GenericRecord> kafkaTemplate) {
//        this.avroKafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendMessage(GenericRecord record) {
//        //Date object
//        Date date = new Date();
//        //getTime() returns current time in milliseconds
//        long time = date.getTime();
//        //Passed the milliseconds to constructor of Timestamp class
//        Timestamp ts = new Timestamp(time);
//        System.out.println("Current Time Stamp: "+ts);
//
//        System.out.println("this.kafkaTemplate: " + this.avroKafkaTemplate);
//        this.avroKafkaTemplate.send(topicName,
//                                ts+"",
//                                    record);
//        log.info(String.format("Produced genericRecord -> %s", record));
//    }

    @Autowired
    private final KafkaTemplate<String, byte[]> avroKafkaTemplate;

    @Autowired
    public AvroProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {

        this.avroKafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(byte[] bytes) {
        //Date object
        Date date = new Date();
        //getTime() returns current time in milliseconds
        long time = date.getTime();
        //Passed the milliseconds to constructor of Timestamp class
        Timestamp ts = new Timestamp(time);
        System.out.println("Current Time Stamp: "+ts);

        System.out.println("this.kafkaTemplate: " + this.avroKafkaTemplate);
        this.avroKafkaTemplate.send(topicName,
                ts+"",
                bytes);
        log.info(String.format("Produced byte[] -> %s", bytes));
    }


}
