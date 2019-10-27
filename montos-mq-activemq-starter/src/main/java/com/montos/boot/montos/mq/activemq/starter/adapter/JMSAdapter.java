package com.montos.boot.montos.mq.activemq.starter.adapter;

import com.montos.boot.montos.mq.activemq.starter.helper.JMSAdapterHelper;
import com.montos.boot.montos.mq.core.adapter.IMQAdapter;
import com.montos.boot.montos.mq.core.adapter.IMQReceiver;
import com.montos.boot.montos.mq.core.adapter.MQMessage;
import com.montos.boot.montos.mq.core.api.enums.QueueType;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * jms协议的适配器
 */
public class JMSAdapter implements IMQAdapter {

    private static final Logger log = Logger.getLogger(JMSAdapter.class);

    MyActiveMQProperties myActiveMQProperties;

    JmsMessagingTemplate jmsMessagingTemplate;

    ActiveMQConnectionFactory activeMQConnectionFactory;

    JMSAdapterHelper jMSAdapterHelper;

    public JMSAdapter setjMSAdapterHelper(JMSAdapterHelper jMSAdapterHelper) {
        this.jMSAdapterHelper = jMSAdapterHelper;
        return this;
    }


    public JMSAdapter setMyActiveMQProperties(MyActiveMQProperties myActiveMQProperties) {
        this.myActiveMQProperties = myActiveMQProperties;
        return this;
    }

    public JMSAdapter init() {

        activeMQConnectionFactory = new ActiveMQConnectionFactory(
                myActiveMQProperties.getUser(),
                myActiveMQProperties.getPassword(),
                myActiveMQProperties.getBrokerUrl());

        activeMQConnectionFactory.setUseAsyncSend(true);

        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);

        if (myActiveMQProperties.getPool() != null) {
            ActiveMQProperties.Pool pool = myActiveMQProperties.getPool();
            pooledConnectionFactory.setBlockIfSessionPoolIsFull(pool.isBlockIfFull());
            pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(pool.getBlockIfFullTimeout());
            pooledConnectionFactory.setCreateConnectionOnStartup(pool.isCreateConnectionOnStartup());
            pooledConnectionFactory.setExpiryTimeout(pool.getExpiryTimeout());
            pooledConnectionFactory.setIdleTimeout(pool.getIdleTimeout());
            pooledConnectionFactory.setMaxConnections(pool.getMaxConnections());
            pooledConnectionFactory.setMaximumActiveSessionPerConnection(pool.getMaximumActiveSessionPerConnection());
            pooledConnectionFactory.setReconnectOnException(pool.isReconnectOnException());
            pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(pool.getTimeBetweenExpirationCheck());
            pooledConnectionFactory.setUseAnonymousProducers(pool.isUseAnonymousProducers());
        }

        jmsMessagingTemplate = new JmsMessagingTemplate();
        jmsMessagingTemplate.setConnectionFactory(pooledConnectionFactory);

        log.info(String.format("activemq client [%s] started", myActiveMQProperties.getKey()));

        return this;
    }


    @Override
    public void send(MQMessage msg) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("send a message , maname is [%s]", msg.getmQName()));
        }
        jmsMessagingTemplate.getJmsTemplate().setPubSubDomain(QueueType.Topic.equals(msg.getQueueType()));
        jmsMessagingTemplate.getJmsTemplate().send(msg.getmQName(), new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(msg.getContent().toString());
                if (msg.getDelayTime() > 0) {
                    message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, msg.getDelayTime());
                }
                return message;
            }

        });
    }

    @Override
    public void listener(final IMQReceiver mQReceiver) {
        jMSAdapterHelper.initListener(mQReceiver, activeMQConnectionFactory);
    }

}
