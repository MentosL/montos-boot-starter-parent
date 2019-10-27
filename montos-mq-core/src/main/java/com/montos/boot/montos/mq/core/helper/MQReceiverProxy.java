package com.montos.boot.montos.mq.core.helper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.montos.boot.montos.mq.core.adapter.IMQReceiver;
import com.montos.boot.montos.mq.core.api.enums.QueueType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 每个mq接收端的代理
 */
public class MQReceiverProxy implements IMQReceiver {

    private String mQName;

    private String tag;

    private String mQDataSource;

    private QueueType queueType;

    private Object target;

    private Class<?>[] msgClass;

    ObjectMapper objectMapper;

    MQNameHelper mQNameHelper;

    @Override
    public void receive(Object msg) throws Throwable {
        String msgStr = (String) msg;
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, String.class);
        List<String> sourceList = objectMapper.readValue(msgStr, javaType);
        Method method = mQNameHelper.getMethodByMQNameAndTarget(mQName, tag, msgClass, target);
        Type[] types = method.getGenericParameterTypes();

        Object[] dest = new Object[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            if (types[i] instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) types[i]).getActualTypeArguments();
                Class<?>[] classes = new Class[genericTypes.length];
                for (int j = 0; j < genericTypes.length; j++) {
                    classes[j] = (Class<?>) genericTypes[j];
                }
                javaType = objectMapper.getTypeFactory().constructParametricType(msgClass[i], classes);
                dest[i] = objectMapper.readValue(sourceList.get(i), javaType);
            } else {
                dest[i] = objectMapper.readValue(sourceList.get(i), msgClass[i]);
            }
        }
        try {
            method.invoke(target, dest);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public MQReceiverProxy setmQName(String mQName) {
        this.mQName = mQName;
        return this;
    }

    public MQReceiverProxy setQueueType(QueueType queueType) {
        this.queueType = queueType;
        return this;
    }

    public MQReceiverProxy setTarget(Object target) {
        this.target = target;
        return this;
    }

    @Override
    public QueueType getQueueType() {
        return queueType;
    }

    @Override
    public String getmQName() {
        return mQName;
    }

    @Override
    public Class<?>[] getMsgClass() {
        return msgClass;
    }

    public MQReceiverProxy setMsgClass(Class<?>[] msgClass) {
        this.msgClass = msgClass;
        return this;
    }

    public MQReceiverProxy setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public MQReceiverProxy setmQDataSource(String mQDataSource) {
        this.mQDataSource = mQDataSource;
        return this;
    }

    public MQReceiverProxy setmQNameHelper(MQNameHelper mQNameHelper) {
        this.mQNameHelper = mQNameHelper;
        return this;
    }

    @Override
    public String getmQDataSource() {
        return mQDataSource;
    }

    public MQReceiverProxy setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public String getTag() {
        // TODO Auto-generated method stub
        return tag;
    }

}
