package com.example.hadar.AcadeMovie.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.hadar.AcadeMovie.Activity.SplashActivity;
import com.example.hadar.AcadeMovie.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Liad Pasker on 15/05/2018.
 */

/**
 * ** IMPORTANT NOTE: **
 * Messages wil be compose in FIREBASE like the following:
 * ONLY key-value push allowed.
 * 'key' = "title", 'value' = "<whatever title you wish to perform>"
 * 'key' = "body", 'value' = "<whatever body message you wish to perform>"
 */


public class AcademovieMessegingService extends FirebaseMessagingService
{
    private static final String TAG = "FCM Service";
    private Map<String,String> m_Data;
    private String m_Title;
    private String m_Body;
    private Uri m_SoundRri;


    @Override
    public void onMessageReceived(RemoteMessage i_RemoteMessage)
    {
        super.onMessageReceived(i_RemoteMessage);

        Log.e(TAG, "onMessageReceived >> [" + i_RemoteMessage + "]");

        if(validateMessegeAndUpdateParams(i_RemoteMessage) == false)
        {
            Log.e(TAG, "messege invalid!");
            return;
        }

        updateData(i_RemoteMessage);

        //Creates Notification
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,null)
                .setSmallIcon(R.drawable.cinema)
                .setContentTitle(m_Title)
                .setContentText(m_Body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(m_SoundRri);


        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void updateData(RemoteMessage i_remoteMessage)
    {
        m_Data = i_remoteMessage.getData();
        Log.e(TAG, "Message data : " + m_Data);

        m_Title= getValue("title");
        Log.e(TAG, "m_Title = "+ m_Title);

        m_Body= getValue("body");
        Log.e(TAG, "m_Body = "+ m_Body);

        updateRingtone();
    }

    private void updateRingtone()
    {
        String value;

        value = m_Data.get("sound");
        if (value != null)
        {
            if (value.equals("alert"))
            {
                m_SoundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }

            else if (value.equals("ringtone"))
            {
                m_SoundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        else
        {
            m_SoundRri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
    }

    private boolean validateMessegeAndUpdateParams(RemoteMessage i_remoteMessage)
    {
        boolean valid=true;

        if (i_remoteMessage.getNotification() == null)
        {
            valid=false;
            Log.e(TAG, "onMessageReceived() >> Notification is empty");
        }
        else
        {
            //m_Title = i_remoteMessage.getNotification().getTitle();
            //m_Body = i_remoteMessage.getNotification().getBody();
            //Log.e(TAG, "onMessageReceived() >> title= " + m_Title + " , body= " + m_Body);
        }

        // Check if message contains a data payload.
        if (i_remoteMessage.getData().size() == 0)
        {
            Log.e(TAG, "onMessageReceived() << No data doing nothing");
            valid=false;
        }

        return valid;
    }

    private String getValue(String i_Value)
    {
        String value = m_Data.get(i_Value);
        String result=null;

        if (value != null) {
            result = value;
        }
        else
        {
            Log.e(TAG, i_Value +" is null" );
        }

        return result;
    }
}

/*
        String m_Title = "title";
        String m_Body = "body";

        Log.e(TAG, "Messege Recieved >>");

        //////////////////////////////////////////
        String title = "title";
        String body = "body";

        int icon = R.drawable.cinema;
        Uri soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Map<String,String> data;
        RemoteMessage.Notification notification;

        Log.e(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getNotification() == null)
        {
            Log.e(TAG, "onMessageReceived() >> Notification is empty");
        }
        else
        {
           // notification = remoteMessage.getNotification();
           // m_Title = notification.getTitle();
           // m_Body = notification.getBody();
            Log.e(TAG, "onMessageReceived() >> title= " + title + " , body= " + body);
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() == 0)
        {
            Log.e(TAG, "onMessageReceived() << No data doing nothing");
            return;
        }


        //parse the data
        data = remoteMessage.getData();
        Log.e(TAG, "Message data : " + data);


        m_Title= getValue(data, "title");
        Log.e(TAG, "m_Title = "+ m_Title);

        m_Body= getValue(data, "body");
        Log.e(TAG, "m_Body = "+ m_Body);


        String value = data.get("small_icon");

        if (value != null  && value.equals("alarm"))
        {
            icon = R.drawable.cinema;
        }

        value = data.get("sound");
        if (value != null)
        {
            if (value.equals("alert"))
            {
                soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
            else if (value.equals("ringtone"))
            {
                soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        Intent intent = new Intent(this, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, null)
                        .setContentTitle(m_Title)
                        .setContentText(m_Body)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(icon)
                        .setSound(soundRri);


        value = data.get("action");
        if (value != null) {
            if (value.contains("share")) {
                PendingIntent pendingShareIntent = PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.cinema,"Share",pendingShareIntent));
            }
            if (value.contains("go to sale")) {
                PendingIntent pendingShareIntent = PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.cinema,"Go to sale!",pendingShareIntent));
            }

        }

NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());

                Log.e(TAG, "onMessageReceived() <<");

                //////////////////////////////////////////

                Log.e(TAG, "From: " + remoteMessage.getFrom());
                Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

 */
