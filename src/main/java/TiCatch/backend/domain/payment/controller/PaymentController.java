package TiCatch.backend.domain.payment.controller;

import TiCatch.backend.domain.payment.service.PaymentService;
import TiCatch.backend.domain.payment.util.SessionUtils;
import TiCatch.backend.domain.payment.dto.ApproveRequest;
import TiCatch.backend.domain.payment.dto.ApproveResponse;
import TiCatch.backend.domain.payment.dto.ReadyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<ReadyResponse> payReady(@RequestBody ApproveRequest approveRequest){
        String name = approveRequest.getName();
        int totalPrice = approveRequest.getTotalPrice();

        ReadyResponse readyResponse = paymentService.ready(name,totalPrice);

        SessionUtils.addAttribute("tid", readyResponse.getTid());
        return ResponseEntity.ok(readyResponse);
    }


    @GetMapping("/complete")
    public ResponseEntity<ApproveResponse> payCompleted(@RequestParam("pg_token") String pgToken){
        String tid = SessionUtils.getStringAttributeValue("tid");
        ApproveResponse approveResponse = paymentService.payApprove(tid, pgToken);

        return ResponseEntity.ok(approveResponse);
    }

}
