//package TiCatch.backend.domain.ticketing.service;
//
//import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
//import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
//import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
//import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
//import TiCatch.backend.domain.user.entity.User;
//import TiCatch.backend.domain.user.repository.UserRepository;
//import TiCatch.backend.global.exception.NotExistTicketException;
//import TiCatch.backend.global.exception.UnAuthorizedTicketAccessException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@SpringBootTest
//public class TicketingServiceTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TicketingService ticketingService;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        testUser = User.builder()
//                .userId(1L)
//                .userNickname("이기태")
//                .userScore(0)
//                .build();
//        userRepository.save(testUser);
//    }
//
//    @Test
//    @DisplayName("새로운 티켓팅을 등록한다.")
//    void createTicket() {
//        // given
//        LocalDateTime ticketingTime = LocalDateTime.now();
//        CreateTicketingDto createTicketingDto = CreateTicketingDto.builder()
//                .ticketingLevel(TicketingLevel.EASY)
//                .ticketingTime(ticketingTime)
//                .build();
//
//        // when
//        TicketingResponseDto newTicket = ticketingService.createTicket(createTicketingDto, testUser);
//
//        // then
//        assertThat(newTicket.getTicketingId()).isNotNull();
//        assertThat(newTicket.getTicketingStatus()).isEqualTo(TicketingStatus.WAITING);
//        assertThat(newTicket.getTicketingLevel()).isEqualTo(TicketingLevel.EASY);
//        assertThat(newTicket.getTicketingTime()).isEqualTo(ticketingTime);
//    }
//
//    @Test
//    @DisplayName("티켓팅을 조회한다.")
//    void getTicket() {
//        // given
//        LocalDateTime ticketingTime = LocalDateTime.now();
//        CreateTicketingDto createTicketingDto = CreateTicketingDto.builder()
//                .ticketingLevel(TicketingLevel.EASY)
//                .ticketingTime(ticketingTime)
//                .build();
//        TicketingResponseDto newTicket = ticketingService.createTicket(createTicketingDto, testUser);
//
//        // when
//        TicketingResponseDto targetTicket = ticketingService.getTicket(newTicket.getTicketingId(), testUser);
//
//        // then
//        assertThat(targetTicket.getTicketingId()).isNotNull();
//        assertThat(targetTicket.getTicketingStatus()).isEqualTo(TicketingStatus.WAITING);
//        assertThat(targetTicket.getTicketingLevel()).isEqualTo(TicketingLevel.EASY);
//        assertThat(targetTicket.getTicketingTime()).isEqualTo(ticketingTime);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 티켓팅은 조회할 수 없다.")
//    void notExistTicketExceptionTest() {
//        // given
//        Long notExistentTicketingId = 999L; // 존재하지 않는 임의의 번호
//
//        // when & then
//        assertThatThrownBy(() -> ticketingService.getTicket(notExistentTicketingId, testUser))
//                .isInstanceOf(NotExistTicketException.class)
//                .hasMessage("티켓팅이 존재하지 않습니다.");
//    }
//
//    @Test
//    @DisplayName("본인이 생성하지 않은 티켓팅에는 접근할 수 없습니다.")
//    void unAuthorizedTicketAccessExceptionTest() {
//        // given
//        LocalDateTime ticketingTime = LocalDateTime.now();
//        CreateTicketingDto createTicketingDto = CreateTicketingDto.builder()
//                .ticketingLevel(TicketingLevel.EASY)
//                .ticketingTime(ticketingTime)
//                .build();
//        TicketingResponseDto newTicket = ticketingService.createTicket(createTicketingDto, testUser);
//
//        User unAuthorizedUser = User.builder()
//                .userId(999L)
//                .userNickname("이기태2")
//                .userScore(0)
//                .build();
//        userRepository.save(testUser);
//
//        // when & then
//        assertThatThrownBy(() -> ticketingService.getTicket(newTicket.getTicketingId(), unAuthorizedUser))
//                .isInstanceOf(UnAuthorizedTicketAccessException.class)
//                .hasMessage("티켓팅에 접근할 권한이 없습니다.");
//    }
//}