package com.liuhanling.rest.download;

import java.io.File;

public interface ProgressListener {

    /**
     * 下载进度监听
     *
     * @param bytes     已经下载文件的大小
     * @param total     文件的大小
     * @param progress  当前进度
     */
    void onDownloadProgress(long bytes, long total, int progress);

    /**
     * 下载完成
     *
     * @param file
     */
    void onDownloadComplete(File file);

}
