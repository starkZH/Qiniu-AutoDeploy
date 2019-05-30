# Qiniu-AutoDeploy
Qiniu-AutoDeploy（七牛自动化部署）项目旨在让静态网页在七牛上部署自动化，用户只需按要求配置好文件及WebHook即可。<br>
本项目具有以下特点：
- 通过WebHook自动把本地文件上传到七牛云存储上，再刷新CDN缓存。
- 可进行多个项目的WebHook及部署
- 通过JSON文件对项目信息进行管理

## 使用方法
1、本项目需要Tomcat运行环境，您也可以自己将其打包成jar文件。
<br>

2、application.yml 文件说明：
<br>
>  \# 自动部署后发送邮件需要的信息<br>
  notify:<br>
    username: # 用来发送的邮箱账号<br>
    password: # 用来发送的邮箱密码<br>
    receiver: # 接收者的邮箱<br>
  \# 七牛的相关密钥<br>
  qiniu:<br>
    access-key: your access key<br>
    secret-key: your secret key<br>
  \# 项目的配置文件路径  
  project-config-path: # 支持绝对路径以及classpath<br>

3、配置文件说明：
> [{<br>
  "id": "你的项目名称",<br>
  "path": "要上传的文件的父目录路径",<br>
  "command": "要执行的命令",<br>
  "prefix": "文件的前缀",<br>
  "bucket": "bucket名称",<br>
  "domain": "bucket所绑定的域名",<br>
  "secret": "WebHook的密钥"<br>
}]  

4、默认通知URL：/webhook/app/hook/{project_id}


  
