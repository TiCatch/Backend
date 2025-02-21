package TiCatch.backend.domain.payment.service;

import TiCatch.backend.domain.payment.dto.ApproveResponse;
import TiCatch.backend.domain.payment.dto.ReadyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${kakaopay.secretKey}")
    private String kakaopaySecretKey;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.host}")
    private String sampleHost;

    //카카오 페이 결제창 연결
    public ReadyResponse ready(String name, int totalPrice){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("partner_order_id", "1234567890");
        parameters.put("partner_user_id", "ticatch");
        parameters.put("item_name" , name);
        parameters.put("quantity","1");
        parameters.put("total_amount", String.valueOf(totalPrice));
        parameters.put("tax_free_amount","0");
        parameters.put("approval_url", sampleHost +"/order/pay/completed");
        parameters.put("cancel_url",sampleHost + "/order/pay/cancel");
        parameters.put("fail_url", sampleHost + "order/pay/fail");

        HttpEntity<Map<String,String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();

        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";

        ResponseEntity<ReadyResponse> responseEntity = template.postForEntity(url, requestEntity, ReadyResponse.class);
        return responseEntity.getBody();

    }

    public ApproveResponse payApprove(String tid, String pgToken){
        Map<String, String> parameters = new HashMap<>();

        parameters.put("cid",cid);
        parameters.put("tid", tid);
        parameters.put("partner_order_id", "1234567890");
        parameters.put("partner_user_id", "ticatch");
        parameters.put("pg_token", pgToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/approve";
        ApproveResponse approveResponse = template.postForObject(url,requestEntity, ApproveResponse.class);

        return approveResponse;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DEV_SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
