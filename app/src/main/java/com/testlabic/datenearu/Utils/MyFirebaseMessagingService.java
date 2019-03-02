package com.testlabic.datenearu.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.testlabic.datenearu.R;

import java.util.Map;


        public class MyFirebaseMessagingService  extends FirebaseMessagingService {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                super.onMessageReceived(remoteMessage);
                //Log.e(TAG, "The message received is " + remoteMessage);
                //showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
            }

            /*private void showNotification(String title, String body, Map<String, String> data) {
                //Intent intent = new Intent(this, UpdatesActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                Context context = getApplicationContext();
                String channelId = getString(R.string.default_notification_channel_id);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId)
                                .setSmallIcon(R.drawable.notificationicon)
                                //.setLargeIcon(bitmap)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setDefaults(Notification.DEFAULT_ALL)
                                 .setPriority(Notification.PRIORITY_HIGH);
                                //.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
                builder.setAutoCancel(true);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "INFOTSAV",
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                    builder.setSmallIcon(R.drawable.notificationicon);
                }
                else {
                    builder.setSmallIcon(R.drawable.notificationicon);
                }
                notificationManager.notify(0 , builder.build());
    }*/
}