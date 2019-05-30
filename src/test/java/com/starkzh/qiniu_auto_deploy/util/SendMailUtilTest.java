package com.starkzh.qiniu_auto_deploy.util;

import com.starkzh.qiniu_auto_deploy.QiniuAutoDeployApplication;
import com.starkzh.qiniu_auto_deploy.config.CommonConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= QiniuAutoDeployApplication.class)// 指定spring-boot的启动类
public class SendMailUtilTest {

    @Autowired(required=false)
    CommonConfig commonConfig;

    @Test
    public void sendQQMail() {
        try {
            SendMailUtil.sendQQMail(commonConfig.getUsername(),commonConfig.getPassword(),commonConfig.getReceiver(),
                    "Hello","Test");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}