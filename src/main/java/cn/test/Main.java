package cn.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.sound.midi.Soundbank;
import java.util.Set;

/**
 * @Author wenqiang [2019-03-21]
 * @Date March, 21, Thursday
 * @Comment ...
 */
public class Main {

    static void one() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();//初始化
        jedisPoolConfig.setMaxTotal(10);//创建jedis最大对象数据为10个

        JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379, 10000);//创建jedispool对象

        JedisPool pool2 = new JedisPool(jedisPoolConfig, "172.27.0.7", 6379, 10000, "R4L@zaq1!");//创建jedispool对象

        Jedis jedis = null, jedis2 = null;

        try{
            jedis = pool.getResource(); //从jedispool中获取jedis对象，获取后该对象就不存在池中
            jedis.select(1);

            jedis2 = pool2.getResource();
            jedis2.select(1);

            System.out.println("111 获取链接 " + jedis.toString());
            System.out.println("222 获取链接 " + jedis2.toString());

            Set<String> set =  jedis.keys("*");
            System.out.println("111 all counts: " + set.size());
            for (String k: set) {
                System.out.println("111 -->" + k);

                jedis2.set(k, jedis.get(k));

            }
            System.out.println("111 打印完成");


            System.out.println("222 count:\t" + jedis2.keys("*").size());




        }catch(Exception e){
            e.printStackTrace();
        }finally {
            //还回pool中
            if(jedis != null){
                jedis.close();  //关闭后，jedis对象回到池中
            }
        }
        pool.close();
    }

    public static void main(String[] args) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();//初始化
        jedisPoolConfig.setMaxTotal(10);//创建jedis最大对象数据为10个

        JedisPool pool2 = new JedisPool(jedisPoolConfig, "172.27.0.7", 6379, 10000, "R4L@zaq1!");//创建jedispool对象

        Jedis jedis2 = null;

        try{

            jedis2 = pool2.getResource();
            jedis2.select(1);

            System.out.println("222 获取链接 " + jedis2.toString());

            Set<String> set =  jedis2.keys("*");
            System.out.println("222 counts: " + set.size());
            for (String k: set) {

                System.out.println("key:\t" + k + ", value:\t"+jedis2.get(k));

            }


        }catch(Exception e){
            e.printStackTrace();
        }finally {
            //还回pool中
            if(jedis2 != null){
                jedis2.close();  //关闭后，jedis对象回到池中
            }
        }
        pool2.close();
    }

}
