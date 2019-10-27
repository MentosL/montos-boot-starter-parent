package com.montos.boot.montos.dao.hibernate.annotation;

import com.montos.boot.montos.dao.hibernate.config.MontosDaoHibernateAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MontosDaoHibernateAutoConfiguration.class})
public @interface EnableHibernate {

}
