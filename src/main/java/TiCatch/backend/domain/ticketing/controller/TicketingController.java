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

import static TiCatch.backend.global.constant.UserConstants.ACTUAL_USERTYPE;
import static TiCatch.backend.global.constant.UserConstants.VIRTUAL_USERTYPE;

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

    // 티켓팅 취소
    @PatchMapping("/{ticketingId}")
    public ResponseEntity<SingleResponseResult<TicketingResponseDto>> cancelTicket(HttpServletRequest request, @PathVariable("ticketingId") Long ticketingId) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.cancelTicket(ticketingId, user)));
    }

    @GetMapping("/waiting/{ticketingId}/{userType}")
    public ResponseEntity<SingleResponseResult<TicketingWaitingResponseDto>> startTicketing(HttpServletRequest request, @Parameter(description = "티켓팅 ID") @PathVariable("ticketingId") Long ticketingId, @Parameter(description = "유저 유형 (ACTUAL 또는 VIRTUAL)") @PathVariable("userType") String userType) {
        String userId;
        if (userType.equals(ACTUAL_USERTYPE)) {
            userId = userService.getUserFromRequest(request).getUserId().toString();
        } else {
            userId = VIRTUAL_USERTYPE + UUID.randomUUID().toString();
        }
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.addTicketingWaitingQueue(ticketingId, userId)));
    }

    @GetMapping("/waiting-status/{ticketingId}")
    public ResponseEntity<SingleResponseResult<TicketingWaitingResponseDto>> getTicketingWaitingStatus(HttpServletRequest request, @Parameter(description = "티켓팅 ID") @PathVariable("ticketingId") Long ticketingId) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.getTicketingWaitingStatus(ticketingId, user.getUserId())));
    }

    // 전체 좌석 예약 상태 조회
    @GetMapping("/seats/{ticketingId}")
    public Mono<ResponseEntity<SingleResponseResult<Map<String, Boolean>>>> getTicketingSeats(@PathVariable("ticketingId") Long ticketingId) {
        return ticketingService.getTicketingSeats(ticketingId)
                .map(seats -> ResponseEntity.ok(new SingleResponseResult<>(seats)));
    }

    // 특정 구역 좌석 예약 상태 조회
    @GetMapping("/seats/{ticketingId}/{section}")
    public Mono<ResponseEntity<SingleResponseResult<Map<String, Boolean>>>> getSectionSeats(@PathVariable("ticketingId") Long ticketingId, @PathVariable String section) {
        return ticketingService.getSectionSeats(ticketingId, section)
                .map(seats -> ResponseEntity.ok(new SingleResponseResult<>(seats)));
    }

    // 좌석 예약 가능 여부 확인
    @GetMapping("/seats/{ticketingId}/check/{seatKey}")
    public ResponseEntity<SingleResponseResult<String>> checkSeatAvailability(@PathVariable("ticketingId") Long ticketingId, @PathVariable String seatKey) {
        ticketingService.isAvailable(ticketingId, seatKey);
        return ResponseEntity.ok(new SingleResponseResult<>("예매가 가능한 좌석입니다."));
    }
}
