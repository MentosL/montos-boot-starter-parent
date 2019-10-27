package com.montos.boot.montos.mq.rocketmq.starter.config;

import com.montos.boot.montos.mq.core.adapter.IMQDataSource;
import com.montos.boot.montos.mq.core.config.MontosMQCoreConfiguration;
import com.montos.boot.montos.mq.core.helper.MQDataSource;
import com.montos.boot.montos.mq.core.helper.MQDataSourceGroup;
import com.montos.boot.montos.mq.core.helper.MQNameHelper;
import com.montos.boot.montos.mq.rocketmq.starter.adapter.RocketAdapter;
import com.montos.boot.montos.mq.rocketmq.starter.adapter.RocketMQNameHelper;
import com.montos.boot.montos.mq.rocketmq.starter.adapter.RocketMQProperties;
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
@EnableConfigurationProperties(MutilRocketMQProperties.class)
public class MontosMQRocketMQAutoConfiguration implements EnvironmentAware {
	
//	private List<RocketMQProperties> rocketMQPropertiesList=new ArrayList<RocketMQProperties>();
	
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
//			Object type = dsMap.get("type");
//			if(type==null){
//				throw new RuntimeException("can not find nemo.mq.*.type in application.properties");
//			}
//			if(!type.equals(ROCKETMQ)){
//				continue ;
//			}
//			
//			rocketMQPropertiesList.add(this.parse(dsMap).setKey(dsPrefix));
//		}
	}
	
//	private RocketMQProperties parse(Map<String,Object> map){
//		RocketMQProperties rocketMQProperties = new RocketMQProperties();
//		rocketMQProperties.setType(String.valueOf(map.get("type")));
//		rocketMQProperties.setUrl(String.valueOf(map.get("url")));
//		rocketMQProperties.setUser(String.valueOf(map.get("user")));
//		rocketMQProperties.setPassword(String.valueOf(map.get("password")));
//		rocketMQProperties.setProducerId(String.valueOf(map.get("producer-id")));
//		rocketMQProperties.setConsumerId(String.valueOf(map.get("consumer-id")));
//		
//		return rocketMQProperties;
//	}
	
	@Bean
	public MQNameHelper MQNameHelper(MutilRocketMQProperties mutilRocketMQProperties){
		return new RocketMQNameHelper().setMutilRocketMQProperties(mutilRocketMQProperties);
	}
	
	@Bean
	public MQDataSourceGroup initRocketDataSourceGroup(MutilRocketMQProperties mutilRocketMQProperties){
		List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
		for(Entry<String, RocketMQProperties> entry:mutilRocketMQProperties.getRocketmq().entrySet()){
			RocketMQProperties rocketMQProperties = entry.getValue();
			rocketMQProperties.setKey(entry.getKey());
			
			RocketAdapter adapter = new RocketAdapter().setRocketMQProperties(rocketMQProperties);
			mQDataSourceList.add(new MQDataSource().setSender(adapter).setListener(adapter)
					.setType(rocketMQProperties.getType())
					.setKey(rocketMQProperties.getKey()));
		}
		return new MQDataSourceGroup().setType(MutilRocketMQProperties.ROCKETMQ).setDataSourceList(mQDataSourceList);
	}

}
