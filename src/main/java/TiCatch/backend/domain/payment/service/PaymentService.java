package TiCatch.backend.domain.payment.service;

import TiCatch.backend.domain.payment.dto.ApproveResponse;
import TiCatch.backend.domain.payment.dto.ReadyResponse;
import TiCatch.backend.global.external.RestTemplateService;
import lombok.RequiredArgsConstructor;
import TiCatch.backend.global.constant.PaymentConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestTemplateService restTemplateService;

    @Value("${kakaopay.secretKey}")
    private String kakaopaySecretKey;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.host}")
    private String sampleHost;

    //카카오 페이 결제창 연결
    public ReadyResponse ready(String name, int totalPrice){
        HttpEntity<Map<String, String>> readyRequest = createReadyRequest(name, totalPrice);
        return restTemplateService.post(PaymentConstants.PAYMENT_READY_URL, readyRequest, ReadyResponse.class).getBody();
    }

    public ApproveResponse payApprove(String tid, String pgToken){
        HttpEntity<Map<String, String>> approveRequest = createApproveRequest(tid, pgToken);
        return restTemplateService.post(PaymentConstants.PAYMENT_APPROVE_URL, approveRequest, ApproveResponse.class).getBody();
    }

    private HttpEntity<Map<String, String>> createReadyRequest(String name, int totalPrice) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", PaymentConstants.PARTNER_ORDER_ID);
        parameters.put("partner_user_id", PaymentConstants.PARTNER_USER_ID);
        parameters.put("item_name", name);
        parameters.put("quantity", "1");
        parameters.put("total_amount", String.valueOf(totalPrice));
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", sampleHost + PaymentConstants.PAYMENT_SUCCESS_URL);
        parameters.put("cancel_url", sampleHost + PaymentConstants.PAYMENT_CANCEL_URL);
        parameters.put("fail_url", sampleHost + PaymentConstants.PAYMENT_FAIL_URL);

        return new HttpEntity<>(parameters, getHeaders());
    }

    private HttpEntity<Map<String, String>> createApproveRequest(String tid, String pgToken) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        parameters.put("partner_order_id", PaymentConstants.PARTNER_ORDER_ID);
        parameters.put("partner_user_id", PaymentConstants.PARTNER_USER_ID);
        parameters.put("pg_token", pgToken);

        return new HttpEntity<>(parameters, getHeaders());
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DEV_SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}