package TiCatch.backend.domain.ticketing.repository;

import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketingRepository extends JpaRepository<Ticketing, Long> {
    Optional<Ticketing> findByUserAndTicketingStatusIn(User user, List<TicketingStatus> ticketingStatus);
}