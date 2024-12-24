package com.sbb2.infrastructer.redis;

import java.util.Map;

public interface RedisDao {
	void setData(String key, String data, long timeout);

	String getData(String key);

	void delete(String key);

	void setHashDataAll(String key, Map<?, ?> map);

	Map<?, ?> getHashDataAll(String key);

	String getHashData(String key, String hashKey);

	void setHashData(String key, String hashKey, String data);

	void setTimeout(String key, long timeout);

}
