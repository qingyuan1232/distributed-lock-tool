package com.qingyuan1232.redis.lock.spring.handler;

import com.qingyuan1232.redis.lock.core.LockTemplate;
import com.qingyuan1232.redis.lock.spring.BusinessKeyProvider;
import com.qingyuan1232.redis.lock.spring.annotation.SLock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Lock 注解处理器
 *
 * @author: zhao qingyuan
 * @date: 2019-06-11 14:18
 */
@Aspect
@Component
@EnableAspectJAutoProxy
public class LockHandler {
    private static final Logger logger = LoggerFactory.getLogger(LockHandler.class);


    @Autowired
    private LockTemplate lockTemplate;
    @Autowired
    private BusinessKeyProvider businessKeyProvider;

    @Around(value = "@annotation(lock)")
    public Object lockAround(ProceedingJoinPoint joinPoint, SLock lock) throws Throwable {
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class targetClass = joinPoint.getTarget().getClass();

        String keyPrefix = targetClass.getName() + ":" + targetMethod.getName() + ":";

        String key = keyPrefix + businessKeyProvider.getKeyName(joinPoint, lock);

        Object ret = null;
        if (lockTemplate.lock(key, lock.awaitTimeout(), lock.lockTimeout())) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("获取锁成功,key:{}", key);
                }
                ret = joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                lockTemplate.unLock(key);
                if (logger.isDebugEnabled()) {
                    logger.debug("释放锁成功,key:{}", key);
                }
            }
        } else if (lock.lockFailHandler() != null && lock.lockFailHandler().trim().length() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("获取锁失败,key:{}", key);
            }
            //执行自定义加锁失败处理
            ret = handlerLockFail(joinPoint, lock);
        }

        return ret;
    }

    /**
     * 执行自定义加锁失败方法
     *
     * @param joinPoint
     * @param lock
     * @return
     * @throws Throwable
     */
    private Object handlerLockFail(JoinPoint joinPoint, SLock lock) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();

        Method handleMethod;
        try {
            handleMethod = currentMethod.getDeclaringClass().getDeclaredMethod(lock.lockFailHandler(), currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param awaitTimeoutHandler", e);
        }

        Object[] args = joinPoint.getArgs();

        // invoke
        Object res;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Fail to invoke custom lock fail handler: " + lock.lockFailHandler(), e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }
}

