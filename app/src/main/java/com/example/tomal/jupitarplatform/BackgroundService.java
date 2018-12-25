package com.example.tomal.jupitarplatform;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

public class BackgroundService extends IntentService {

    public static boolean shouldContinue = false;
    public static Context context;

    public BackgroundService() {
        super("InternetThread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            try {
                if (shouldContinue == false) {
                    stopSelf();
                    return;
                } else {
                    NetworkStatus status = new NetworkStatus(context);
                    if (status.isConnected()) {
                        Thread.sleep(1000);
                    } else {
                        stopSelf();
                        startActivity(new Intent(context, NoInternetConnectionActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception : " + e.getMessage());
            }
        }
    }
}