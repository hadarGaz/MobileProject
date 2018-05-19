package com.example.hadar.exercise02.Analytics;

import android.content.Context;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.flurry.android.FlurryAgent;

public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager mInstance = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private  MixpanelAPI mMixpanel;

    private AnalyticsManager() {
    }

    public static AnalyticsManager getInstance() {

        if (mInstance == null) {
            mInstance = new AnalyticsManager();
        }
        return (mInstance);
    }

    public void init(Context context) {

        new FlurryAgent.Builder().withLogEnabled(true).build(context, "MR4FKRPWNRZGRG4WNQ87");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

       // Appsee.start();

        mMixpanel = MixpanelAPI.getInstance(context, "4c47f046331cc4a69a1f14e6ab48ffc4");

    }
}
