package adam.test.lock;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisCore {

	private static final Log log = LogFactory.getLog(RedisCore.class);

	private static JedisCluster cluster = null;

	public JedisCluster getJedisCluster() {
		if (null == cluster) {
			init();
		}
		return cluster;
	}

	private synchronized void init() {
		if (null == cluster) {
			jedisInit();
		}
	}

	public synchronized void refresh() {
		if (null != cluster) {
			try {
				cluster.close();
			} catch (IOException e) {
				log.error(e, e);
			}
			cluster = null;
		}
		jedisInit();
	}

	private synchronized void jedisInit() {
		String redisConnect = "10.199.199.135:7000,10.199.199.135:7001,10.199.199.135:7002,10.199.199.135:7003,10.199.199.135:7004,10.199.199.135:7005";
		String maxTotalStr = "20";
		int maxTotal = 5;
		if (StringUtils.isNumeric(maxTotalStr)) {
			maxTotal = Integer.parseInt(maxTotalStr);
		}

		JedisCluster jedisCluster = getJedisCluster(redisConnect, maxTotal);
		cluster = jedisCluster;
	}

	private JedisCluster getJedisCluster(String redisConnect, int maxTotal) {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();

		String[] splits = redisConnect.split(",");
		for (String split : splits) {
			String[] tokens = split.split(":");
			String host = tokens[0];
			int port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : 6379;
			HostAndPort hostAndPort = new HostAndPort(host, port);
			jedisClusterNodes.add(hostAndPort);
		}
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(maxTotal);
		JedisCluster jc = new JedisCluster(jedisClusterNodes, 2000, poolConfig);
		return jc;
	}
}
