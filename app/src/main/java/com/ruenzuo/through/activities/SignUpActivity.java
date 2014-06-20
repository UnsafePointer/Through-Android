package com.ruenzuo.through.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.ruenzuo.through.R;
import com.ruenzuo.through.extensions.BaseActivity;
import com.ruenzuo.through.helpers.NavigationHelper;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class SignUpActivity extends BaseActivity {

    private FloatLabeledEditText edtTextUsername;
    private FloatLabeledEditText edtTextPassword;
    private FloatLabeledEditText edtTextPasswordRepeat;
    private Switch swtAgreed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_layout);
        edtTextUsername = (FloatLabeledEditText) findViewById(R.id.edtTextUsername);
        edtTextPassword = (FloatLabeledEditText) findViewById(R.id.edtTextPassword);
        edtTextPasswordRepeat = (FloatLabeledEditText) findViewById(R.id.edtTextPasswordRepeat);
        swtAgreed = (Switch) findViewById(R.id.swtAgreed);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_up_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAnimated();
            return true;
        } else if (item.getItemId() == R.id.action_create) {
            signUp();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void privacyPolicy(View view) {
        NavigationHelper.openInBroswer(this, "https://www.iubenda.com/privacy-policy/895941");
    }

    public void termsOfUse(View view) {
        NavigationHelper.openInBroswer(this, "https://dl.dropboxusercontent.com/u/12352209/Through/ThroughToS.html");
    }

    private boolean validateSignUpEmail() {
        return edtTextUsername.getText().toString().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    }

    private boolean validateSignUpPasswordCorrectness() {
        return edtTextPassword.getText().toString().matches("^(?=.*\\d).{6,20}$");
    }

    private boolean validateSignUpPasswordMatch() {
        return edtTextPassword.getText().toString().equals(edtTextPasswordRepeat.getText().toString());
    }

    private void signUp() {
        if (!validateSignUpEmail()) {
            Toast.makeText(this, "Email is not valid.", Toast.LENGTH_SHORT).show();
        } else if (!validateSignUpPasswordCorrectness()) {
            Toast.makeText(this, "Password is not valid. Must be between 6 and 20 characters and contain at least one numeric digit.", Toast.LENGTH_LONG).show();
        } else if (!validateSignUpPasswordMatch()) {
            Toast.makeText(this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
        } else if (!swtAgreed.isChecked()) {
            Toast.makeText(this, "Please, agreed to terms in order to create an account.", Toast.LENGTH_LONG).show();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(edtTextUsername.getText().toString());
            user.setPassword(edtTextPassword.getText().toString());
            user.put("email", edtTextUsername.getText().toString());
            user.put("isFacebookServiceConnected", false);
            user.put("isTwitterServiceConnected", false);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setMessage("Signing in");
            user.signUpInBackground(new SignUpCallback() {

                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        Toast.makeText(SignUpActivity.this, "Unexpected error. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent =  new Intent(SignUpActivity.this, ConnectListActivity.class);
                        intent.putExtra("ShouldAllowDisconnect", false);
                        startActivity(intent);
                        finish();
                    }
                }

            });
        }
    }

}
