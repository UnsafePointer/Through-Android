package com.ruenzuo.through.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.ruenzuo.through.R;


public class SignInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_menu, menu);
        return true;
    }

    public void signIn(View view) {
        Intent openMainActivity =  new Intent(SignInActivity.this, FeedListActivity.class);
        startActivity(openMainActivity);
        finish();
    }

}
