package com.ruenzuo.through.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.ruenzuo.through.R;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;


public class SignInActivity extends Activity {

    private FloatLabeledEditText edtTextUsername;
    private FloatLabeledEditText edtTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity_layout);
        edtTextUsername = (FloatLabeledEditText) findViewById(R.id.edtTextUsername);
        edtTextPassword = (FloatLabeledEditText) findViewById(R.id.edtTextPassword);
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
                        Intent openMainActivity =  new Intent(SignInActivity.this, FeedListActivity.class);
                        startActivity(openMainActivity);
                        finish();
                    }
                }

            });
        }
    }

}
