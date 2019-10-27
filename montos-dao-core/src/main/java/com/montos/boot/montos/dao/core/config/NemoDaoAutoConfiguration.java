package com.montos.boot.montos.dao.core.config;

import com.montos.boot.montos.dao.core.helper.InitContextAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NemoDaoAutoConfiguration {
	
	@Bean
	public InitContextAspect initContextAspect(){
		return new InitContextAspect();
	}

}
