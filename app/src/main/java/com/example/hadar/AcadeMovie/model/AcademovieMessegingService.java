package com.example.hadar.AcadeMovie.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.hadar.AcadeMovie.Activity.SplashActivity;
import com.example.hadar.AcadeMovie.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
 * 'key' = "image", 'value' = "<image link>"
 */


public class AcademovieMessegingService extends FirebaseMessagingService
{
    private static final String TAG = "FCM Service";
    private Map<String,String> m_Data;
    private String m_Title;
    private String m_Body;
    private Uri m_SoundRri;
    private String m_ImageUrl;

    @Override
    public void onMessageReceived(RemoteMessage i_RemoteMessage)
    {
        super.onMessageReceived(i_RemoteMessage);
        Log.e(TAG, "onMessageReceived >> [" + i_RemoteMessage + "]");

        Toast.makeText(this,"push works!", Toast.LENGTH_LONG);

        if(validateMessageAndUpdateParams(i_RemoteMessage) == false)
        {
            Log.e(TAG, "messege invalid!");
            return;
        }

        updateData(i_RemoteMessage);
        Bitmap image= getBitmapFromUrl(m_ImageUrl);

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
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                .setContentIntent(pendingIntent)
                .setSound(m_SoundRri)
                .addAction(R.drawable.cinema, "Order Now", pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary));

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());
    }
    

    public Bitmap getBitmapFromUrl(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "Error loading image.");
            return null;
        }
    }

    private void updateData(RemoteMessage i_remoteMessage)
    {
        m_Data = i_remoteMessage.getData();
        Log.e(TAG, "Message data : " + m_Data);

        m_Title= getValue("title");
        Log.e(TAG, "m_Title = "+ m_Title);

        m_Body= getValue("body");
        Log.e(TAG, "m_Body = "+ m_Body);

        m_ImageUrl = getValue("image");

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

    private boolean validateMessageAndUpdateParams(RemoteMessage i_remoteMessage)
    {
        boolean valid=true;

        if (i_remoteMessage.getNotification() == null || i_remoteMessage.getData().size() == 0)
        {
            valid=false;
            Log.e(TAG, "onMessageReceived() >> Invalid data");
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