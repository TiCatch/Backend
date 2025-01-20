package TiCatch.backend.domain.ticketing.controller;

import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.service.TicketingService;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.service.UserService;
import TiCatch.backend.global.response.SingleResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class TicketingController {

    private final UserService userService;
    private final TicketingService ticketingService;

    @PostMapping("/new")
    public ResponseEntity<SingleResponseResult<TicketingResponseDto>> createTicket(HttpServletRequest request, @RequestBody CreateTicketingDto createTicketingDto) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok().body(new SingleResponseResult<>(ticketingService.createTicket(createTicketingDto, user)));
    }

    @GetMapping("/{ticketingId}")
    public ResponseEntity<SingleResponseResult<TicketingResponseDto>> getTicket(HttpServletRequest request, @PathVariable Long ticketingId) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new SingleResponseResult<>(ticketingService.getTicket(ticketingId, user)));
    }
}
