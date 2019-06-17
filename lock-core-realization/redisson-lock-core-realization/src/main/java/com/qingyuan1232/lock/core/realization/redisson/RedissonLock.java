package com.qingyuan1232.lock.core.realization.redisson;

import com.qingyuan1232.redis.lock.core.AbstractLockTemplate;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * redisson lock
 *
 * @author: zhao qingyuan
 * @date: 2019-06-12 16:56
 */
public class RedissonLock extends AbstractLockTemplate {

    private Redisson redisson;

    @Override
    public boolean tryLock(String key) {
        RLock lock = redisson.getLock(key);
        try {
            return lock.tryLock(0, getLockTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean releaseLock(String key) {
        redisson.getLock(key).unlock();
        return true;
    }

    public void setRedisson(Redisson redisson) {
        this.redisson = redisson;
    }
}
