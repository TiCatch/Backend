package TiCatch.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static TiCatch.backend.global.constant.RedisConstants.WAITING_QUEUE_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public void setValues(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, 14, TimeUnit.DAYS);
	}

	public void addToWaitingQueue(Long ticketId, String userId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		double score = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(queueKey, userId, score);
	}

	public void deleteWaitingQueue(Long ticketId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		redisTemplate.delete(queueKey);
	}

	public Long getWaitingQueueRank(Long ticketId, String userId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		Long rank = redisTemplate.opsForZSet().rank(queueKey, userId);
		if(rank == null) {
			return -1L;
		}
		return rank+1;
	}

	public Set<ZSetOperations.TypedTuple<String>> getBatchFromQueue(Long ticketId, int batchSize) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		return redisTemplate.opsForZSet().rangeWithScores(queueKey, 0, batchSize - 1);
	}

	public void removeBatchFromQueue(Long ticketId, int batchSize) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		redisTemplate.opsForZSet().removeRange(queueKey, 0, batchSize - 1);
	}
}