package com.example.hadar.AcadeMovie.model;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Liad Pasker on 15/05/2018.
 */

public class PushTokenHandler extends FirebaseInstanceIdService
{
    private static final String TAG = "PushTokenHandler";

    @Override
    public void onTokenRefresh()
    {
        Log.e(TAG, "onTokenRefresh() >>");

        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, "onTokenRefresh() << deviceToken="+deviceToken);
    }
}