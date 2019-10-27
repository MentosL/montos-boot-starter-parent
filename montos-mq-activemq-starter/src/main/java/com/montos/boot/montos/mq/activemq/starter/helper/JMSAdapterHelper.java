package com.montos.boot.montos.mq.activemq.starter.helper;

import com.montos.boot.montos.mq.core.adapter.IMQReceiver;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JMSAdapterHelper implements BeanFactoryPostProcessor {
	
	private static final Logger log = Logger.getLogger(JMSAdapterHelper.class);
		
	private DefaultListableBeanFactory dlbf;
	
	private Map<IMQReceiver, ActiveMQConnectionFactory> mQReceiverMap = new HashMap<IMQReceiver,ActiveMQConnectionFactory>();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.dlbf = (DefaultListableBeanFactory) beanFactory;
		this.initListener();
	}
	
	public void initListener(IMQReceiver mQReceiver, ActiveMQConnectionFactory activeMQConnectionFactory){
		mQReceiverMap.put(mQReceiver, activeMQConnectionFactory);
		this.initListener();
	}
	
	private void initListener(){
		
		if(this.dlbf==null){
			return ;
		}
		Iterator<Entry<IMQReceiver, ActiveMQConnectionFactory>> it = mQReceiverMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<IMQReceiver, ActiveMQConnectionFactory> entry = it.next();
			IMQReceiver mQReceiver = entry.getKey();
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
	                .rootBeanDefinition(DefaultMessageListenerContainer.class)
	                .addPropertyValue("connectionFactory", entry.getValue())
	                .addPropertyValue("destinationName", mQReceiver.getmQName())
	                .addPropertyValue("sessionTransacted", true)
	                .addPropertyValue("cacheLevel", DefaultMessageListenerContainer.CACHE_NONE)
	                .addPropertyValue("messageListener", new SessionAwareMessageListener<TextMessage>(){

						@Override
						public void onMessage(TextMessage message, Session session) throws JMSException {
							try {
								log.info(String.format("receive a message, mqname is [%s]", mQReceiver.getmQName()));
								mQReceiver.receive(message.getText());
							} catch (Throwable e) {
								log.warn("handle massage throw a exception", e);
								throw new JMSException(e.getMessage(), e.toString());
							}
						}
	                	
	                });

			log.info(String.format("add a message listener[%s]", mQReceiver.getmQName()));
			dlbf.registerBeanDefinition(String.format("activemq-%s-clientProxy", mQReceiver.getmQName()), beanDefinitionBuilder.getBeanDefinition());
			
			it.remove();
		}
		
	}
}
