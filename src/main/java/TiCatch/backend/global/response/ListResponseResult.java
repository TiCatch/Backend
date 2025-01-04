package TiCatch.backend.global.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ListResponseResult<T> extends ResponseResult {

    private List<T> data;

    public ListResponseResult(List<T> data) {
        super(successResponse.statusCode, successResponse.messages, successResponse.developerMessage,
                successResponse.timestamp);
        this.data = data;
    }
}