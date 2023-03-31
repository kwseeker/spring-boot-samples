package top.kwseeker.web.pressure.common;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public final class ApiRequest<T> {

    @Valid
    @NotNull(message = "请求参数不能为空")
    protected T data;
}
