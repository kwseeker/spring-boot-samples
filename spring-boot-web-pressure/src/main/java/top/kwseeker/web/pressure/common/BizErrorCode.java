package top.kwseeker.web.pressure.common;

public interface BizErrorCode {

    ErrorCode SUCCESS = ErrorCode.of(1, "调用成功");
    ErrorCode ERR_SYSTEM_ERROR = ErrorCode.of(10, "系统错误，稍后重试");
}
