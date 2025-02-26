package TiCatch.backend.global.constant;

public class RedisConstants {
    public static final String WAITING_QUEUE_PREFIX = "queue:ticket:";
    public static final String TICKETING_SEAT_PREFIX = "ticketingId:";
    public static final String TIME_TO_LIVE_PREFIX = "expired";
    public static final int RANGE_START_INDEX = 0;
    public static final int EXPIRE_TIMEOUT = 14;
}
