package com.ruenzuo.through.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ruenzuo.through.R;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent openMainActivity =  new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(openMainActivity);
                finish();
            }
        }, 3000);
    }

}
