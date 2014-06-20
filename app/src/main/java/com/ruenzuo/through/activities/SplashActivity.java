package com.ruenzuo.through.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.ruenzuo.through.R;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);
        if (ParseUser.getCurrentUser() != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setMessage("Validation session");
            ParseUser.getCurrentUser().refreshInBackground(new RefreshCallback() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        ParseUser.logOut();
                        Toast.makeText(SplashActivity.this, "Your account has been deleted, please contact support.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    } else {
                        Intent intent;
                        if (!ParseUser.getCurrentUser().getBoolean("isTwitterServiceConnected") &&
                            !ParseUser.getCurrentUser().getBoolean("isFacebookServiceConnected")) {
                            intent = new Intent(SplashActivity.this, ConnectListActivity.class);
                            intent.putExtra("ShouldAllowDisconnect", false);

                        } else {
                            intent = new Intent(SplashActivity.this, FeedListActivity.class);
                        }
                        startActivity(intent);
                    }
                    finish();
                }
            });
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
            }, 1500);
        }
    }

}
