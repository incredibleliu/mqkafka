package com.mq.demo.activemq;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.jta.atomikos.AtomikosConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Configuration
@EnableTransactionManagement
public class ActiveMqConfigXA {

    String BROKER_URL = "tcp://localhost:61616";
    String BROKER_USERNAME = "admin";
    String BROKER_PASSWORD = "admin";

//    @Bean
//    public ActiveMQXAConnectionFactory connectionFactory(){
//        ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory();
//        connectionFactory.setBrokerURL(BROKER_URL);
//        connectionFactory.setPassword(BROKER_PASSWORD);
//        connectionFactory.setUserName(BROKER_USERNAME);
//        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
//        redeliveryPolicy.setMaximumRedeliveryDelay(3000);
//        redeliveryPolicy.setMaximumRedeliveries(3);
//        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
//        return connectionFactory;
//    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public ConnectionFactory xaFactory(){
        PoolingConnectionFactory poolingConnectionFactory = new PoolingConnectionFactory();
        poolingConnectionFactory.setClassName("org.apache.activemq.ActiveMQXAConnectionFactory");
        poolingConnectionFactory.setUniqueName("xaFactory");
        poolingConnectionFactory.setUser(BROKER_USERNAME);
        poolingConnectionFactory.setPassword(BROKER_PASSWORD);
        poolingConnectionFactory.setMinPoolSize(1);
        poolingConnectionFactory.setMaxPoolSize(3);
        poolingConnectionFactory.setAllowLocalTransactions(true);
        return poolingConnectionFactory;
    }

    @Bean
    org.springframework.jms.connection.SingleConnectionFactory singleConnectionFactory(){
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(xaFactory());
        return singleConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws Throwable{
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(singleConnectionFactory());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(false);
        factory.setReceiveTimeout((long) 3000);
        factory.setTransactionManager(platformTransactionManager());
        return factory;
    }

    @Bean
    public TransactionManager transactionManager() {
        return TransactionManagerServices.getTransactionManager();
    }

    @Bean
    public UserTransaction userTransaction() {
        return TransactionManagerServices.getTransactionManager();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();
        TransactionManager transactionManager = transactionManager();
        return new JtaTransactionManager(userTransaction, transactionManager);
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(xaFactory());
        return template;
    }

}