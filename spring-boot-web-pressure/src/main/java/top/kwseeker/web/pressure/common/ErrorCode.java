package top.kwseeker.web.pressure.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorCode {

    private Integer code;
    private String errorMsg;

    public static ErrorCode of(Integer errorCode, String errorMsg) {
        return new ErrorCode(errorCode, errorMsg);
    }
}
