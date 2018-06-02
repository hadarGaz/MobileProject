package com.project.hadar.AcadeMovie.Model;

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

import com.project.hadar.AcadeMovie.Activity.SelectTicketsActivity;
import com.project.hadar.AcadeMovie.Activity.SplashActivity;
import com.project.hadar.AcadeMovie.R;
import com.project.hadar.AcadeMovie.Adapter.MovieWithKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * ** IMPORTANT NOTE: **
 * Messages wil be compose in FIREBASE like the following:
 * ONLY key-value push allowed.
 * 'key' = "title", 'value' = "<whatever title you wish to perform>"
 * 'key' = "body", 'value' = "<whatever body message you wish to perform>"
 * 'key' = "image", 'value' = "<image link>"
 */

public class AcademovieMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "FCM Service";
    private Map<String,String> m_Data;
    private String m_Title;
    private String m_Body;
    private Uri m_SoundRri;
    private String m_movieUID = null;
    private String m_ImageUrl;
    private Intent m_campaignIntent;


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onMessageReceived(RemoteMessage i_RemoteMessage)
    {
        super.onMessageReceived(i_RemoteMessage);
        Log.e(TAG, "onMessageReceived >> [" + i_RemoteMessage + "]");

        updateData(i_RemoteMessage);
        
        if(m_movieUID != null)
            handleSpecificMovieCampaign();
        
        else
            handleGeneralCampaign();

        Bitmap image= getBitmapFromUrl(m_ImageUrl);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                m_campaignIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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

    private void handleGeneralCampaign()
    {
        m_campaignIntent = new Intent(this, SplashActivity.class);
        m_campaignIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    private void handleSpecificMovieCampaign()
    {
        m_campaignIntent = new Intent(this, SelectTicketsActivity.class);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie/"+m_movieUID);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                MovieWithKey movieWithKey = new MovieWithKey(dataSnapshot.getValue((Movie.class)), dataSnapshot.getKey());
                m_campaignIntent.putExtra("Movie", movieWithKey.getMovie());
                m_campaignIntent.putExtra("Key", movieWithKey.getKey());
                m_campaignIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Log.e(TAG, "MOVIE NAME: "+movieWithKey.getMovie().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
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
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeStream(input);
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

        m_movieUID = getValue("movieuid");

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

    private String getValue(String i_Value)
    {
        return m_Data.get(i_Value);
    }
}