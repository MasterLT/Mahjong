package com.wasu.game.activeMQ;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.wasu.game.domain.Result;
import com.wasu.game.tools.AppBeanContext;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MQProductor implements ExceptionListener {

    @Autowired
    PooledConnectionFactory pool;

    @Autowired
    ActiveMqConfig config;

    // 线程池数量
    private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
    public final static int DEFAULT_THREAD_POOL_SIZE = 50;

    // 是否持久化消息
    private boolean isPersistent = DEFAULT_IS_PERSISTENT;
    public final static boolean DEFAULT_IS_PERSISTENT = true;

    private ExecutorService threadPool = Executors.newFixedThreadPool(this.threadPoolSize);

    private static MQProductor mQProductor = null;

    public static MQProductor build() {
        if (mQProductor == null) {
            mQProductor = AppBeanContext.getBean(MQProductor.class);
        }
        return mQProductor;
    }

    /**
     * 执行发送消息的具体方法
     */
    public void send(final Serializable data, final String jsmType) {
        // 直接使用线程池来执行具体的调用
        this.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMsg(config.getTopick(), jsmType, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
//	public void send(final String topic, final Serializable data, final String jsmType) {
//		// 不使用线程池来保证消息发送顺序
//		try {
//			sendMsg(topic, jsmType, data);
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
//	}

    /**
     * 执行发送消息的具体方法
     *
     * @param data
     */
    public void send(final List<Result> data, final String jsmType) {
        // 直接使用线程池来执行具体的调用
        this.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMsg(config.getTopick(), jsmType, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 真正的执行消息发送
     *
     * @param topic
     * @param obj
     * @throws JMSException
     */
    protected void sendMsg(String topic, String jsmType, Serializable obj) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            // 从连接池工厂中获取一个连接
            connection = pool.createConnection();
            connection.start();
            // false 参数表示 为非事务型消息，后面的参数表示消息的确认类型
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            // 消息方式
            Destination destination = new ActiveMQTopic(topic);
            // Creates a MessageProducer to send messages to the specified
            // destination
            MessageProducer producer = session.createProducer(destination);
            // set delevery mode
            producer.setDeliveryMode(this.isPersistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            // map convert to javax message
            Message message = getMessage(session, jsmType, obj);
            producer.send(destination, message);
        } finally {
            closeSession(session);
            closeConnection(connection);
        }

    }

    /**
     * 真正的执行消息发送
     *
     * @param topic
     * @param obj
     * @throws JMSException
     */
    protected void sendMsg(String topic, String jsmType, List<Result> obj) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            // 从连接池工厂中获取一个连接
            connection = pool.createConnection();
            connection.start();
            // false 参数表示 为非事务型消息，后面的参数表示消息的确认类型
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            // 消息方式
            Destination destination = new ActiveMQTopic(topic);
            // Creates a MessageProducer to send messages to the specified
            // destination
            MessageProducer producer = session.createProducer(destination);
            // set delevery mode
            producer.setDeliveryMode(this.isPersistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            // map convert to javax message
            for (Serializable o : obj) {
                Message message = getMessage(session, jsmType, o);
                producer.send(destination, message);
            }
        } finally {
            closeSession(session);
            closeConnection(connection);
        }

    }

    private Message getMessage(Session session, String jsmType, Serializable obj) throws JMSException {
        ObjectMessage message = session.createObjectMessage();
        message.setObject(obj);
        if (jsmType != null && !"".equals(jsmType))
            message.setJMSType(jsmType);
        return message;
    }

    private void closeSession(Session session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onException(JMSException e) {
        e.printStackTrace();
    }
}
