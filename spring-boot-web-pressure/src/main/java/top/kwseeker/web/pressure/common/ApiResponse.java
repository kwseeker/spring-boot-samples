package top.kwseeker.web.pressure.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public final class ApiResponse<T> {

    private Integer result;
    @JsonProperty("error_msg")
    private String errorMsg;
    private T data;

    public static <T> ApiResponse<T> buildSuccess() {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setResult(BizErrorCode.SUCCESS.getCode());
        apiResponse.setErrorMsg(BizErrorCode.SUCCESS.getErrorMsg());
        return apiResponse;
    }

    public static <T> ApiResponse<T> buildSuccess(T data) {
        ApiResponse<T> apiResponse = buildSuccess();
        apiResponse.setData(data);
        return apiResponse;
    }

    public static  <T> ApiResponse<T> buildFail(ErrorCode error) {
        return buildFail(error.getCode(), error.getErrorMsg());
    }

    public static <T> ApiResponse<T> buildFail(Integer code, String msg) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setResult(code);
        apiResponse.setErrorMsg(msg);
        return apiResponse;
    }
}
