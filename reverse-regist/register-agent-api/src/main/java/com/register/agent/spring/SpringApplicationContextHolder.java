package com.register.agent.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContextHolder implements ApplicationContextAware {


    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException
    {
        applicationContext = arg0;
    }


    public static <T> T getBean(Class<T> aClass) throws BeansException{
        return applicationContext.getBean(aClass);
    }

    public static Object getBean(String beanName) throws BeansException{
        return applicationContext.getBean(beanName);
    }


    private static ApplicationContext applicationContext;

}
