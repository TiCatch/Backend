package TiCatch.backend.domain.ticketing.controller;

import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingWaitingResponseDto;
import TiCatch.backend.domain.ticketing.service.TicketingService;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.service.UserService;
import TiCatch.backend.global.response.SingleResponseResult;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class TicketingController {

    private final UserService userService;
    private final TicketingService ticketingService;

    @PostMapping("/new")
    public Mono<ResponseEntity<SingleResponseResult<TicketingResponseDto>>> createTicket(HttpServletRequest request, @RequestBody CreateTicketingDto createTicketingDto) {
        User user = userService.getUserFromRequest(request);
        return ticketingService.createTicket(createTicketingDto, user)
                .map(ticket -> ResponseEntity.ok(new SingleResponseResult<>(ticket)));
    }

    @GetMapping("/{ticketingId}")
    public ResponseEntity<SingleResponseResult<TicketingResponseDto>> getTicket(HttpServletRequest request, @PathVariable("ticketingId") Long ticketingId) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.getTicket(ticketingId, user)));
    }

    @GetMapping("/waiting/{ticketingId}/{userType}")
    public ResponseEntity<SingleResponseResult<TicketingWaitingResponseDto>> startTicketing(HttpServletRequest request, @Parameter(description = "티켓팅 ID") @PathVariable("ticketingId") Long ticketingId, @Parameter(description = "유저 유형 (ACTUAL 또는 VIRTUAL)") @PathVariable("userType") String userType) {
        String userId;
        if (userType.equals("ACTUAL")) {
            userId = userService.getUserFromRequest(request).getUserId().toString();
        } else {
            userId = "VIRTUAL:" + UUID.randomUUID().toString();
        }
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.addTicketingWaitingQueue(ticketingId, userId)));
    }

    @GetMapping("/waiting-status/{ticketingId}")
    public ResponseEntity<SingleResponseResult<TicketingWaitingResponseDto>> getTicketingWaitingStatus(HttpServletRequest request, @Parameter(description = "티켓팅 ID") @PathVariable("ticketingId") Long ticketingId) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.getTicketingWaitingStatus(ticketingId, user.getUserId())));
    }

    // 전체 좌석 예약 상태 조회
    @GetMapping("/seats")
    public Mono<ResponseEntity<SingleResponseResult<Map<String, Boolean>>>> getUserSeats(HttpServletRequest request) {
        User user = userService.getUserFromRequest(request);
        return ticketingService.getUserSeats(user)
                .map(seats -> ResponseEntity.ok(new SingleResponseResult<>(seats)));
    }
}
