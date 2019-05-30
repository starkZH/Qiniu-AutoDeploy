package com.starkzh.qiniu_auto_deploy.entity;

import lombok.Data;

@Data
public class Project {
        /**
         * 设置项目的id，即通知url最后的部分
         */
        private String id;

        /**
         * 项目的绝对路径，程序会自动pull
         */
        private String path;

        /**
         * pull后要执行的命令
         */
        private String command;

        /**
         * 在七牛上的文件前缀
         */
        private String prefix;

        /**
         * 要上传到哪个bucket
         */
        private String bucket;

        /**
         * Webhook时的签名密钥
         */
        private String secret;

        /**
         * 对象存储绑定的域名
         */
        private String domain;
}