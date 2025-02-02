package TiCatch.backend.domain.ticketing.repository;

import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketingRepository extends JpaRepository<Ticketing, Long> {
    List<Ticketing> findAllByTicketingStatusAndTicketingTimeBefore(TicketingStatus ticketingStatus, LocalDateTime now);
}
