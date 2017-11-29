package com.wasu.game.activeMQ.listener;

import java.util.concurrent.ExecutorService;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import com.wasu.game.activeMQ.HandleThreadPoolExecutor;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.domain.Request;
import com.wasu.game.scanner.Invoker;
import com.wasu.game.scanner.InvokerHoler;

public class Requestlistener implements MessageListener {

    private static Logger logger = Logger.getLogger(Requestlistener.class);

    //默认线程池数量  
    public final static int DEFAULT_HANDLE_THREAD_POOL = 10;

    private ExecutorService handleThreadPool = new HandleThreadPoolExecutor(DEFAULT_HANDLE_THREAD_POOL);

    @Override
    public void onMessage(Message msg) {
        try {
//			logger.info("MQ receive TYPE:"+msg.getJMSType());
            //如果请求消息
            if (JmsTypeEnum.REQUEST.equals(msg.getJMSType()) && msg instanceof ObjectMessage) {
                ObjectMessage obj = (ObjectMessage) msg;
                //获取消息
                final Request res = (Request) obj.getObject();
//				logger.info("MQ receive CONTENT:"+res);
                handleThreadPool.execute(new Runnable() {
                    public void run() {
                        //反射调用
                        Invoker invoker = InvokerHoler.getInvoker(res.getModule(), res.getCmd());
                        if (invoker != null) {
                            invoker.invoke(res);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
