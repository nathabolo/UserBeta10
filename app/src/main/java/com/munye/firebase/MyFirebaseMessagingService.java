package com.munye.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.munye.MapActivity;
import com.munye.MyJobsActivity;
import com.munye.user.R;
import com.munye.ViewQuotesActivity;
import com.munye.parse.DataParser;
import com.munye.utils.Const;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Akash on 1/27/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static int count;
    private Intent notificationIntent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String message = remoteMessage.getData().get("message");
        String displyMessage = remoteMessage.getData().get("message_title");
        Intent pushIntent = new Intent(Const.PushStatus.PUSH_STATUS_INTENT);
        pushIntent.putExtra(Const.PushStatus.PUSH_MESSAGE,message);
        generateNotification(this,displyMessage,message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushIntent);
    }

    private void generateNotification(Context context , String displayMessage , String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(context.getString(R.string.app_name))
                .setContentText(displayMessage);

        makeNotificationIntent(message);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0 , PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify( count , builder.build());
    }

    private void makeNotificationIntent(String message){

        switch (DataParser.parsePushMessage.getPushId(message)){
            case Const.PushStatus.ADD_QUOTE:
                count = Const.NotificationId.ADD_QUOTE;
                notificationIntent = new Intent(this , ViewQuotesActivity.class);
                break;

            case Const.PushStatus.CANCEL_QUOTE:
                count = Const.NotificationId.CANCEL_QUOTE;
                notificationIntent = new Intent(this , ViewQuotesActivity.class);
                break;

            case Const.PushStatus.PROVIDER_ON_THE_WAY:
            case Const.PushStatus.PROVIDER_ARRIVE:
            case Const.PushStatus.PROVIDER_START_JOB:
            case Const.PushStatus.JOB_DONE:
                count = Const.NotificationId.TRADESMAN_STATUS;
                notificationIntent = new Intent(this , MyJobsActivity.class);
                break;

            case Const.PushStatus.PROVIDER_CANCEL_JOB:
                count = Const.NotificationId.CANCEL_JOB;
                notificationIntent = new Intent(this , MyJobsActivity.class);
                break;

            default:
                count = 0;
                notificationIntent = new Intent(this , MapActivity.class);
                break;

        }

    }
}
