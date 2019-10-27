package com.montos.boot.montos.mq.rabbitmq.starter.config;

import com.montos.boot.montos.mq.core.adapter.IMQDataSource;
import com.montos.boot.montos.mq.core.config.MontosMQCoreConfiguration;
import com.montos.boot.montos.mq.core.helper.MQDataSource;
import com.montos.boot.montos.mq.core.helper.MQDataSourceGroup;
import com.montos.boot.montos.mq.rabbitmq.starter.adapter.RabbitAdapter;
import com.montos.boot.montos.mq.rabbitmq.starter.adapter.RabbitProperties;
import com.montos.boot.montos.mq.rabbitmq.starter.helper.RabbitAdapterHelper;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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
@EnableConfigurationProperties(MutilRabbitProperties.class)
public class NemoMQRabbitMQAutoConfiguration implements EnvironmentAware {
	
	public static final String RABBITMQ = "rabbitmq";
	
//	private List<RabbitProperties> rabbitPropertiesList=new ArrayList<RabbitProperties>();
	
	@Override
	public void setEnvironment(Environment environment) {
//		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
//				environment, "nemo.mq.");
//		String dsPrefixs = propertyResolver.getProperty("names");
//		if(dsPrefixs==null){
//			throw new RuntimeException("can not find nemo.mq.names in application.properties");
//		}
//
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
//			if(!type.equals(RABBITMQ)){
//				continue ;
//			}
//			rabbitPropertiesList.add(this.parse(dsMap).setKey(dsPrefix));
//
//		}
	}
	
//	private RabbitProperties parse(Map<String,Object> map){
//		RabbitProperties rabbitProperties = new RabbitProperties();
//		rabbitProperties.setType(String.valueOf(map.get("type")));
//		rabbitProperties.setHost(String.valueOf(map.get("host")));
//		rabbitProperties.setUsername(String.valueOf(map.get("user")));
//		rabbitProperties.setPassword(String.valueOf(map.get("password")));
//		if(map.get("port")!=null){
//			rabbitProperties.setPort(Integer.parseInt(map.get("port").toString()));
//		}
//		return rabbitProperties;
//	}
	
	@Bean
	public RabbitAdapterHelper rabbitAdapterHelper(){
		return new RabbitAdapterHelper();
	}
	
	@Bean
	public MQDataSourceGroup initRabbitmqDataSourceGroup(RabbitAdapterHelper rabbitAdapterHelper, MutilRabbitProperties mutilRabbitProperties){
		List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
		for(Entry<String, RabbitProperties> entry:mutilRabbitProperties.getRabbitmq().entrySet()){
			RabbitProperties rabbitProperties = entry.getValue();
			rabbitProperties.setKey(entry.getKey());
			
			RabbitAdapter adapter = new RabbitAdapter().setRabbitAdapterHelper(rabbitAdapterHelper).setRabbitProperties(rabbitProperties);
			mQDataSourceList.add(new MQDataSource().setSender(adapter).setListener(adapter)
					.setType(rabbitProperties.getType())
					.setKey(rabbitProperties.getKey()));
		}
		return new MQDataSourceGroup().setType(RABBITMQ).setDataSourceList(mQDataSourceList);
	}
	
	@Bean
	public List<RabbitAdmin> rabbitAdmin(MutilRabbitProperties mutilRabbitProperties){
		List<RabbitAdmin> rabbitAdminList = new ArrayList<RabbitAdmin>();
		for(Entry<String, RabbitProperties> entry:mutilRabbitProperties.getRabbitmq().entrySet()){
			rabbitAdminList.add(new RabbitAdmin(entry.getValue()));
		}
		return rabbitAdminList;
	}

}
