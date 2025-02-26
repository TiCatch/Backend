package TiCatch.backend.global.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestTemplateService {

    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> post(String url, Object requestEntity, Class<T> responseType){
        return restTemplate.postForEntity(url, requestEntity, responseType);
    }
}
