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
	private final String WAITING_QUEUE_PREFIX = "queue:ticket:";

	public void setValues(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, 14, TimeUnit.DAYS);
	}

	public Long addToWaitingQueue(Long ticketId, String userId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		double score = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(queueKey, userId, score);
		return getWaitingQueueRank(ticketId, userId);
	}

	private Long getWaitingQueueRank(Long ticketId, String userId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		Long rank = redisTemplate.opsForZSet().rank(queueKey, userId);
		if(rank == null) {
			return -1L;
		}
		return rank+1;
	}
}