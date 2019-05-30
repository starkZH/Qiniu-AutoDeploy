package com.starkzh.qiniu_auto_deploy.config;

import com.starkzh.qiniu_auto_deploy.controller.app;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(app.class);

    }

}
