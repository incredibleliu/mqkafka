package com.mq.demo;

import com.ibm.mq.jms.MQConnectionFactory;
import com.mq.demo.listener.WebsphereMQListener;
import com.mq.demo.producer.AvroProducer;
import com.mq.demo.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.jms.JMSException;

@SpringBootApplication
@RestController
@EnableJms
public class MQSpringApplication {

    private static final Logger log
            = LoggerFactory.getLogger(MQSpringApplication.class);

    private static final String queueName = "DEV.QUEUE.1";

    private static final String xml = "<?xml version='1.0' standalone='yes'?>\n" +
            "<movies>\n" +
            " <movie>\n" +
            "  <title>PHP: Behind the Parser</title>\n" +
            "  <characters>\n" +
            "   <character>\n" +
            "    <name>Ms. Coder</name>\n" +
            "    <actor>Onlivia Actora</actor>\n" +
            "   </character>\n" +
            "   <character>\n" +
            "    <name>Mr. Coder</name>\n" +
            "    <actor>El Act&#211;r</actor>\n" +
            "   </character>\n" +
            "  </characters>\n" +
            "  <plot>\n" +
            "   So, this language. It's like, a programming language. Or is it a\n" +
            "   scripting language? All is revealed in this thrilling horror spoof\n" +
            "   of a documentary.\n" +
            "  </plot>\n" +
            "  <great-lines>\n" +
            "   <line>PHP solves all my web problems</line>\n" +
            "  </great-lines>\n" +
            "  <rating type=\"thumbs\">7</rating>\n" +
            "  <rating type=\"stars\">5</rating>\n" +
            " </movie>\n" +
            "</movies>";

    @Value("${ibm.mq.queueManager}")
    private String qm;

    @Value("${ibm.mq.ip}")
    private String ip;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.user}")
    private String user;

    @Value("${ibm.mq.password}")
    private String password;

    @Autowired
     private JmsTemplate jmsTemplate;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    AvroProducer avroProducer;

     public static void main(String[] args) {

         log.info("MqspringApplication starts...");
         SpringApplication.run(MQSpringApplication.class, args);
     }

    @Bean
    public MQConnectionFactory mqConnectionFactory(){
        MQConnectionFactory connectionFactory = new MQConnectionFactory();
        try {

            connectionFactory.setHostName(ip); //mq host name
            connectionFactory.setPort(port); // mq port
            connectionFactory.setQueueManager(qm); //mq queue manager
            connectionFactory.setChannel(channel); //mq channel name
            connectionFactory.setTransportType(1);
            //connectionFactory.setSSLCipherSuite(); //tls cipher suite name

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connectionFactory;
    }

    @Primary
    @Bean
    @DependsOn(value = { "mqConnectionFactory" })
    UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
            MQConnectionFactory mqQueueConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setUsername(user);
        userCredentialsConnectionFactoryAdapter.setPassword(password);
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqConnectionFactory());
        return userCredentialsConnectionFactoryAdapter;
    }


    @Bean
    public DefaultMessageListenerContainer myMessageEventContainer() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setAutoStartup(true);
        container.setConnectionFactory(userCredentialsConnectionFactoryAdapter(mqConnectionFactory()));
        container.setDestinationName(queueName);
        //container.setMessageListener(new MqListener());
        log.info("messageProducer = {}", messageProducer);
        container.setMessageListener(new WebsphereMQListener(messageProducer, avroProducer));
        return container;
    }

    @GetMapping("send")
    String send(){
        try{
            jmsTemplate.convertAndSend(queueName, xml);
            return "OK";
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }

    @GetMapping("recv")
    String recv(){
        try{
            return jmsTemplate.receiveAndConvert(queueName ).toString();
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }

}