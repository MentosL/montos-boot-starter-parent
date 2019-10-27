package com.jimistore.boot.nemo.dao.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jimistore.boot.nemo.dao.core.helper.InitContextAspect;

@Configuration
public class NemoDaoAutoConfiguration {
	
	@Bean
	public InitContextAspect initContextAspect(){
		return new InitContextAspect();
	}

}
