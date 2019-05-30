package com.starkzh.qiniu_auto_deploy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "common")
public class CommonConfig {

    @Value("${common.notify.username}")
    String username;

    @Value("${common.notify.password}")
    String password;

    @Value("${common.notify.receiver}")
    String receiver;

    @Value("${common.qiniu.access-key}")
    String accessKey;

    @Value("${common.qiniu.secret-key}")
    String secretKey;

    @Value("${common.project-config-path}")
    String projectConfigPath;

}
