package com.premar.radiomunabuddu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Telephony;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class AppUtils {

    public AppUtils(){}

    public static final int RMB_NOTIFICATION_ID = 222;
    public static final String RMB_CHANNEL_ID = "Radio_MB_channel";
    public static final int ALL_PERMISSIONS = 192;

    public static void showNotification(Context context, NotificationManager notificationManager) {

        //create the notification channel for the app
        createWorkerNotificationChannel(notificationManager);

        Intent radioIntent = new Intent(context, HomeActivity.class);
        radioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent workerPendingIntent = PendingIntent.getActivity(context,
                RMB_NOTIFICATION_ID,
                radioIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = getNotificationBuilder(context);
        builder.setContentIntent(workerPendingIntent);

        notificationManager.notify(RMB_NOTIFICATION_ID, builder.build());
    }

    private static void createWorkerNotificationChannel(NotificationManager notificationManager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Notification channel targeting Android 8 and above
            NotificationChannel channel = new NotificationChannel(RMB_CHANNEL_ID,
                    "Worker Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.BLACK);
            channel.setDescription("Notification from Radio MB FM");

            notificationManager.createNotificationChannel(channel);
        }
    }



    private static NotificationCompat.Builder getNotificationBuilder(Context context){
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.buddu3);

        return new NotificationCompat.Builder(context, RMB_CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Radio MB FM")
                .setContentText("You're listening to Radio MB FM")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);
    }

    //Check for available network
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getDefaultSmsAppPackageName(@NonNull Context context) {
        String defaultSmsPackageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            return defaultSmsPackageName;
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                return resolveInfos.get(0).activityInfo.packageName;

        }
        return null;
    }

}
