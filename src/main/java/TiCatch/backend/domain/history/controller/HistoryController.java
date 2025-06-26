package TiCatch.backend.domain.history.controller;

import TiCatch.backend.domain.history.dto.response.LevelHistoryResponse;
import TiCatch.backend.domain.history.dto.response.TicketingHistoryPagingResponse;
import TiCatch.backend.domain.history.service.HistoryService;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.service.UserService;
import TiCatch.backend.global.response.PageResponseResult;
import TiCatch.backend.global.response.SingleResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final UserService userService;
    private final HistoryService historyService;

    @GetMapping("/ticketings")
    public ResponseEntity<PageResponseResult<TicketingHistoryPagingResponse>> getTicketingHistory(HttpServletRequest request, Pageable pageable) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new PageResponseResult<>(historyService.getTicketingHistoryWithPaged(user.getUserId(), pageable)));
    }

    @GetMapping("/levels")
    public ResponseEntity<SingleResponseResult<LevelHistoryResponse>> getLevelHistory(HttpServletRequest request) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new SingleResponseResult<>(historyService.getLevelHistory(user.getUserId())));
    }

    @GetMapping("/ticketingsByLevels")
    public ResponseEntity<PageResponseResult<TicketingHistoryPagingResponse>> getTicketingHistoryByLevel(HttpServletRequest request, Pageable pageable, @RequestParam("ticketingLevel") TicketingLevel ticketingLevel) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new PageResponseResult<>(historyService.getTicketingHistoryByLevelWithPaged(user.getUserId(), pageable, ticketingLevel)));
    }
}
