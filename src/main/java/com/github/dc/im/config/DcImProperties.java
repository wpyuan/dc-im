package com.github.dc.im.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     配置属性
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/6 9:49
 */
@Configuration
@ConfigurationProperties(prefix = "dc.im")
@Data
public class DcImProperties {

    private Authorize authorize = new Authorize();

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class Authorize {
        private String publicKey;
        private String privateKey;
    }
}
