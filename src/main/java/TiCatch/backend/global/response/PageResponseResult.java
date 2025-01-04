package TiCatch.backend.global.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class PageResponseResult<T> extends ResponseResult {

    private Page<T> data;

    public PageResponseResult(Page<T> data) {
        super(successResponse.statusCode, successResponse.messages, successResponse.developerMessage,
                successResponse.timestamp);
        this.data = data;
    }
}