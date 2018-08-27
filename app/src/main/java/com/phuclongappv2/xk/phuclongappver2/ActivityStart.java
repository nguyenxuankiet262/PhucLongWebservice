package com.phuclongappv2.xk.phuclongappver2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ActivityStart extends AppCompatActivity {
    private ImageView logoView;
    private Animation anim_alpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        logoView = (ImageView) findViewById(R.id.logo_Image);
        anim_alpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        anim_alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoView.startAnimation(anim_alpha);
    }
}
