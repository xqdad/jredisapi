package cn.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author wenqiang [2019-03-21]
 * @Date March, 21, Thursday
 * @Comment ...
 */

public class JredisApi {

    private volatile static JredisApi jedisApi;

    /**
     * 保存多个连接源
     */
    private static Map<String, JedisPool> poolMap = new HashMap<String, JedisPool>();

    private JredisApi() {
    }

    /**
     * @Description: jedisPool 池
     * @Param: [ip, port]
     * @return: redis.clients.jedis.JedisPool
     */
    private static JedisPool getPool(String ip, int port) {

        try {
            String key = ip + ":" + port;
            JedisPool pool = null;
            if (!poolMap.containsKey(key)) {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxIdle(100);
                config.setMaxTotal(100);
                //  在获取连接的时候检查有效性, 默认false
                config.setTestOnBorrow(true);
                //  在空闲时检查有效性, 默认false
                config.setTestOnReturn(true);
                pool = new JedisPool(config, ip, port, 10000);
                poolMap.put(key, pool);
            } else {
                pool = poolMap.get(key);
            }
            return pool;
        } catch (Exception e) {
            System.err.println("init jedis pool failed ! " + e.getMessage());
        }
        return null;
    }

    /**
     * @Description: 线程安全单列模式
     * @Param: []
     * @return: JedisApi
     */
    public static JredisApi getRedisApi() {

        if (jedisApi == null) {
            synchronized (JredisApi.class) {
                if (jedisApi == null) {
                    jedisApi = new JredisApi();
                }
            }
        }
        return jedisApi;
    }

    /**
     * @Description: 获取一个jedis连接
     * @Param: [ip, port]
     * @return: redis.clients.jedis.Jedis
     */
    public Jedis getRedis(String ip, int port) {
        Jedis jedis = null;
        int count = 0;
        while (jedis == null && count <= 10) {
            try {
                jedis = getPool(ip, port).getResource();
            } catch (Exception e) {
                System.err.println("get redis failed ! " + e.getMessage());
                count++;
            }
        }
        return jedis;
    }

    /**
     * @Description: 释放jedis到jedisPool中
     * @Param: [jedis, ip, port]
     */
    public void closeRedis(Jedis jedis) {

        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                System.err.println("colse jedis failed ! " + e.getMessage());
            }
        }
    }

/*
    public static void testString() {
        try {
            JredisApi.getPool("", "");
            jedis.select(1);
            jedis.flushDB();
            System.out.println("====字符串功能展示====");
            System.out.println("增:");
            System.out.println(jedis.set("a", "1"));
            System.out.println(jedis.set("b", "2"));
            System.out.println(jedis.set("c", "3"));
            System.out.println("删除键 a:" + jedis.del("a"));
            System.out.println("获取键 a:" + jedis.get("a"));
            System.out.println("修改键 b:" + jedis.set("b", "bChanged"));
            System.out.println("获取键 b 的值:" + jedis.get("b"));
            System.out.println("在键 c后面加入值：" + jedis.append("c", "End"));
            System.out.println("获取键 c的值：" + jedis.get("c"));
            System.out.println("增加多个键值对：" + jedis.mset("key01", "value01", "key02", "value02", "key03", "value03"));
            System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03"));
            System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03", "key04"));
            System.out.println("删除多个键值对：" + jedis.del(new String[]{"key01", "key02"}));
            System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03"));

            jedis.flushDB();
            System.out.println("新增键值对防止覆盖原先值:");
            System.out.println(jedis.setnx("key001", "value001"));
            System.out.println(jedis.setnx("key002", "value002"));
            System.out.println(jedis.setnx("key002", "value002-new"));
            System.out.println("获取键key001的值：" + jedis.get("key001"));
            System.out.println("获取键key002的值：" + jedis.get("key002"));

            System.out.println("新增键值对并设置有效时间:");
            System.out.println(jedis.setex("key003", 2, "value003"));
            System.out.println("获取键key003的值：" + jedis.get("key003"));
            TimeUnit.SECONDS.sleep(3);
            System.out.println("获取键key003的值：" + jedis.get("key003"));

            System.out.println("获取原值，更新为新值:");
            System.out.println(jedis.getSet("key002", "key2GetSet"));
            System.out.println("获取键key002的值：" + jedis.get("key002"));

            System.out.println("截取key002的值的字符串：" + jedis.getrange("key002", 2, 5));

            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
