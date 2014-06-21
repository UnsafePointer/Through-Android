package com.ruenzuo.through.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
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
import com.ruenzuo.through.models.enums.ServiceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import twitter4j.auth.AccessToken;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class ConnectListActivity extends BaseListActivity implements OnConnectSwitchChangedListener {

    public static final int TWITTER_OAUTH_REQUEST_CODE = 1;
    private boolean shouldAllowDisconnect;
    public static final String ACTION_DISCONNECT_SERVICE = "ACTION_DISCONNECT_SERVICE";
    public static final String ACTION_CONNECT_SERVICE = "ACTION_CONNECT_SERVICE";
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

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
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
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
        Connection connectionFacebook = new Connection.ConnectionBuilder(ServiceType.SERVICE_TYPE_FACEBOOK, currentUser.getBoolean("isFacebookServiceConnected")).build();
        connections.add(connectionFacebook);
        return connections;
    }

    private void connectTwitter(AccessToken accessToken) {
        ParseObject twitterOAuth = new ParseObject("TwitterOAuth");
        twitterOAuth.put("secret", accessToken.getTokenSecret());
        twitterOAuth.put("token", accessToken.getToken());
        twitterOAuth.put("id", accessToken.getUserId());
        twitterOAuth.put("user", ParseUser.getCurrentUser());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage("Connecting");
        twitterOAuth.saveInBackground(new SaveCallback() {

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
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ConnectListActivity.ACTION_CONNECT_SERVICE);
                    sendBroadcast(broadcastIntent);
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
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ConnectListActivity.ACTION_DISCONNECT_SERVICE);
                    sendBroadcast(broadcastIntent);
                }
            }
        });
    }

    private void connectFacebook() {
        Session session = Session.getActiveSession();
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("user_photos");
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setPermissions(permissions).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, permissions, statusCallback);
        }
    }

    private void disconnectFacebook() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    @Override
    public void onConnectSwitchChanged(Switch swtService, Connection connection) {
        if (!swtService.isChecked() && !shouldAllowDisconnect) {
            swtService.setChecked(true);
            Toast.makeText(this, "You can disconnect service later on settings.", Toast.LENGTH_SHORT).show();
        } else if (connection.getType() == ServiceType.SERVICE_TYPE_TWITTER) {
            if (swtService.isChecked()) {
                startActivityForResult(new Intent(this, TwitterOAuthActivity.class), TWITTER_OAUTH_REQUEST_CODE);
            } else {
                disconnectTwitter();
            }
        } else if (connection.getType() == ServiceType.SERVICE_TYPE_FACEBOOK) {
            if (swtService.isChecked()) {
                connectFacebook();
            } else {
                disconnectFacebook();
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
        } else {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            ParseUser user = ParseUser.getCurrentUser();
            if (state == SessionState.OPENED && !user.getBoolean("isFacebookServiceConnected")) {
                ParseObject facebookOAuth = new ParseObject("FacebookOAuth");
                facebookOAuth.put("token", session.getAccessToken());
                facebookOAuth.put("user", ParseUser.getCurrentUser());
                final ProgressDialog progressDialog = new ProgressDialog(ConnectListActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
                progressDialog.setMessage("Connecting");
                facebookOAuth.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        if (e != null) {
                            Toast.makeText(ConnectListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                            ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                            adapter.notifyDataSetChanged();
                        } else {
                            ParseUser user = ParseUser.getCurrentUser();
                            user.put("isFacebookServiceConnected", true);
                            user.saveInBackground();
                            ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                            adapter.clear();
                            adapter.addAll(generateConnectionData());
                            adapter.notifyDataSetChanged();
                            invalidateOptionsMenu();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(ConnectListActivity.ACTION_CONNECT_SERVICE);
                            sendBroadcast(broadcastIntent);
                        }
                    }

                });
            } else if (state == SessionState.CLOSED && user.getBoolean("isFacebookServiceConnected")) {
                final ProgressDialog progressDialog = new ProgressDialog(ConnectListActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
                progressDialog.setMessage("Disconnecting");
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", ParseUser.getCurrentUser().getUsername());
                ParseCloud.callFunctionInBackground("disconnectFacebookForUser", params, new FunctionCallback<Object>() {

                    @Override
                    public void done(Object o, ParseException e) {
                        progressDialog.dismiss();
                        if (e != null) {
                            Toast.makeText(ConnectListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                        } else {
                            ParseUser user = ParseUser.getCurrentUser();
                            user.put("isFacebookServiceConnected", false);
                            user.saveInBackground();
                            ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                            adapter.clear();
                            adapter.addAll(generateConnectionData());
                            adapter.notifyDataSetChanged();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(ConnectListActivity.ACTION_DISCONNECT_SERVICE);
                            sendBroadcast(broadcastIntent);
                        }
                    }

                });
            } else if (state == SessionState.CLOSED_LOGIN_FAILED) {
                ConnectionAdapter adapter = (ConnectionAdapter) getListAdapter();
                adapter.notifyDataSetChanged();
            }
        }
    }

}
