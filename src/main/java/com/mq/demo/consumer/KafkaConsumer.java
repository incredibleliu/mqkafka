package com.mq.demo.consumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import com.mq.demo.avro.AvroTransformer;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    AvroTransformer at;

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

//    @KafkaListener(topics = "${kafka.topic.name}")
//    public void receive(GenericRecord record) {
//        LOGGER.info("Receiver received record='{}'", record.toString());
//        latch.countDown();
//    }

    @KafkaListener(topics = "${kafka.topic.name}")
    public void receive(byte[] bytes) {
        LOGGER.info("Receiver received Byte[]='{}'", bytes);
        File file = new File("e:/xmlreceive.avro");
        try{
            OutputStream os
                    = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(bytes);
            System.out.println("Successfully" + " byte inserted");

            // Close the file
            os.close();
            latch.countDown();


            at.avroFileToXmlFile(file, new File("e:/xmlreceive.xml"));

        } catch(Exception e){
            e.printStackTrace();
        }

    }
}