package com.starkzh.qiniu_auto_deploy.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qiniu.common.QiniuException;
import com.starkzh.qiniu_auto_deploy.config.CommonConfig;
import com.starkzh.qiniu_auto_deploy.entity.Project;
import com.starkzh.qiniu_auto_deploy.util.QiniuUtil;
import com.starkzh.qiniu_auto_deploy.util.SendMailUtil;
import com.starkzh.qiniu_auto_deploy.util.Util;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/app")
public class app {

    Logger logger = LoggerFactory.getLogger(app.class);

    @Autowired
    CommonConfig commonConfig;

    @Path("/hook/{project_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public String hook(@PathParam("project_id")String project_id, @HeaderParam("X-Hub-Signature") String signature,
                       @Context HttpServletRequest request){
        Project project=null;
        List<Project> projectList=Util.readConfig(commonConfig.getProjectConfigPath());
        for(Project proj:projectList){
            if(proj.getId().equals(project_id)){
                project=proj;
                break;
            }
        }
        if(project!=null){
            logger.info("Project : {}",project.getId());
            String body=Util.getBody(request);
            if(!Util.verifyRequest(signature,body,project.getSecret())){
                logger.error("Illegal request.{}\nbody:{}",signature,body);
                return "Illegal request.";
            }
            Map<String,Object> params = new Gson().fromJson(body,Map.class);
            logger.info(body);
            String access_key=commonConfig.getAccessKey(),secret_key=commonConfig.getSecretKey();

            String mailContent="";
            int added_count=0,modified_count=0,removed_count=0;
            List<String>
                    delete_list=new ArrayList<>(),
                    add_list=new ArrayList<>();
            List<Map<String,Object>> commit_list= (List<Map<String, Object>>) params.get("commits");
            if(commit_list==null)
                return "Empty commit list.";
            for(Map<String,Object> commit:commit_list){
                List<String> added= (List<String>) commit.get("added"),
                        modified= (List<String>) commit.get("modified"),
                        removed= (List<String>) commit.get("removed");
                added_count+=added.size();
                modified_count+=modified.size();
                removed_count+=removed.size();

                add_list.addAll(added);
                add_list.addAll(modified);
                delete_list.addAll(modified);
                delete_list.addAll(removed);
            }
            mailContent+="[修改者]："+params.get("pusher").toString();
            mailContent+="\n[提交情况]：添加了"+added_count+"个文件，修改了"+modified_count+"个文件，删除了"+removed_count+"个文件";
            mailContent+="\n[执行命令]："+project.getCommand();
            mailContent+="\n[执行结果]："+Util.execCommand(project.getCommand());

            String exception_text="";
            String delete_content="",upload_content="";
            //删除在七牛上原来的文件
            for(String file:delete_list)
                try {
                    String file_key=project.getPrefix() + file;
                    if (QiniuUtil.delete(access_key, secret_key, project.getBucket(), file_key))
                        delete_content+=file_key+"\n";
                }catch (QiniuException ex){
                    exception_text+="[Delete Exception]:\n"+ex.response.toString();
                }
             //上传新的文件
           for (String file : add_list)
               try {
                   String file_path=project.getPath() + "/" + file;
                   if (QiniuUtil.upload(access_key, secret_key, project.getBucket(), file_path,
                            project.getPrefix() + file))
                       upload_content+=file_path+"\n";
                }catch (QiniuException e){
                     exception_text+="[Upload Exception]:\n"+e.response.toString()+"\n";
                }

            mailContent+="\n\n以下为在七牛云存储上的操作：\n";
            mailContent+="\n[删除的文件]："+delete_content;
            mailContent+="\n[上传的文件]："+upload_content;

            mailContent+="\n\n以下为在七牛CDN上的操作：\n";
            String flush_content="";
            //获取要刷新的url
            String[] urls = new String[add_list.size()];
            for(int i=0;i<add_list.size();i++){
                urls[i]=project.getPrefix()+add_list.get(i);
                flush_content+=urls[i]+"\n";
            }
            boolean flush_result;
            //刷新CDN文件
            flush_result=(QiniuUtil.flushFile(access_key,secret_key,project.getDomain(), urls));
            mailContent+="\n[刷新URL]：["+flush_result+"]\n"+flush_content;

            String modified_files="";
            for(String file:add_list)
                modified_files+="  "+file+"\n";

            mailContent+="\n\n--------\n";
            mailContent+="\n[异常信息]："+exception_text;
            mailContent+="\n[修改的文件]："+modified_files;
            mailContent+="\n[发送时间]："+Util.getDate();
            try {
                SendMailUtil.sendQQMail(commonConfig.getUsername(),commonConfig.getPassword(),commonConfig.getReceiver(),
                        "七牛自动部署",mailContent);
                logger.info("邮件发送成功\n{}",mailContent);
            } catch (MessagingException e) {
                logger.error("邮件发送失败");
                e.printStackTrace();
            }
        }else return project_id+" is not exist.";
        return "OK";
    }

}
