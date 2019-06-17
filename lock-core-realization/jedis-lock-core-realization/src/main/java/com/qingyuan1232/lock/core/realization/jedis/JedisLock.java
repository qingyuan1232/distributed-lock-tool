package com.qingyuan1232.lock.core.realization.jedis;

import com.qingyuan1232.redis.lock.core.AbstractLockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

/**
 * 分布式锁 jedis实现
 *
 * @author: zhao qingyuan
 * @date: 2019-06-11 9:19
 */
public class JedisLock extends AbstractLockTemplate {
    private static final Logger log = LoggerFactory.getLogger(JedisLock.class);

    private static final String LUA_SCRIPT = ""
            + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1],ARGV[1]));"
            + "\nredis.call('PEXPIRE',KEYS[1],ARGV[2]);"
            + "\nreturn r";

    private JedisPool jedisPool;

    /**
     * 尝试加锁，返回成功或失败
     *
     * @param key key
     * @return
     */
    @Override
    public boolean tryLock(String key) {
        /**使用setNX加锁*/
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long ret = (Long) jedis.eval(LUA_SCRIPT, Arrays.asList(key), Arrays.asList("", getLockTimeout() + ""));
            if (ret != null && ret.intValue() == 1) {
                return true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 删除锁
     *
     * @param key
     * @return
     */
    @Override
    public boolean releaseLock(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = jedis.del(key);
            if (result != null && result.intValue() == 1) {
                return true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
