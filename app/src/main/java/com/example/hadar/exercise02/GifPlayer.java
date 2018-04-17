package com.example.hadar.exercise02;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import pl.droidsonroids.gif.GifTextView;

/**
 * Created by Liad Pasker on 17/04/2018.
 */

public class GifPlayer {
    private static final int CLEAR_ANIMATION = 0;
    private static boolean m_googleSignedIn = false;
    private static boolean m_facebookSignedIn = false;
    private static boolean m_anonymousSignedIn = false;
    public static GifTextView m_LoadingBar;


    public static void setGoogleSignIn(boolean i_result)
    {
        m_googleSignedIn=i_result;
    }

    public static void setFacebookSignIn(boolean i_result)
    {
        m_facebookSignedIn=i_result;
    }

    public static void setanonymousSignIn(boolean i_result)
    {
        m_anonymousSignedIn=i_result;
    }

    public static void stopGif()
    {
        m_LoadingBar.setBackgroundResource(CLEAR_ANIMATION);
        m_facebookSignedIn=m_googleSignedIn=m_anonymousSignedIn=false;
    }

    public static void playGif() //plays loading animations
    {
        final Animation Loader = new AlphaAnimation(1.f, 1.f);
        Loader.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                if (m_googleSignedIn == true)
                {
                    m_LoadingBar.setBackgroundResource(R.drawable.google_dark_load);

                }

                else if(m_facebookSignedIn == true)
                {
                    m_LoadingBar.setBackgroundResource(R.drawable.facebook_load_anim);
                }

                else if(m_anonymousSignedIn == true)
                {
                    m_LoadingBar.setBackgroundResource(R.drawable.anonymous);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                m_LoadingBar.clearAnimation();
            }
        });
        m_LoadingBar.startAnimation(Loader);

    }

}
