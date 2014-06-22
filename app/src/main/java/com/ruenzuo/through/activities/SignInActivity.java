package com.ruenzuo.through.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.ruenzuo.through.R;
import com.ruenzuo.through.application.ThroughApplication;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;


public class SignInActivity extends Activity {

    private FloatLabeledEditText edtTextUsername;
    private FloatLabeledEditText edtTextPassword;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ThroughApplication) getApplication()).getTracker(ThroughApplication.TrackerName.APP_TRACKER);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SignUpActivity.ACTION_SIGN_UP_COMPLETED);
        registerReceiver(receiver, intentFilter);
        setContentView(R.layout.sign_in_activity_layout);
        checkForCrashes();
        if (!getResources().getBoolean(R.bool.google_play_build)) {
            checkForUpdates();
        }
        edtTextUsername = (FloatLabeledEditText) findViewById(R.id.edtTextUsername);
        edtTextPassword = (FloatLabeledEditText) findViewById(R.id.edtTextPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private boolean validateSignInFields() {
        if (edtTextUsername.getText().toString().equalsIgnoreCase("")) {
            return false;
        } else if (edtTextUsername.getText().toString().trim().equalsIgnoreCase("")) {
            return false;
        } else if (edtTextPassword.getText().toString().equalsIgnoreCase("")) {
            return false;
        } else if (edtTextPassword.getText().toString().trim().equalsIgnoreCase("")) {
            return false;
        }
        return true;
    }

    public void signIn(View view) {
        if (!validateSignInFields()) {
            Toast.makeText(this, "Invalid user info.", Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setMessage("Signing in");
            ParseUser.logInInBackground(edtTextUsername.getText().toString(), edtTextPassword.getText().toString(), new LogInCallback() {

                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        if (e.getCode() == 101) {
                            Toast.makeText(SignInActivity.this, "Invalid user info.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Unexpected error. Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Intent intent;
                        if (parseUser.getBoolean("isFacebookServiceConnected") ||
                                parseUser.getBoolean("isTwitterServiceConnected")) {
                            intent = new Intent(SignInActivity.this, FeedListActivity.class);
                        } else {
                            intent = new Intent(SignInActivity.this, ConnectListActivity.class);
                            intent.putExtra("ShouldAllowDisconnect", false);
                        }
                        startActivity(intent);
                        finish();
                    }
                }

            });
        }
    }

    private void checkForCrashes() {
        CrashManager.register(this, "7b0393573930d8bcbabcaa7d6e7b005b");
    }

    private void checkForUpdates() {
        UpdateManager.register(this, "7b0393573930d8bcbabcaa7d6e7b005b");
    }

}
