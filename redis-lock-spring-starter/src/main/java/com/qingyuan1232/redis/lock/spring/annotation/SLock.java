package com.qingyuan1232.redis.lock.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义锁注解
 * 使用aop处理 @link
 *
 * @author: zhao qingyuan
 * @date: 2019-06-11 11:11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SLock {
    /**
     * key值，使用SpEL表达式处理
     *
     * @return
     */
    String[] key() default {};

    /**
     * 重试加锁次数 默认为0
     *
     * @return
     */
    int tryTimes() default 0;

    /**
     * 重试加锁等待时间 默认为0 单位 ms
     *
     * @return
     */
    long awaitTimeout() default 0;

    /**
     * 锁超时时间 默认为1000 单位 ms
     *
     * @return
     */
    long lockTimeout() default 1000;

    /**
     * 加锁失败自定义处理器，指定失败处理方法，入参需要和接口参数一致
     *
     * @return
     */
    String lockFailHandler() default "";

}
