package com.wasu.game.aop;

/**
 * Created by Administrator on 2017/4/7.
 */

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 切面
 *
 */
@Aspect
@Component
public class TimeHandler {
    private static Logger logger = Logger.getLogger(TimeHandler.class);

    @Pointcut("execution(* com.wasu.game.module.player.service.PlayerServiceImpl.*(..))")
//    @Pointcut("execution(* com.wasu.game.aop.Test.*(..))")
    private void anyMethod(){}//定义一个切入点

    @Around("anyMethod()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable{
//        logger.info("进入环绕通知");
        long start=System.currentTimeMillis();
        Object object = pjp.proceed();//执行该方法
        long end=System.currentTimeMillis();
        logger.info("方法"+pjp.getSignature().getName()+"耗时："+(end-start));
        return object;
    }
}
