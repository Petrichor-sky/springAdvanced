package com.springLearning.day02;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class TestBeanFactory {
    public static void main(String[] args) {
        //BeanFactory默认的实现
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //Bean 的定义(class, scope, 初始化, 销毁)
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();

        beanFactory.registerBeanDefinition("config",beanDefinition);
        //输出结果只有一个config,@Configuration 和@Bean 并未生效
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        //1 . 给 BeanFactory 添加一些常用的后处理器 (提供解析注解的能力)
            //这里只是将常用的后处理器bean添加进beanFactory 工厂中 , 第 2,3 步操作是建立后处理器 bean 与 beanFactory 的的联系,使其生效
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
            /*config
            internalConfigurationAnnotationProcessor 解析 @Configuration 和 @Bean 注解生效
            internalAutowiredAnnotationProcessor 解析@Autowire 注解
            internalCommonAnnotationProcessor 解析 @Resource 注解
            internalEventListenerProcessor
            internalEventListenerFactory
            */
            //这里的后处理器并未生效,以下的操作是执行相应的后处理器






        //2. BeanFactory 后处理器主要功能,补充一些 bean 定义
        //BeanFactoryPostProcessor 接口下有名为 ConfigurationAnnotationProcessor 的实现类去解析@Configuration 和 @Bean 注解
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(beanFactoryPostProcessor -> {
            //执行后处理器
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        });
            //bean1和 bean2 都额外出现了
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        //3. Bean 后处理器,针对 Bean 生命周期各个阶段提供拓展,例如 @Autowire @Resource...
        //BeanPostProcessor 接口下有CommonAnnotationBeanPostProcessor 和 AutowiredAnnotationProcessor实现类,分别去解析@Resource 和 @Autowire 注解
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().forEach(beanFactory::addBeanPostProcessor);
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        beanFactory.preInstantiateSingletons();//在容器启动时预先准备好所有的单例
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        //以上说明beanFactory默认容器启动时只是保持了 bean 的一些定义信息,只有以下第一次用到时才会实例化(延迟加载) ,除非调用以上的方法使其预先初始化

        System.out.println(beanFactory.getBean(Bean1.class).getBean2());


        /*
            学到了什么：
            O. beanFactory 不会做的事

            1. 不会主动调用 BeanFactory 后处理器
            2. 不会主动添加 Bean 后处理器
            3.不会去动动始化發份
            4.不会解加beanFactory 还不会解折 )1与#{} b. bean 后处理器会有排房的逻算
         */


    }

    @Configuration
    static class Config{
        @Bean
        public Bean1 bean1(){
            return new Bean1();
        }
        @Bean
        public Bean2 bean2(){
            return new Bean2();
        }
    }

    static class Bean1{
        private static final Logger log  = LoggerFactory.getLogger(Bean1.class);
        public Bean1(){
            log.debug("构造 Bean1()");
        }
        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2(){
            return bean2;
        }
    }

    static class Bean2 {
        private static final Logger log = LoggerFactory.getLogger(Bean1.class);

        public Bean2() {
            log.debug("构造 Bean2()");
        }
    }
}
