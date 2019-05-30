package com.starkzh.qiniu_auto_deploy.util;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.starkzh.qiniu_auto_deploy.entity.Project;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Util {

    public static boolean verifyRequest(String signature,String body,String key){
        return String.valueOf(signature).equals("sha1="+hmacSha1(body,key));
    }

    public static String getDate(){
        return  (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }

    /**
     * 读取项目配置文件
     * */
    public static List<Project> readConfig(String path){
        String res="";
        try {
            if(path.startsWith("classpath"))
                path= new ClassPathResource(path.substring(path.indexOf("/"))).getURL().getPath();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                res += line;
            }
            res=res.substring(res.indexOf("["));
        }catch (Exception e){
            e.printStackTrace();
        }
        Gson gson=new Gson();
        List<LinkedTreeMap> list=gson.fromJson(res,List.class);
        List<Project> projectList=new ArrayList<>();
        for(LinkedTreeMap map:list)
            projectList.add((Project) Util.map2Bean(Project.class,map));
        return projectList;
    }

    public static String execCommand(String command){
        String res="";
        try{
            if(command.isEmpty())
                return "Empty command";
        Process process = Runtime.getRuntime().exec(command);
        BufferedInputStream bis = new BufferedInputStream(
                process.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        String line;
        while ((line = br.readLine()) != null) {
            res+=line;
        }
        } catch (Exception e) {
        res=e.getMessage();
        e.printStackTrace();
    }
    return res;
    }

    /**
     * 获取POST请求中Body参数
     * @param request
     * @return 字符串
     */
    public static String getBody(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Object map2Bean(Class type, Map map){
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
            Object obj = type.newInstance(); // 创建 JavaBean 对象

            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();

                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);

                    Object[] args = new Object[1];
                    args[0] = value;

                    descriptor.getWriteMethod().invoke(obj, args);
                }
            }
            return obj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String hmacSha1(String src, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(src.getBytes("utf-8"));
            return Hex.encodeHexString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
