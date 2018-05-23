package com.example.hadar.AcadeMovie.model;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Liad Pasker on 15/05/2018.
 */

public class PushTokenHandler extends FirebaseInstanceIdService {
    private static final String TAG = "PushTokenHandler";
    private static String deviceToken;

    @Override
    public void onTokenRefresh() {

        Log.e(TAG, "onTokenRefresh() >>");
        // Get updated InstanceID token.
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        // The following will be used if and when server would be added as backend:
        //send registration to server
        //sendRegistrationToServer(deviceToken);

        Log.e(TAG, "onTokenRefresh() << deviceToken="+deviceToken);
    }
}

