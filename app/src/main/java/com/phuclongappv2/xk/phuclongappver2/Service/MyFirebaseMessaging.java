package com.phuclongappv2.xk.phuclongappver2.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.phuclongappv2.xk.phuclongappver2.Model.Token;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.Utils.NotificationHelper;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessaging extends FirebaseMessagingService{

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if(Common.CurrentUser != null){
            updateTokenToFirebase(s);
        }
    }

    private void updateTokenToFirebase(String token) {
        IPhucLongAPI mSerVice = Common.getAPI();
        mSerVice.insertToken(Common.CurrentUser.getPhone(), token,0)
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        Log.d("EEE","OK");
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData() != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                sendNoti(remoteMessage);
            }
            else{
                sendNotiAPI26(remoteMessage);
            }
        }
    }
    private void sendNotiAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        NotificationHelper helper;
        Notification.Builder builder;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        builder = helper.getAppNoti(notification.getTitle(),notification.getBody(),uri);

        helper.getNotificationManager().notify(new Random().nextInt(),builder.build());

    }

    private void sendNoti(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Phúc Long Coffee & Tea Exprees")
                .setSmallIcon(R.drawable.ic_local_cafe_black_24dp)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSound(uri);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(),builder.build());
    }
}
