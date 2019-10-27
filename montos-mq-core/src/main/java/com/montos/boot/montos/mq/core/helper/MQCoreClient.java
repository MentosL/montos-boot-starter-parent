package com.montos.boot.montos.mq.core.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.mq.core.adapter.IMQDataSource;
import com.montos.boot.montos.mq.core.adapter.IMQListener;
import com.montos.boot.montos.mq.core.annotation.EnableJsonMQ;
import com.montos.boot.montos.mq.core.api.annotation.JsonMQMapping;
import com.montos.boot.montos.mq.core.api.annotation.JsonMQService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MQCoreClient implements BeanPostProcessor, ApplicationContextAware, InitializingBean, DisposableBean {

	private static final Logger log = Logger.getLogger(MQCoreClient.class);

	Map<String, IMQDataSource> mQDataSourceMap = new HashMap<String, IMQDataSource>();

	ObjectMapper objectMapper;

	Map<String, Object> mqMap = new HashMap<String, Object>();

	Map<String, Class<?>> clazzMap = new HashMap<String, Class<?>>();

	Map<String, MQSenderProxy> senderMap = new HashMap<String, MQSenderProxy>();

	Map<String, JsonMQService> annoMap = new HashMap<String, JsonMQService>();

	ConfigurableListableBeanFactory beanFactory;

	ApplicationContext applicationContext;

	AsynExecuter asynExecuter;

	MQNameHelper mQNameHelper;

	public MQCoreClient setmQDataSourceList(List<IMQDataSource> mQDataSourceList) {
		for (IMQDataSource mQDataSource : mQDataSourceList) {
			mQDataSourceMap.put(mQDataSource.getKey(), mQDataSource);
		}
		return this;
	}

	public MQCoreClient setAsynExecuter(AsynExecuter asynExecuter) {
		this.asynExecuter = asynExecuter;
		return this;
	}

	public MQCoreClient setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	private IMQDataSource getMQDataSource(String dataSource) {
		IMQDataSource ds = mQDataSourceMap.get(dataSource);
		if (ds == null) {
			throw new RuntimeException(String.format(
					"no datasource[%s] can be found in your configuration, check application.properties please ",
					dataSource));
		}
		return ds;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

		SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
		String[] scanPackages = null;

		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			EnableJsonMQ enableJsonMQ = beanFactory.findAnnotationOnBean(beanName, EnableJsonMQ.class);
			if (enableJsonMQ != null) {
				scanPackages = enableJsonMQ.value();
			}
			JsonMQService jsonMQService = beanFactory.findAnnotationOnBean(beanName, JsonMQService.class);
			if (jsonMQService != null) {
				annoMap.put(beanName, jsonMQService);
				Class<?> clazz = this.getClass(beanFactory, beanName);
				clazzMap.put(beanName, clazz);
			}
		}

		for (String scanPackage : scanPackages) {
			String resolvedPath = resolvePackageToScan(scanPackage);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Scanning '%s' for JSON-MQ service interfaces.", resolvedPath));
			}
			try {
				for (Resource resource : applicationContext.getResources(resolvedPath)) {
					if (resource.isReadable()) {
						MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
						ClassMetadata classMetadata = metadataReader.getClassMetadata();
						AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
						String jsonRpcPathAnnotation = JsonMQService.class.getName();
						if (annotationMetadata.isAnnotated(jsonRpcPathAnnotation)) {
							String className = classMetadata.getClassName();
							String dataSource = (String) annotationMetadata
									.getAnnotationAttributes(jsonRpcPathAnnotation).get("value");

							String beanName = this.getExistKey(className);
							if (beanName != null) {
								MQSenderProxy mQSenderProxy = new MQSenderProxy().setmQNameHelper(mQNameHelper)
										.setAsynExecuter(asynExecuter).setObjectMapper(objectMapper)
										.setmQSender(this.getMQDataSource(dataSource).getMQSender());
								mQSenderProxy.setServiceInterface(Class.forName(className));

								senderMap.put(beanName, mQSenderProxy);
							} else {
								if (log.isDebugEnabled()) {
									log.debug(String.format("Found JSON-MQ service to proxy [%s].", className));
								}
								// 本地重新注册一个调用代理
								registerJsonProxyBean(dlbf, className, dataSource);
							}

						}
					}
				}

			} catch (Exception e) {
				throw new RuntimeException(String.format("Cannot scan package '%s' for classes.", resolvedPath), e);
			}
		}
	}

	private String getExistKey(String className) {
		for (Entry<String, Class<?>> entry : clazzMap.entrySet()) {
			try {
				if (Class.forName(className).isAssignableFrom(entry.getValue())) {
					return entry.getKey();
				}
			} catch (ClassNotFoundException e) {
			}
		}
		return null;
	}

	private void registerJsonProxyBean(DefaultListableBeanFactory dlbf, String className, String dataSource) {
		try {
			Class<?> serviceInterface = Class.forName(className);
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MQSenderProxy.class)
					.addPropertyValue("asynExecuter", asynExecuter)
					.addPropertyValue("serviceInterface", serviceInterface)
					.addPropertyValue("objectMapper", objectMapper).addPropertyValue("mQNameHelper", mQNameHelper)
					.addPropertyValue("mQSender", this.getMQDataSource(dataSource).getMQSender());
			dlbf.registerBeanDefinition(className + "-clientProxy", beanDefinitionBuilder.getBeanDefinition());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		this.initReceiver(bean, beanName);
		if (senderMap.containsKey(beanName)) {
			return senderMap.get(beanName);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	private void initReceiver(Object target, String beanName) {
		JsonMQService jsonMQService = annoMap.get(beanName);
		if (jsonMQService == null) {
			return;
		}
		if (target == null) {
			target = beanFactory.getBean(beanName);
		}
		Class<?> clazz = null;
		if (target instanceof FactoryBean) {
			FactoryBean<?> f = (FactoryBean<?>) target;
			clazz = f.getObjectType();
		} else {
			clazz = this.getClass(beanFactory, beanName);
		}

		List<Method> methodList = this.listMethodByAnnotaion(clazz);
		for (Method method : methodList) {
			JsonMQMapping destination = AnnotationUtil.getAnnotation(method, JsonMQMapping.class);
			if (destination == null) {
				continue;
			}
			String mQName = mQNameHelper.getMQNameClassAndMethod(clazz, method);

			IMQListener mQListener = this.getMQDataSource(jsonMQService.value()).getMQListener();
			mQListener.listener(new MQReceiverProxy().setmQNameHelper(mQNameHelper).setQueueType(destination.type())
					.setTarget(target).setMsgClass(method.getParameterTypes()).setObjectMapper(objectMapper)
					.setmQDataSource(jsonMQService.value()).setmQName(mQName).setTag(destination.tag()));
		}
	}

	/**
	 * Find a {@link BeanDefinition} in the {@link BeanFactory} or it's parents.
	 */
	private BeanDefinition findBeanDefintion(ConfigurableListableBeanFactory beanFactory, String serviceBeanName) {
		if (beanFactory.containsLocalBean(serviceBeanName)) {
			return beanFactory.getBeanDefinition(serviceBeanName);
		}
		BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
		if (parentBeanFactory != null && ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
			return findBeanDefintion((ConfigurableListableBeanFactory) parentBeanFactory, serviceBeanName);
		}
		throw new RuntimeException(String.format("Bean with name '%s' can no longer be found.", serviceBeanName));
	}

	/**
	 * 根据beanName获取Class
	 * 
	 * @param beanFactory
	 * @param serviceBeanName
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> getClass(ConfigurableListableBeanFactory beanFactory, String serviceBeanName) {
		BeanDefinition beanDefinition = this.findBeanDefintion(beanFactory, serviceBeanName);
		String className = beanDefinition.getBeanClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Method> listMethodByAnnotaion(Class<?> clazz) {
		List<Method> methodList = new ArrayList<Method>();
		for (Class<?> iclass : clazz.getInterfaces()) {
			if (iclass.isAnnotationPresent(JsonMQService.class)) {
				for (Method method : iclass.getDeclaredMethods()) {
					methodList.add(method);
				}
			}
		}
		return methodList;
	}

	/**
	 * Converts the scanPackage to something that the resource loader can handle.
	 */
	private String resolvePackageToScan(String scanPackage) {
		return ResourceUtils.CLASSPATH_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanPackage)
				+ "/**/*.class";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext
				.getBeanFactory();
		this.postProcessBeanFactory(defaultListableBeanFactory);
	}

	public MQCoreClient setmQNameHelper(MQNameHelper mQNameHelper) {
		this.mQNameHelper = mQNameHelper;
		return this;
	}

	@Override
	public void destroy() throws Exception {
		for (Entry<String, IMQDataSource> entry : mQDataSourceMap.entrySet()) {
			entry.getValue().getMQListener().shutdown();
		}
	}

}
