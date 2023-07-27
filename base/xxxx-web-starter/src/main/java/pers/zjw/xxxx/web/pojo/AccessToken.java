package pers.zjw.xxxx.web.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * access token
 *
 * @author zhangjw
 * @date 2022/12/26 0026 16:56
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken implements Customizable {
    private static final long serialVersionUID = -7576475913035805858L;

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private Integer expiresIn;
    private String scope;
}
