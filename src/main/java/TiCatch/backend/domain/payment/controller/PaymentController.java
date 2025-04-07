package TiCatch.backend.domain.payment.controller;

import TiCatch.backend.domain.payment.service.PaymentService;
import TiCatch.backend.domain.payment.dto.ApproveRequest;
import TiCatch.backend.domain.payment.dto.ApproveResponse;
import TiCatch.backend.domain.payment.dto.ReadyResponse;
import TiCatch.backend.global.response.SingleResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<SingleResponseResult<ReadyResponse>> payReady(@RequestBody ApproveRequest approveRequest){
        String name = approveRequest.getName();
        int totalPrice = approveRequest.getTotalPrice();
        ReadyResponse readyResponse = paymentService.ready(name,totalPrice);

        return ResponseEntity.ok(new SingleResponseResult<>(readyResponse));
    }


    @GetMapping("/complete")
    public ResponseEntity<SingleResponseResult<ApproveResponse>> payCompleted(@RequestParam("tid") String tid, @RequestParam("pg_token") String pgToken){
        ApproveResponse approveResponse = paymentService.payApprove(tid, pgToken);

        return ResponseEntity.ok(new SingleResponseResult<>(approveResponse));
    }

}
