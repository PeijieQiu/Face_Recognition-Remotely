package com.example.face_recognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.face_recognition.okhttpUtils.OkHttpUtils;
import com.example.face_recognition.okhttpUtils.OnFinishListener;

public class MainActivity extends AppCompatActivity implements OnFinishListener {

    private Button takePhoto, uploadImage;
    private ImageView imageView;
    private final int CAMERA_REQUEST = 8888;
    private Bitmap photo;
    private static final String baseUrl = "your server address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = findViewById(R.id.take_photo);
        uploadImage = findViewById(R.id.upload_image);
        imageView = findViewById(R.id.picture);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(baseUrl+"image_upload_handler/", photo, "test.jpg");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    private void uploadImage(String url, Bitmap bitmap, String ImageName) {
        long startTime = System.currentTimeMillis();
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        okHttpUtils.uploadImage(url, bitmap, ImageName, this);
        long endTime = System.currentTimeMillis();
        long operatingTime = endTime - startTime;
        System.out.println("Operating Time:"+operatingTime+"ms");
    }

    @Override
    public void onFailed(String msg) {
        System.out.println("MainActivity : " + msg + " 线程名 : " + Thread.currentThread().getName());
    }

    @Override
    public void onSuccess(Object obj) {
        System.out.println("MainActivity : " + obj.toString() + "\n 线程名 : " + Thread.currentThread().getName());
    }


}
