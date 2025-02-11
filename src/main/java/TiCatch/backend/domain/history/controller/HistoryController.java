package TiCatch.backend.domain.history.controller;

import TiCatch.backend.domain.history.dto.response.HistoryPagingResponse;
import TiCatch.backend.domain.history.service.HistoryService;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.service.UserService;
import TiCatch.backend.global.response.PageResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final UserService userService;
    private final HistoryService historyService;

    @GetMapping("/mypage")
    public ResponseEntity<PageResponseResult<HistoryPagingResponse>> getMyPageList(HttpServletRequest request, Pageable pageable) {
        User user = userService.getUserFromRequest(request);
        return ResponseEntity.ok(new PageResponseResult<>(historyService.getHistoryListWithPaged(user.getUserId(), pageable)));
    }
}
