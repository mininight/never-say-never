package never.say.never.testtest.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * Redis 锁
 * <p> tryGetDistributedLock  方法用于尝试获取分布式锁，如果获取成功则返回 true。
 * releaseDistributedLock  方法用于释放分布式锁，如果释放成功则返回 true。
 * 其中， lockKey  表示锁名， requestId  表示请求标识， expireTime  表示锁的过期时间。
 * 在  tryGetDistributedLock  方法中，采用 Redis 的 set 命令尝试获取锁，如果执行成功则表示锁被获取。
 * 在  releaseDistributedLock  方法中，采用 Lua 脚本，通过比较 requestId 和锁中的值是否相等来删除锁，并判断是否释放成功。
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-22
 * @see java.util.concurrent.locks.Lock TODO
 */

public class RedisLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param millisecondsToExpire 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, long millisecondsToExpire) {
        SetParams setParams = new SetParams();
        setParams.nx().px(millisecondsToExpire);
        String result = jedis.set(lockKey, requestId, setParams);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(luaScript, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }
}
