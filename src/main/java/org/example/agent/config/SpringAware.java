package org.example.agent.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringAware implements ApplicationContextAware {
    private static ApplicationContext context;

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T extends Object> T getBean(Class<T> beanClass, String name) {
        return context.getBean(beanClass, name);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        setContext(context);
    }

    private static synchronized void setContext(ApplicationContext context) {
        SpringAware.context = context;
    }
}
