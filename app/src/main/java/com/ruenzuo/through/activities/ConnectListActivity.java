package com.ruenzuo.through.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ruenzuo.through.R;
import com.ruenzuo.through.adapters.ConnectionAdapter;
import com.ruenzuo.through.definitions.OnConnectSwitchChangedListener;
import com.ruenzuo.through.extensions.BaseListActivity;
import com.ruenzuo.through.models.Connection;
import com.ruenzuo.through.models.Media;
import com.ruenzuo.through.models.enums.ServiceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class ConnectListActivity extends BaseListActivity implements OnConnectSwitchChangedListener {

    static final int TWITTER_OAUTH_REQUEST_CODE = 1;
    private boolean shouldAllowDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldAllowDisconnect = getIntent().getExtras().getBoolean("ShouldAllowDisconnect");
        if (shouldAllowDisconnect) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.connect_list_activity_layout);
        ConnectionAdapter adapter = new ConnectionAdapter(this, R.layout.connection_row_layout);
        adapter.setListener(this);
        setListAdapter(adapter);
        adapter.addAll(generateConnectionData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.getBoolean("isTwitterServiceConnected") ||
                currentUser.getBoolean("isFacebookServiceConnected")) {
            if (!shouldAllowDisconnect) {
                getMenuInflater().inflate(R.menu.connect_menu, menu);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowDisconnect) {
            finishAnimated();
        } else {
            finish();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAnimated();
            return true;
        } else if (item.getItemId() == R.id.action_done) {
            Intent intent = new Intent(ConnectListActivity.this, FeedListActivity.class);
            intent.putExtra("ShouldGenerateFeed", true);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private ArrayList<Connection> generateConnectionData() {
        ArrayList<Connection> connections = new ArrayList<Connection>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Connection connectionTwitter = new Connection.ConnectionBuilder(ServiceType.SERVICE_TYPE_TWITTER, currentUser.getBoolean("isTwitterServiceConnected")).build();
        connections.add(connectionTwitter);
        return connections;
    }

    private void connectTwitter(AccessToken accessToken) {
        ParseObject gameScore = new ParseObject("TwitterOAuth");
        gameScore.put("secret", accessToken.getTokenSecret());
        gameScore.put("token", accessToken.getToken());
        gameScore.put("id", accessToken.getUserId());
        gameScore.put("user", ParseUser.getCurrentUser());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage("Connecting");
        gameScore.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(ConnectListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                    ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                    adapter.notifyDataSetChanged();
                } else {
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("isTwitterServiceConnected", true);
                    user.saveInBackground();
                    ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                    adapter.clear();
                    adapter.addAll(generateConnectionData());
                    adapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
                }
            }

        });
    }

    private void disconnectTwitter() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage("Disconnecting");
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", ParseUser.getCurrentUser().getUsername());
        ParseCloud.callFunctionInBackground("disconnectTwitterForUser", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(ConnectListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                } else {
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("isTwitterServiceConnected", false);
                    user.saveInBackground();
                    ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                    adapter.clear();
                    adapter.addAll(generateConnectionData());
                    adapter.notifyDataSetChanged();
                    //TODO: Broadcast results.
                }
            }
        });
    }

    @Override
    public void onConnectSwitchChanged(Switch swtService, Connection connection) {
        if (!swtService.isChecked() && !shouldAllowDisconnect) {
            swtService.setChecked(true);
            Toast.makeText(this, "You can disconnect service later on settings.", Toast.LENGTH_SHORT).show();
        }
        else if (connection.getType() == ServiceType.SERVICE_TYPE_TWITTER) {
            if (swtService.isChecked()) {
                startActivityForResult(new Intent(this, TwitterOAuthActivity.class), TWITTER_OAUTH_REQUEST_CODE);
            } else {
                disconnectTwitter();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TWITTER_OAUTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                AccessToken accessToken = (AccessToken) data.getExtras().getSerializable("AccessToken");
                connectTwitter(accessToken);
            } else if (resultCode == TwitterOAuthActivity.RESULT_TWITTER_OAUTH_ERROR) {
                Toast.makeText(this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Authorization cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
