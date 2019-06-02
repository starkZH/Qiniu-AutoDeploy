package com.starkzh.qiniu_auto_deploy.util;

import com.qiniu.cdn.CdnManager;
import com.qiniu.cdn.CdnResult;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

public class QiniuUtil {

    public static boolean upload(String accessKey,String secretKey,String bucket,String localFilePath,String key) throws QiniuException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        uploadManager.put(localFilePath, key, upToken);

        return true;
    }

    public static boolean delete(String accessKey,String secretKey,String bucket,String key) throws QiniuException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        bucketManager.delete(bucket, key);

        return true;
    }

    public static boolean flushFile(String accessKey,String secretKey,String[] urls){
        Auth auth = Auth.create(accessKey, secretKey);
        CdnManager c = new CdnManager(auth);
        try{
            //单次方法调用刷新的链接不可以超过100个
            CdnResult.RefreshResult result = c.refreshUrls(urls);
            System.out.println(result.code);
        } catch (QiniuException e) {
            System.err.println(e.response.toString());
            return false;
        }
        return true;
    }

    public static boolean flushDirectory(String accessKey,String secretKey,String[] urls){
        Auth auth = Auth.create(accessKey, secretKey);
        CdnManager c = new CdnManager(auth);
        try{
            //单次方法调用刷新的链接不可以超过10个
            CdnResult.RefreshResult result = c.refreshDirs(urls);
            System.out.println(result.code);
        } catch (QiniuException e) {
            System.err.println(e.response.toString());
            return false;
        }
        return true;
    }

}
