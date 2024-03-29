package com.liuhanling.rest.download;

import com.liuhanling.rest.RestHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

public class DownloadManager {

    /**
     * 保存文件
     *
     * @param response     ResponseBody
     * @param destFileName 文件名（包括文件后缀）
     * @param destFileDir  文件下载路径
     * @return 返回
     * @throws IOException
     */
    public static File saveFile(ResponseBody response, String destFileName, String destFileDir, ProgressListener progressListener) throws IOException {

        String defaultDestFileDir = RestHttpUtils.getContext().getExternalFilesDir(null) + File.separator;

        long contentLength = response.contentLength();
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.byteStream();

            long sum = 0;

            File dir = new File(null == destFileDir ? defaultDestFileDir : destFileDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                final long finalSum = sum;

                progressListener.onDownloadProgress(finalSum, contentLength, (int) ((finalSum * 1.0f / contentLength) * 100));
            }
            fos.flush();

            progressListener.onDownloadComplete(file);

            return file;

        } finally {
            try {
                response.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }

        }
    }


}
