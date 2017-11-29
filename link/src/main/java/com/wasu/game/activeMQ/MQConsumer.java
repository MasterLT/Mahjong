package com.wasu.game.activeMQ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.jms.*;

/**
 * Created by Administrator on 2017/4/24.
 */
//@Component
public class MQConsumer {

//    @Autowired
    DefaultMessageListenerContainer jmsContainer;

//    @PostConstruct
    public void setListener() throws JMSException {
        String selector = "JMSType='" + JmsTypeEnum.REQUEST + "'";
        jmsContainer.setMessageSelector(selector);
    }
}
