package TiCatch.backend.global.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SingleResponseResult<T> extends ResponseResult {

    private T data;

    public SingleResponseResult(T data) {
        super(successResponse.statusCode, successResponse.messages, successResponse.developerMessage,
                successResponse.timestamp);
        this.data = data;
    }
}