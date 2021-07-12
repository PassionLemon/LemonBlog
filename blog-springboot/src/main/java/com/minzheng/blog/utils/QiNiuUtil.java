package com.minzheng.blog.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @Author: lyh
 * @Date: 2021/06/15  11:47
 * @Description:
 */
@Slf4j
@Component
public class QiNiuUtil {
    private static String accessKey;

    private static String secretKey;

    private static String bucketName;

    private static String doMain;

    @Value("${qiniu.accessKey}")
    public void setAccessKey(String accessKey) {
        QiNiuUtil.accessKey = accessKey;
    }

    @Value("${qiniu.secretKey}")
    public void setSecretKey(String secretKey) {
        QiNiuUtil.secretKey = secretKey;
    }

    @Value("${qiniu.bucketName}")
    public void setBucketName(String bucketName) {
        QiNiuUtil.bucketName = bucketName;
    }

    @Value("${qiniu.domain}")
    public void setDoMain(String domain) {
        QiNiuUtil.doMain = domain;
    }

    /**
     * 上传图片
     *
     * @param file       图片
     * @param imageUrl 图片路径
     * @return 上传后的路径
     */
    public static String fileUpload(MultipartFile file, String imageUrl) {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucketName);
            Response response = uploadManager.put(file.getInputStream(), imageUrl + LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli(), upToken, null, null);

            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);

            return doMain + "/" + putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除七牛空间图片方法
     *
     * @param key 七牛空间中文件名称
     */
    /*public boolean removeFile(String key) {

        if (StringUtils.isNullOrEmpty(key)) {
            throw new QiniuApiException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        Auth auth = Auth.create(this.accessKey, this.secretKey);
        Configuration config = new Configuration(Region.autoRegion());
        BucketManager bucketManager = new BucketManager(auth, config);
        try {
            Response re = bucketManager.delete(this.bucket, key);
            return re.isOK();
        } catch (QiniuException e) {
            Response r = e.response;
            throw new QiniuApiException("[" + this.storageType + "]删除文件发生异常：" + r.toString());
        }
    }*/

}
