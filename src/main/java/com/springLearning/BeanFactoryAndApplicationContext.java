package com.springLearning;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class BeanFactoryAndApplicationContext {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        ConfigurableApplicationContext context = SpringApplication.run(BeanFactoryAndApplicationContext.class, args);

        /*
         * 1. 到底什么是BeanFactory  【Ctrl+Alt+U 看类图】 【Ctrl+F12 看接口中的所有方法】
         *      - 他是ApplicationContext 的父接口
         *      - 它才是 spring 的核心容器 ， 主要的ApplicationContext 实现都【组合】了它的功能
         */

        /*
            在此打断点发现，context有一个名为 BeanFactory (DefaultListableBeanFactory) 的属性,
            打开BeanFactory发现有 SingletonObjectI (ConcurrentHashMap) 属性 , 单例Bean存储再这里
         */
        System.out.println(context);

        //容器中的Bean对象的名字为首字母小写，若大写找不到
        context.getBean("listener1"); // 底层是  return this.getBeanFactory().getBean(name); 先获取BeanFactory再获取的bean


        /*
            2.BeanFactory 能干点啥
                - 表面上只有getBean（）
                - 实际上控制反转，基本的依赖注入，直至Bean的生命周期的各种功能，都有它的实现类实现
         */
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Map<String,Object> map = (Map<String,Object>)singletonObjects.get(beanFactory);
        map.entrySet().stream().filter(e->e.getKey().startsWith("listener1")).forEach(e->{
            System.out.println(e.getKey()+"="+e.getValue());
        });



        /*
            3. ApplicationContext 比 BeanFactory 多点啥？
                - 四个拓展接口
         */

        //MessageSource接口 ， messages.properties文件表示所有语言通用的翻译 ， 可以为空，但是不能没有
        System.out.println(context.getMessage("hi", null, Locale.ENGLISH));
        System.out.println(context.getMessage("hi", null, Locale.JAPANESE));
        System.out.println(context.getMessage("hi", null, Locale.CHINESE));

        //ResourcePatternResolver , ApplicationEventPublisher, EnvironmentCapable、、、



        context.publishEvent(new UserRegisterEvent(context));

    }
}
