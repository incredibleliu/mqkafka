package com.mq.demo.activemq;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.jta.atomikos.AtomikosConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.transaction.SystemException;

@Configuration
@EnableTransactionManagement
public class ActiveMqConfigNoXA {

    String BROKER_URL = "tcp://localhost:61616";
    String BROKER_USERNAME = "admin";
    String BROKER_PASSWORD = "admin";

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setPassword(BROKER_USERNAME);
        connectionFactory.setUserName(BROKER_PASSWORD);
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveryDelay(3000);
        redeliveryPolicy.setMaximumRedeliveries(3);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        return connectionFactory;
    }

//    @Bean(initMethod = "init")
//    public ConnectionFactory xaFactory(){
//        AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
//        atomikosConnectionFactoryBean.setLocalTransactionMode(false);
//        atomikosConnectionFactoryBean.setPoolSize(30);
//        atomikosConnectionFactoryBean.setUniqueResourceName("xaFactory");
//        atomikosConnectionFactoryBean.setXaConnectionFactory(connectionFactory());
//        return atomikosConnectionFactoryBean;
//    }

    @Bean
    org.springframework.jms.connection.SingleConnectionFactory singleConnectionFactory(){
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(connectionFactory());
        return singleConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws SystemException{
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(singleConnectionFactory());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setReceiveTimeout((long) 3000);
        return factory;
    }

//    @Bean(initMethod = "init", destroyMethod = "close")
//    public UserTransactionManager userTransactionManager() throws SystemException {
//        UserTransactionManager userTransactionManager = new UserTransactionManager();
//        userTransactionManager.setTransactionTimeout(30000);
//        userTransactionManager.setForceShutdown(false);
//        return userTransactionManager;
//    }
//
//    @Bean
//    public UserTransactionImp userTransaction() throws SystemException {
//        UserTransactionImp userTransaction = new UserTransactionImp();
//        userTransaction.setTransactionTimeout(30000);
//        return userTransaction;
//    }
//
//    @Bean
//    public JtaTransactionManager jtaTransactionManager() throws SystemException{
//        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
//        jtaTransactionManager.setTransactionManager(userTransactionManager());
//        jtaTransactionManager.setUserTransaction(userTransaction());
//        return jtaTransactionManager;
//    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

}