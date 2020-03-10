package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class QiniuService {
    private static  final Logger logger =  LoggerFactory.getLogger(ToutiaoUtil.class);

    public String saveImage(MultipartFile file) throws IOException {
        try {
            Configuration cfg = new Configuration(Region.region0());
//...其他参数参考类注释

            UploadManager uploadManager = new UploadManager(cfg);
            //...生成上传凭证，然后准备上传
            String accessKey = "your access key";
            String secretKey = "your secret key";
            String bucket = "your bucket name";
            //如果是Windows情况下，格式是 D:\\qiniu\\test.png
            String localFilePath = "/home/qiniu/test.png";
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            String key = null;

            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            int doPos = file.getOriginalFilename().lastIndexOf(".");
            if (doPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            Response res = uploadManager.put(file.getBytes(), fileName, upToken);
            if(res.isOK()&& res.isJson()){
                String fileKey = ToutiaoUtil.QINIU_PREFIX + JSONObject.parseObject(res.bodyString()).get("key").toString();
                return fileKey;

            }
            else{
                logger.error("云服务异常"+res.bodyString());
                return null;
            }
        }catch(QiniuException e){
            logger.error("云服务异常"+e.getMessage());
            return null;
        }

    }
}
