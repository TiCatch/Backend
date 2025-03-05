package TiCatch.backend.global.service.redis;

import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.ticketing.repository.TicketingRepository;
import TiCatch.backend.global.config.DynamicScheduler;
import TiCatch.backend.global.exception.NotExistTicketException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisExpirationListener implements MessageListener {

    private final DynamicScheduler dynamicScheduler;
    private final TicketingRepository ticketingRepository;

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredMessageKey = message.toString();
        TicketingStatus ticketingStatus = TicketingStatus.valueOf(expiredMessageKey.split(":")[0]);
        Long expiredTicketingId = Long.valueOf(expiredMessageKey.split(":")[1]);
        Ticketing ticketing = ticketingRepository.findById(expiredTicketingId).orElseThrow(NotExistTicketException::new);

        if(ticketingStatus.equals(TicketingStatus.IN_PROGRESS)) {
            ticketing.changeTicketingStatus(TicketingStatus.IN_PROGRESS);
            dynamicScheduler.startTicketingScheduler(ticketing.getTicketingId(),ticketing.getTicketingLevel());
        } else {
            ticketing.changeTicketingStatus(TicketingStatus.COMPLETED);
            dynamicScheduler.stopScheduler(ticketing.getTicketingId());
        }
    }
}
