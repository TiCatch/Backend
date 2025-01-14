package TiCatch.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public void setValues(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, 14, TimeUnit.DAYS);
	}

}