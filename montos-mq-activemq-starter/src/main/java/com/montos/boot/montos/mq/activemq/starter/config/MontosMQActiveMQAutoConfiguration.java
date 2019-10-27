package com.montos.boot.montos.mq.activemq.starter.config;

import com.montos.boot.montos.mq.activemq.starter.adapter.JMSAdapter;
import com.montos.boot.montos.mq.activemq.starter.adapter.MyActiveMQProperties;
import com.montos.boot.montos.mq.activemq.starter.helper.JMSAdapterHelper;
import com.montos.boot.montos.mq.core.adapter.IMQDataSource;
import com.montos.boot.montos.mq.core.config.MontosMQCoreConfiguration;
import com.montos.boot.montos.mq.core.helper.MQDataSource;
import com.montos.boot.montos.mq.core.helper.MQDataSourceGroup;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

@Configuration
@AutoConfigureBefore(MontosMQCoreConfiguration.class)
@EnableConfigurationProperties(MutilActiveMQProperties.class)
public class MontosMQActiveMQAutoConfiguration implements EnvironmentAware {
	
	public static final String ACTIVEMQ = "activemq";
	
//	private List<MyActiveMQProperties> activeMQPropertiesList=new ArrayList<MyActiveMQProperties>();

	@Override
	public void setEnvironment(Environment environment) {
//		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
//				environment, "nemo.mq.");
//		String dsPrefixs = propertyResolver.getProperty("names");
//		if(dsPrefixs==null){
//			throw new RuntimeException("can not find nemo.mq.names in application.properties");
//		}
//		
//		String[] dsPrefixsArr = dsPrefixs.split(",");
//		if(dsPrefixsArr==null){
//			return ;
//		}
//		for (String dsPrefix : dsPrefixsArr) {
//			Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
//			if(dsMap==null){
//				return ;
//			}
//			Object type = dsMap.get("type");
//			if(type==null){
//				throw new RuntimeException("can not find nemo.mq.*.type in application.properties");
//			}
//			if(!type.equals(ACTIVEMQ)){
//				continue ;
//			}
//			activeMQPropertiesList.add(this.parse(dsMap).setKey(dsPrefix));
//
//		}
	}
	
//	private MyActiveMQProperties parse(Map<String,Object> map){
//		MyActiveMQProperties activeMQProperties = new MyActiveMQProperties();
//		activeMQProperties.setType(String.valueOf(map.get("type")));
//		activeMQProperties.setBrokerUrl(String.valueOf(map.get("url")));
//		activeMQProperties.setUser(String.valueOf(map.get("user")));
//		activeMQProperties.setPassword(String.valueOf(map.get("password")));
//		return activeMQProperties;
//	}
	
	@Bean
	public JMSAdapterHelper jMSAdapterHelper(){
		return new JMSAdapterHelper();
	}
	
//	@Bean
//	public MQDataSourceGroup initActivemqDataSourceGroup(JMSAdapterHelper jMSAdapterHelper){
//		List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
//		for(MyActiveMQProperties myActiveMQProperties:activeMQPropertiesList){
//			JMSAdapter adapter = new JMSAdapter().setjMSAdapterHelper(jMSAdapterHelper).setMyActiveMQProperties(myActiveMQProperties);
//			mQDataSourceList.add(new MQDataSource().setSender(adapter).setListener(adapter)
//					.setType(myActiveMQProperties.getType())
//					.setKey(myActiveMQProperties.getKey()));
//		}
//		return new MQDataSourceGroup().setType(ACTIVEMQ).setDataSourceList(mQDataSourceList);
//	}
	
	@Bean
	public MQDataSourceGroup initActivemqDataSourceGroup(JMSAdapterHelper jMSAdapterHelper, MutilActiveMQProperties mutilActiveMQProperties){
		List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
		for(Entry<String, MyActiveMQProperties> entry:mutilActiveMQProperties.getActivemq().entrySet()){
			MyActiveMQProperties myActiveMQProperties = entry.getValue();
			myActiveMQProperties.setKey(entry.getKey());
			
			JMSAdapter adapter = new JMSAdapter()
					.setjMSAdapterHelper(jMSAdapterHelper)
					.setMyActiveMQProperties(myActiveMQProperties)
					.init();
			
			mQDataSourceList.add(new MQDataSource().setSender(adapter).setListener(adapter)
					.setType(MutilActiveMQProperties.MQ_DATASOURCE_TYPE_ACTIVEMQ)
					.setKey(entry.getKey()));
		}
		return new MQDataSourceGroup().setType(ACTIVEMQ).setDataSourceList(mQDataSourceList);
	}

}
