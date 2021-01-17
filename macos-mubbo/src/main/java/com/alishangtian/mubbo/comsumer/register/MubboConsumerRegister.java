package com.alishangtian.mubbo.comsumer.register;

import com.alishangtian.mubbo.comsumer.annotation.EnableMubboConsumer;
import com.alishangtian.mubbo.comsumer.annotation.MubboClient;
import com.alishangtian.mubbo.comsumer.beanfactory.MubboConsumerFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * MubboConsumerRegister
 *
 * @author shangtian
 */
@Slf4j
public class MubboConsumerRegister implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {

    private ClassLoader classLoader;

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 存放 @EnableRxFeignLocality 注解的所有属性
     */
    private Map<String, Object> enableMubboConsumerAttributes = null;

    /**
     * 实现该方法，向Spring上下文中注册指定路径下，指定注解的Bean对象
     *
     * @param metadata 注解的元信息
     * @param registry Spring内置的注册器
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        enableMubboConsumerAttributes = metadata.getAnnotationAttributes(EnableMubboConsumer.class.getName(), Boolean.TRUE);
        log.info("@EnableMubboConsumer 注解中属性：{}", enableMubboConsumerAttributes);
        this.registerMubboConsumerClient(registry);
    }

    /**
     * 扫描自定义注解中指定路径下的Bean组件，并且将其注册到Spring的上下文中
     *
     * @param registry
     */
    private void registerMubboConsumerClient(BeanDefinitionRegistry registry) {
        // 获取Provider 其实就是一个是扫描器，提供扫描的功能
        ClassPathScanningCandidateComponentProvider provider = this.getScanner();
        // 给扫描器设置资源加载器
        provider.setResourceLoader(resourceLoader);

        // 添加扫描组件需要过滤的类型，这里我们需要扫描所有添加注解 @MubboClient的类或者接口
        AnnotationTypeFilter typeFilter = new AnnotationTypeFilter(MubboClient.class);
        // 扫描器
        provider.addIncludeFilter(typeFilter);

        String[] scanPackageArr = (String[]) enableMubboConsumerAttributes.get("scanPackages");
        // 如果没有配置则直接就不扫描了  方法直接返回即可
        if (null == scanPackageArr && scanPackageArr.length == 0) {
            log.info("@EnableMubboConsumer 中的scanPackages值为空");
            return;
        }
        // 将需要扫描的路径数组 转化为 Set集合
        Set<String> scanPackages = new HashSet<>(CollectionUtils.arrayToList(scanPackageArr));

        Iterator<String> iterable = scanPackages.iterator();
        while (iterable.hasNext()) {
            String packages = iterable.next();
            Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(packages);
            Iterator<BeanDefinition> bi = beanDefinitions.iterator();
            while (bi.hasNext()) {
                BeanDefinition beanDefinition = bi.next();
                // 含有注解的bean
                if (beanDefinition instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                    AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
                    Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(MubboClient.class.getCanonicalName());
                    Assert.isTrue(annotationMetadata.isInterface(), "@" + MubboClient.class.getName() + " 只能标记在接口上");
                    this.registerMubboConsumerBean(registry, annotationMetadata, annotationAttributes);
                }
            }
        }
    }

    /**
     * 将接口根据代理工厂生成实例对象，并且将该实例对象注册到Spring的上下文中
     *
     * @param registry
     * @param annotationMetadata
     * @param annotationAttributes
     */
    private void registerMubboConsumerBean(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Map<String, Object> annotationAttributes) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(MubboConsumerFactoryBean.class);
        definition.addPropertyValue("className", annotationMetadata.getClassName());
        definition.addPropertyValue("serviceName", annotationAttributes.get("value"));

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        String clazzName = className.substring(className.lastIndexOf(".") + 1);
        String alias = this.lowerFirstCapse(clazzName);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 首字母变小写
     *
     * @param str
     * @return
     */
    public String lowerFirstCapse(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 项目路径下的扫描器
     * <p>
     * ClassPathScanningCandidateComponentProvider 是Spring提供的工具，可以按照自定义的类型，查找classpath下符合要求的class文件
     *
     * @return
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation()) {
                    isCandidate = true;
                }
                return isCandidate;
            }
        };
    }
}
