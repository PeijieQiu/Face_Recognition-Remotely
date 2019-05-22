package com.example.face_recognition.okhttpUtils;

import android.graphics.Bitmap;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    private OkHttpClient okHttpClient;
    private static OkHttpUtils okhttpUtils;
    private final Handler handler;
    private static final int TIME_OUT = 5000;
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    private OkHttpUtils() {
        //创建一个主线程的handler
        handler = new Handler();
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    //设置外部访问的方法
    public static OkHttpUtils getInstance() {
        if (okhttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (okhttpUtils == null) {
                    return okhttpUtils = new OkHttpUtils();
                }
            }
        }
        return okhttpUtils;
    }

    public void uploadImage(String path, final Bitmap bitmap, String ImageName, final OnFinishListener onFinishListener) {

        //将内存中的bitmap转化为二进制文件流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (bitmap!=null){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        }

        // 创建MultipartBody对象
        MultipartBody.Builder mBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (bos != null)
            mBuilder.addFormDataPart("image", ImageName,RequestBody.create(MEDIA_TYPE_JPEG, bos.toByteArray()));

        MultipartBody requestBody = mBuilder.build();

        //构建请求
        Request request = new Request.Builder()
                .url(path)//地址
                .post(requestBody)//添加请求体
                .build();
        //创建一个call对象，参数就是Request请求对象
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFinishListener.onFailed(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //主线程当中执行
                            onFinishListener.onSuccess(result);
                        }
                    });
                }
            }
        });

    }

}
