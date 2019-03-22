package com.example.tomal.jupitarplatform;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

public class TakeVideoActivity extends Activity {
    private static final int PICK_IMAGE = 123;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private VideoView videoView;
    private ImageView gallerybtn;
    private ImageView addVideoBtn;
    EditText xVideoTitle, xDescription;
    TextView xLocation;
    Uri file;
    int flag = 0;
    String path = "";
    String selectedImagePath = "";
    Button xSave;
    private MediaController mediaController;
    ProgressDialog xProgress;
    LinearLayout xLayout;
    Location location = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take__video);

//++++++++++++++++++++++++++++++++++++Internet Status Checking++++++++++++++++++++++++++++++++++++++
        BackgroundService.shouldContinue = true;
        BackgroundService.context = getApplicationContext();
        startService(new Intent(getApplicationContext(), BackgroundService.class));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        xProgress = new ProgressDialog(this);

        videoView = (findViewById(R.id.tookVideoView));
        gallerybtn = (findViewById(R.id.gallerybtn));
        addVideoBtn = (findViewById(R.id.addVideoBtn));
        xVideoTitle = (findViewById(R.id.TitleText));
        xLocation = (findViewById(R.id.LocationText));
        xLayout = (findViewById(R.id.layout));
        xDescription = (findViewById(R.id.descriptionText));
        Button backButton = findViewById(R.id.back_button);
        xSave = findViewById(R.id.video_save);

        Intent intent = getIntent();

        mediaController = new MediaController(this);

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoCamera();
            }
        });
        xLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

        xSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                saveVideo();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        fillAddress();

    }

    private void fillAddress() {

        try {
            if (location != null) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                xLocation.setText(addresses.get(0).getAddressLine(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void saveVideo() {

        xProgress.setMessage("Uploading video...");
        xProgress.show();
        OkHttpClient client = new OkHttpClient();

        MediaType MEDIA_TYPE_PNG = MediaType.get("video/mp4");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("leadid", LEAD_ID)
                .addFormDataPart("file_type", "video")
                .addFormDataPart("file_title", xVideoTitle.getText().toString())
                .addFormDataPart("file_location", xLocation.getText().toString())
                .addFormDataPart("file_desc", xDescription.getText().toString())
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
            public void onFailure(Call call, final IOException e) {

                mainHandler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        xProgress.dismiss();
                        Toast.makeText(TakeVideoActivity.this, "API Failed", Toast.LENGTH_SHORT).show();
                        System.out.println(e.getMessage());
                    }
                });
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
                            Toast.makeText(TakeVideoActivity.this, "Upload error", Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }
                    }
                });
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

    private void openVideoCamera() {
        flag = 2;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, 1);

    }

    private void openGallery() {

        flag = 1;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (flag == 1) {
                //Gallary
                if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
                    Uri videoFileUri = data.getData();

                    path = videoFileUri.getPath();

                    videoView.setVideoURI(videoFileUri);
                    videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(videoView);
                    videoView.start();
                }
            } else if (flag == 2) {
                //Capture
                Uri videoFileUri = data.getData();
                path = getPath(videoFileUri);
                videoView.setVideoURI(videoFileUri);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.start();
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            Toast.makeText(this, "Exception : " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private String getPath(Uri videoFileUri) {

        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(videoFileUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
