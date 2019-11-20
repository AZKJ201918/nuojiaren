package com.shopping.util;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import com.shopping.commons.constans.Constants;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class QiniuFileUploadUtil {

    public  static  String uploadHeadImg(@RequestParam("file") MultipartFile file)throws Exception{

        Configuration cfg=new Configuration(Zone.zone0());
        UploadManager uploadManager=new UploadManager(cfg);
        Auth auth=Auth.create(Constants.QINIU_ACCESS_KEY,Constants.QINIU_SECRET_KEY);
        String upToken=auth.uploadToken(Constants.QINIU_HEAD_IMG_BUCKET_NAME);
        Response response=uploadManager.put(file.getBytes(),null,upToken);
        DefaultPutRet putRet=new Gson().fromJson(response.bodyString(),DefaultPutRet.class);
        return Constants.QINIU_HEAD_IMG_BUCKET_URL+"/"+putRet.key;

    }


    //上传视频
    public static String uploadVideo(MultipartFile file) throws IOException {
        String key = null;
        Configuration cfg = new Configuration(Zone.zone0());
        UploadManager uploadManager = new UploadManager(cfg);
//得到上传文件的文件名，并赋值给key作为七牛存储的文件名
        key = file.getOriginalFilename();
//把文件转化为字节数组
        InputStream is = file.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = is.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        byte[] uploadBytes = bos.toByteArray();
//进行七牛的操作，不懂去七牛的sdk上看
        Auth auth=Auth.create(Constants.QINIU_ACCESS_KEY,Constants.QINIU_SECRET_KEY);
        String upToken = auth.uploadToken(Constants.QINIU_HEAD_IMG_BUCKET_NAME);
//默认上传接口回复对象
        DefaultPutRet putRet;
//进行上传操作，传入文件的字节数组，文件名，上传空间，得到回复对象
        Response response = uploadManager.put(uploadBytes, key, upToken);
        putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        //System.out.println(putRet.key);//key文件名
        System.out.println(Constants.QINIU_HEAD_IMG_BUCKET_URL + "/" + putRet.key);
        return Constants.QINIU_HEAD_IMG_BUCKET_URL + "/" + putRet.key;
    }
}
