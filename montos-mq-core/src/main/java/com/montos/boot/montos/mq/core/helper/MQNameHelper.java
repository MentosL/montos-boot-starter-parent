package com.montos.boot.montos.mq.core.helper;

import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.mq.core.api.annotation.JsonMQMapping;
import com.montos.boot.montos.mq.core.api.annotation.JsonMQService;

import java.lang.reflect.Method;

/**
 * 用来处理mq名称等统一的工具类
 */
public class MQNameHelper {

    private static final String SPLIT = ".";
    private static final String PARAM_SPLIT = "-";

    /**
     * 根据class和method获取MQName
     *
     * @param clazz
     * @param method
     * @return
     */
    public String getMQNameClassAndMethod(Class<?> clazz, Method method) {

        String destName = this.getDestinationName(clazz, method);
        if (destName != null) {
            return destName;
        }

        StringBuilder methodId = new StringBuilder(method.getName());
        for (Class<?> paramType : method.getParameterTypes()) {
            methodId.append(PARAM_SPLIT).append(paramType.getSimpleName());
        }

        return String.format("%s%s%s", clazz.getName(), SPLIT, methodId.toString());
    }

    protected String getDestinationName(Class<?> clazz, Method method) {
        JsonMQMapping destination = AnnotationUtil.getAnnotation(method, JsonMQMapping.class);
        if (destination != null && destination.value() != null && !destination.value().isEmpty()) {
            return destination.value();
        }
        return null;
    }

    /**
     * 根据mqName获取接口的方法
     *
     * @param mQName
     * @param tag
     * @param paramClasses
     * @param target
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public Method getMethodByMQNameAndTarget(String mQName, String tag, Class<?>[] paramClasses, Object target) throws NoSuchMethodException, SecurityException {
        String methodName = null;
        for (Class<?> clazz : target.getClass().getInterfaces()) {
            if (clazz.isAnnotationPresent(JsonMQService.class)) {
                for (Method method : clazz.getMethods()) {
                    String destName = this.getDestinationName(clazz, method);
                    JsonMQMapping destination = AnnotationUtil.getAnnotation(method, JsonMQMapping.class);
                    if (destName != null && destName.equals(mQName) && tag != null && tag.equals(destination.tag())) {
                        methodName = method.getName();
                        break;
                    }
                }
                break;
            }
        }

        if (methodName == null) {
            methodName = mQName.substring(mQName.lastIndexOf(SPLIT) + 1);
            if (methodName.indexOf(PARAM_SPLIT) > 0) {
                methodName = methodName.substring(0, methodName.indexOf(PARAM_SPLIT));
            }
        }

        return target.getClass().getMethod(methodName, paramClasses);
    }


}
