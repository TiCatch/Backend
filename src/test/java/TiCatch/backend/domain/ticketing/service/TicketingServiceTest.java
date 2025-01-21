package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TicketingServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketingService ticketingService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .userNickname("이기태")
                .userScore(0)
                .build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("새로운 티켓팅을 등록한다.")
    void createTicket() {
        // given
        LocalDateTime ticketingTime = LocalDateTime.now();
        CreateTicketingDto createTicketingDto = CreateTicketingDto.builder()
                .ticketingLevel(TicketingLevel.EASY)
                .ticketingTime(ticketingTime)
                .build();

        // when
        TicketingResponseDto newTicket = ticketingService.createTicket(createTicketingDto, testUser);

        // then
        assertThat(newTicket.getTicketingId()).isNotNull();
        assertThat(newTicket.getTicketingStatus()).isEqualTo(TicketingStatus.WAITING);
        assertThat(newTicket.getTicketingLevel()).isEqualTo(TicketingLevel.EASY);
        assertThat(newTicket.getTicketingTime()).isEqualTo(ticketingTime);
    }
}