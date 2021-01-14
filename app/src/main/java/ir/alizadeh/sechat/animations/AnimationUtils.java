package ir.alizadeh.sechat.animations;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnimationUtils {



    public static void fadeAndShowButton(final FloatingActionButton button, int from, int to , final boolean isResized){
        Animation fadeout = new AlphaAnimation(from,to);
        fadeout.setInterpolator(new AccelerateInterpolator());
        fadeout.setDuration(150);
        fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animation animation) {
                if(isResized) {
                    button.setVisibility(View.VISIBLE);
                }else{
                    button.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        button.startAnimation(fadeout);

    }
}
