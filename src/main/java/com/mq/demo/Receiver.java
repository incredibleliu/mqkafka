//package com.mq.demo;
//
//import java.sql.Timestamp;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//
//import org.apache.kafka.clients.producer.*;
//import org.springframework.stereotype.Component;
//
//@Component
//public class Receiver {
//
//    private CountDownLatch latch = new CountDownLatch(1);
//    Properties props;
//    KafkaProducer<String, String> producer;
//
//    public Receiver(){
//        props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ProducerConfig.ACKS_CONFIG, "all");
//        props.put(ProducerConfig.RETRIES_CONFIG, 0);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "MqAdaptor");
//        //Only one in-flight messages per Kafka broker connection
//        // - max.in.flight.requests.per.connection (default 5)
//        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
//        //Set the number of retries - retries
//        props.put(ProducerConfig.RETRIES_CONFIG, 3);
//        //Request timeout - request.timeout.ms
//        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);
//        //Only retry after one second.
//        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
//
//        producer = new KafkaProducer<>(props);
//
//    }
//
//    public void receiveMessage(String message) {
//        System.out.println("Received <" + message + "> @" + new Timestamp(System.currentTimeMillis()));
//        latch.countDown();
//
//        ProducerRecord<String, String> data = new ProducerRecord<String, String>
//                ("rabbitmqkafka", "rabbitmqkafka", message );
//
//        producer.send(data, new Callback() {
//            @Override
//            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//
//                if (e != null) {
//                    System.out.println("Error while producing message to topic :" + recordMetadata);
//                    e.printStackTrace();
//
//                } else {
//                    String message = String.format("sent message to topic:%s partition:%s  offset:%s",
//                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
//                    System.out.println(message);
//
//                }
//
//            }
//        });
//
//    }
//
//    public CountDownLatch getLatch() {
//        return latch;
//    }
//
//}