package com.montos.boot.montos.sliding.window.starter.annotation;

import com.montos.boot.montos.sliding.window.starter.config.NemoSlidingWindowConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NemoSlidingWindowConfiguration.class)
public @interface EnableSlidingWindow {
	
}
