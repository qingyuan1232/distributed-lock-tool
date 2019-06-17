package com.qingyuan1232.redis.lock.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: zhao qingyuan
 * @date: 2019-06-11 13:55
 */
public abstract class AbstractLockTemplate implements LockTemplate {
    /**
     * 重新加锁锁等待时间
     */
    long awaitTimeout = 1000;

    /**
     * 锁超时时间
     */
    long lockTimeout = 1000;

    /**
     * 重试次数
     */
    int tryTimes = 0;

    public AbstractLockTemplate() {
    }

    public AbstractLockTemplate(long awaitTimeout, long lockTimeout) {
        this.awaitTimeout = awaitTimeout;
        this.lockTimeout = lockTimeout;
    }


    @Override
    public boolean lock(String key, Long awaitTimeout, Long lockTimeout) {
        this.lockTimeout = lockTimeout != null ? lockTimeout : this.lockTimeout;
        this.awaitTimeout = awaitTimeout != null ? awaitTimeout : this.awaitTimeout;
        if (tryLock(key)) {
            return true;
        }
        //加锁失败，等待指定超时时间重新获取
        if (this.awaitTimeout == 0) {
            return false;
        }
        //加锁失败，不进行重试，直接返回失败
        if (tryTimes == 0) {
            return false;
        }
        //加锁失败，进行重试
        for (int i = 0; i < tryTimes; i++) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(this.awaitTimeout));
            if (tryLock(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unLock(String key) {
        return releaseLock(key);
    }


    /**
     * 尝试加锁
     *
     * @param key
     * @return
     */
    public abstract boolean tryLock(String key);

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    public abstract boolean releaseLock(String key);

    public long getAwaitTimeout() {
        return awaitTimeout;
    }

    public void setAwaitTimeout(long awaitTimeout) {
        this.awaitTimeout = awaitTimeout;
    }

    public long getLockTimeout() {
        return lockTimeout;
    }

    public void setLockTimeout(long lockTimeout) {
        this.lockTimeout = lockTimeout;
    }

    public void setTryTimes(int tryTimes) {
        this.tryTimes = tryTimes;
    }
}
