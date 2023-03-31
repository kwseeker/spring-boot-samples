package top.kwseeker.web.pressure.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRes {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;
}