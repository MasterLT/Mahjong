package com.wasu.game.activeMQ.listener;

import com.wasu.game.activeMQ.HandleThreadPoolExecutor;
import com.wasu.game.domain.Result;
import com.wasu.game.netty.ServerHandler;
import org.apache.log4j.Logger;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.concurrent.ExecutorService;

public class Responselistener implements MessageListener {

    private static Logger logger = Logger.getLogger(Responselistener.class);

    //默认线程池数量  
    public final static int DEFAULT_HANDLE_THREAD_POOL = 10;

    private ExecutorService handleThreadPool = new HandleThreadPoolExecutor(DEFAULT_HANDLE_THREAD_POOL);

    public void onMessage(Message msg) {
        try {
//			logger.info("MQ receive TYPE:"+msg.getJMSType());
            //如果广播消息对象
//            if (JmsTypeEnum.RADIATE.equals(msg.getJMSType()) && msg instanceof ObjectMessage) {
            ObjectMessage obj = (ObjectMessage) msg;
            //获取消息
            final Result res = (Result) obj.getObject();
//				logger.info("MQ receive CONTENT:"+res);
            handleThreadPool.execute(new Runnable() {
                public void run() {
                    //广播消息
                    ServerHandler.Radiate(res);
                }
            });
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
