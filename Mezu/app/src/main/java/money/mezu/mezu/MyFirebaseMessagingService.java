package money.mezu.mezu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground we handle notification messages here.
        Log.d("", "MyFirebaseMessagingService::onMessageReceived: From: " + remoteMessage.getFrom());
        Log.d("", "MyFirebaseMessagingService::onMessageReceived: Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d("", "MyFirebaseMessagingService::onMessageReceived: Notification Data: " + remoteMessage.getData());
        Log.d("", "MyFirebaseMessagingService::onMessageReceived: Notification Title: " + remoteMessage.getNotification().getTitle());

        // Creates an explicit intent for an Activity in the app
        Intent notificationIntent = new Intent(this, OpenBudgetViewWhenReadyActivity.class);
        notificationIntent.putExtra("bid", remoteMessage.getData().get("bid"));
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mezu_logo)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
