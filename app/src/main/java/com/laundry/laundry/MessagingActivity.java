package com.laundry.laundry;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingActivity extends FirebaseMessagingService {

    private String CHANNEL_ID = "Channel 1";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    public void showNotification(String title,String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setAutoCancel(true)
                .setContentText(message);

        NotificationManagerCompat manager  = NotificationManagerCompat.from(this);
        manager.notify(0,builder.build());
    }
}
