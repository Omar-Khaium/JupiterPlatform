package com.example.tomal.jupitarplatform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.COOKIE_FOR_API;
import static com.example.tomal.jupitarplatform.MainActivity.LEAD_ID;

public class TakeImageActivity extends AppCompatActivity {

    private ImageView takeImage;
    private ImageView gallerybtn;
    int flag = 0;
    EditText photoTitleText, descriptionText, locationText;
    private ImageView addPhotoBtn;
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_IMAGE = 101;
    private Uri imageUri;
    Button submit;
    File file;
    String path = "";
    ProgressDialog xProgress;
    LinearLayout xLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take__image);

//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        takeImage = (findViewById(R.id.tookImageView));
        gallerybtn = (findViewById(R.id.gallerybtn));
        addPhotoBtn = (findViewById(R.id.addPhotoBtn));
        submit = (findViewById(R.id.sendImage));
        photoTitleText = (findViewById(R.id.photoTitleText));
        descriptionText = (findViewById(R.id.descriptionText));
        locationText = (findViewById(R.id.photoLocationText));
        xLayout = (findViewById(R.id.layout));
        xProgress = new ProgressDialog(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Back Button
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();

            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        xLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void submitData() {
        xProgress.setMessage("Uploading Photo...");
        xProgress.show();
        OkHttpClient client = new OkHttpClient();

        MediaType MEDIA_TYPE_PNG = MediaType.get("image/png");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("leadid", LEAD_ID)
                .addFormDataPart("file_type", "image")
                .addFormDataPart("file_title", photoTitleText.getText().toString())
                .addFormDataPart("file_location", locationText.getText().toString())
                .addFormDataPart("file_desc", descriptionText.getText().toString())
                .addFormDataPart("ufile", path.substring(path.lastIndexOf("/")),
                        RequestBody.create(MEDIA_TYPE_PNG, new File(path)))
                .build();

        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/apifileupload.php")
                .post(requestBody)
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                xProgress.dismiss();
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String myResponse = response.body().string();

                mainHandler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        try {
                            xProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ViewMediaActivity.class));
                            System.out.println(myResponse);

                        } catch (Exception e) {
                            xProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }
                    }
                });
            }
        });
    }

    //Open Camera
    private void openCamera() {
        flag = 2;
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, TAKE_IMAGE);
    }


    //Open Gallery
    private void openGallery() {
        flag = 1;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //flag = 1;
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case PICK_IMAGE:
                        takeImage.setImageURI(data.getData());
                        Cursor cursor = null;
                        try {
                            String[] proj = {MediaStore.Images.Media.DATA};
                            cursor = TakeImageActivity.this.getContentResolver().query(data.getData(), proj, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            path = cursor.getString(column_index);
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    case TAKE_IMAGE:

                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        takeImage.setImageBitmap(photo);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String x = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), photo, "Title", null);
                        Uri uri =  Uri.parse(x);
                        Cursor cursorTakeImage = null;
                        try {
                            String[] proj = {MediaStore.Images.Media.DATA};
                            cursor = TakeImageActivity.this.getContentResolver().query(uri, proj, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            path = cursor.getString(column_index);
                        } finally {
                            if (cursorTakeImage != null) {
                                cursorTakeImage.close();
                            }
                        }

                }
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
