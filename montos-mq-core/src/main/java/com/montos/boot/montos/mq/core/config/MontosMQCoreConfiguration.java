package com.montos.boot.montos.mq.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.montos.boot.montos.mq.core.adapter.IMQDataSource;
import com.montos.boot.montos.mq.core.helper.AsynExecuter;
import com.montos.boot.montos.mq.core.helper.MQCoreClient;
import com.montos.boot.montos.mq.core.helper.MQDataSourceGroup;
import com.montos.boot.montos.mq.core.helper.MQNameHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MontosMQCoreConfiguration {

    @Bean
    @ConditionalOnMissingBean(AsynExecuter.class)
    public AsynExecuter AsynExecuter() {
        return new AsynExecuter().setCapacity(5);
    }


    @Bean
    @ConditionalOnMissingBean(MQNameHelper.class)
    public MQNameHelper MQNameHelper() {
        return new MQNameHelper();
    }


    @Bean
    @ConditionalOnMissingBean(MQCoreClient.class)
    @ConditionalOnBean({IMQDataSource.class, ObjectMapper.class, AsynExecuter.class})
    public MQCoreClient MQClient(List<MQDataSourceGroup> mqDataSourceGroups, AsynExecuter asynExecuter, MQNameHelper mQNameHelper) {
        List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
        for (MQDataSourceGroup mQDataSourceGroup : mqDataSourceGroups) {
            if (mQDataSourceGroup.getDataSourceList() != null) {
                mQDataSourceList.addAll(mQDataSourceGroup.getDataSourceList());
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new MQCoreClient()
                .setmQNameHelper(mQNameHelper)
                .setmQDataSourceList(mQDataSourceList)
                .setObjectMapper(objectMapper)
                .setAsynExecuter(asynExecuter);
    }

}
