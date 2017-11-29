package com.wasu.game.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by admin on 2017/6/9.
 */
public class AppBeanContext {

    private static ApplicationContext applicationContext = null;


    public static ApplicationContext getApplicationContext() {
        return getApplicationContext("applicationContext.xml");
    }

    public static ApplicationContext getApplicationContext(String configLocation) {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext(configLocation);
        }
        return applicationContext;
    }

    public static <T> T getBean(Class<T> tClass) {
        return (T) getApplicationContext().getBean(tClass);
    }


}
