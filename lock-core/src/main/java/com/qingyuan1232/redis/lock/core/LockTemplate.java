package com.qingyuan1232.redis.lock.core;

/**
 * lock 模板接口
 *
 * @author: zhao qingyuan
 * @date: 2019-06-11 9:27
 */
public interface LockTemplate {

    /**
     * 加锁
     *
     * @param key          加锁key
     * @param awaitTimeout 等待时间
     * @param lockTimeout  锁超时时间
     * @return
     */
    boolean lock(String key, Long awaitTimeout, Long lockTimeout);

    /**
     * 解锁
     *
     * @param key 加锁key
     * @return
     */
    boolean unLock(String key);
}
