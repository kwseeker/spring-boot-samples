package top.kwseeker.web.pressure.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReq {

    @NotNull(message = "用户ID不能为空")
    @JsonProperty("user_id")
    private Long userId;
}