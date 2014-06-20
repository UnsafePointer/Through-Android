package com.ruenzuo.through.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseUser;
import com.ruenzuo.through.R;
import com.ruenzuo.through.adapters.ConnectionAdapter;
import com.ruenzuo.through.definitions.OnConnectSwitchChangedListener;
import com.ruenzuo.through.models.Connection;
import com.ruenzuo.through.models.enums.ServiceType;

import java.util.ArrayList;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class ConnectListActivity extends ListActivity implements OnConnectSwitchChangedListener {

    private boolean shouldAllowDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldAllowDisconnect = getIntent().getExtras().getBoolean("ShouldAllowDisconnect");
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
            getMenuInflater().inflate(R.menu.connect_menu, menu);
        }
        return true;
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

    @Override
    public void onConnectSwitchChanged(Switch swtService, Connection connection) {
        if (!swtService.isChecked() && !shouldAllowDisconnect) {
            swtService.setChecked(true);
            Toast.makeText(this, "You can disconnect service later on settings.", Toast.LENGTH_SHORT).show();
        }
        if (connection.getType() == ServiceType.SERVICE_TYPE_TWITTER) {
            
        }
    }

}
