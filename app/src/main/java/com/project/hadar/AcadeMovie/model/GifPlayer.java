package com.project.hadar.AcadeMovie.model;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.project.hadar.AcadeMovie.R;

import pl.droidsonroids.gif.GifTextView;

/**
 * Created by Liad Pasker on 17/04/2018.
 */

public class GifPlayer
{
    public static GifTextView s_LoadingBar;
    private static final int CLEAR_ANIMATION = 0;
    private static boolean m_isGoogleSignedIn = false;
    private static boolean m_isFacebookSignedIn = false;
    private static boolean m_isAnonymousSignedIn = false;
    private static boolean m_CinemaAnim = false;


    public static void setGoogleSignIn(boolean i_result)
    {
        m_isGoogleSignedIn = i_result;
    }

    public static void setFacebookSignIn(boolean i_result)
    {
        m_isFacebookSignedIn = i_result;
    }

    public static void setAnonymousSignIn(boolean i_result)
    {
        m_isAnonymousSignedIn = i_result;
    }
    public static void setCinemaAnim(boolean i_result)
    {
        m_CinemaAnim = i_result;
    }

    public static void stopGif()
    {
        s_LoadingBar.setBackgroundResource(CLEAR_ANIMATION);
        m_isFacebookSignedIn = m_isGoogleSignedIn = m_isAnonymousSignedIn = false;
    }

    //Plays loading animations
    public static void playGif()
    {
        final Animation Loader = new AlphaAnimation(1.f, 1.f);
        Loader.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                if (m_isGoogleSignedIn == true)
                {
                    s_LoadingBar.setBackgroundResource(R.drawable.google_dark_load);

                }

                else if(m_isFacebookSignedIn == true)
                {
                    s_LoadingBar.setBackgroundResource(R.drawable.facebook_load_anim);
                }

                else if(m_isAnonymousSignedIn == true)
                {
                    s_LoadingBar.setBackgroundResource(R.drawable.anonymous);
                }
                else if(m_CinemaAnim == true)
                {
                    s_LoadingBar.setBackgroundResource(R.drawable.cinema);
                }
            }

            @Override
            public void onAnimationRepeat(Animation i_animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation i_animation)
            {
                s_LoadingBar.clearAnimation();
            }
        });

        s_LoadingBar.startAnimation(Loader);
    }
}