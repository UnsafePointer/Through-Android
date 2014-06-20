package com.ruenzuo.through.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ruenzuo.through.R;
import com.ruenzuo.through.definitions.OnConnectSwitchChangedListener;
import com.ruenzuo.through.models.Connection;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class ConnectionAdapter extends ArrayAdapter<Connection> {

    public ConnectionAdapter(Context context, int resource) {
        super(context, resource);
    }

    private OnConnectSwitchChangedListener listener;

    public void setListener(Activity activity) {
        try {
            listener = (OnConnectSwitchChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnConnectSwitchChangedListener");
        }
    }

    private static class ConnectionViewHolder {
        public TextView txtViewConnectionService;
        public Switch swtServiceConnected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.connection_row_layout, null);
            Switch swtServiceConnected = (Switch) convertView.findViewById(R.id.swtServiceConnected);
            swtServiceConnected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Connection connection = (Connection) buttonView.getTag();
                    Switch swtServiceConnected = (Switch) buttonView;
                    listener.onConnectSwitchChanged(swtServiceConnected, connection);
                }

            });
            ConnectionViewHolder holder = new ConnectionViewHolder();
            holder.txtViewConnectionService = (TextView) convertView.findViewById(R.id.txtViewConnectionService);
            holder.swtServiceConnected = swtServiceConnected;
            convertView.setTag(holder);
        }
        ConnectionViewHolder holder = (ConnectionViewHolder)convertView.getTag();
        Connection connection = getItem(position);
        holder.txtViewConnectionService.setText(connection.getType().toString());
        holder.swtServiceConnected.setTag(connection);
        return convertView;
    }

}
