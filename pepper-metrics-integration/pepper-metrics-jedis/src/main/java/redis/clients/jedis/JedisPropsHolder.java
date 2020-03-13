package redis.clients.jedis;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-16
 */
public class JedisPropsHolder {
    public static final ThreadLocal<String> NAMESPACE = new ThreadLocal<>();
}
