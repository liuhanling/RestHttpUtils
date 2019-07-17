package com.liuhanling.rest.upload;

import com.liuhanling.rest.factory.ApiFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 文件上传
 *
 * @author liuhanling
 * @date 2019-02-22 11:18
 */
public class UploadHelper {

    public static final String DEFAULT_FILE_NAME = "files";
    public static final String DEFAULT_UPLOAD_KEY = "defaultUploadUrlKey";
    public static final String DEFAULT_UPLOAD_URL = "https://api.github.com/";

    /**
     * 上传一张图片
     *
     * @param url 上传图片的服务器url
     * @param filePath  图片路径
     * @return Observable
     */
    public static Observable<ResponseBody> uploadFile(String url, String filePath) {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        return uploadFiles(url, DEFAULT_FILE_NAME, null, filePaths);
    }

    /**
     * 只上传图片
     *
     * @param url 上传图片的服务器url
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<ResponseBody> uploadFiles(String url, List<String> filePaths) {
        return uploadFiles(url, DEFAULT_FILE_NAME, null, filePaths);
    }

    /**
     * 图片和参数同时上传的请求
     *
     * @param url 上传图片的服务器url
     * @param paramsMap 普通参数
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<ResponseBody> uploadFiles(String url, Map<String, Object> paramsMap, List<String> filePaths) {
        return uploadFiles(url, DEFAULT_FILE_NAME, paramsMap, filePaths);
    }

    /**
     * 图片和参数同时上传的请求
     *
     * @param url 上传图片的服务器url
     * @param fileName  后台协定的接受图片的name（没特殊要求就可以随便写）
     * @param paramsMap 普通参数
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<ResponseBody> uploadFiles(String url, String fileName, Map<String, Object> paramsMap, List<String> filePaths) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (null != paramsMap) {
            for (String key : paramsMap.keySet()) {
                builder.addFormDataPart(key, (String) Objects.requireNonNull(paramsMap.get(key)));
            }
        }

        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart(fileName, file.getName(), body);
        }

        List<MultipartBody.Part> parts = builder.build().parts();

        return ApiFactory.getInstance()
                .createApi(DEFAULT_UPLOAD_KEY, DEFAULT_UPLOAD_URL, UploadFileApi.class)
                .uploadFiles(url, parts);
    }
}
