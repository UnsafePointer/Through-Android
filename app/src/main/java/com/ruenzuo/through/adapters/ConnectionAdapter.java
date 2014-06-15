package com.ruenzuo.through.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ruenzuo.through.R;
import com.ruenzuo.through.models.Connection;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class ConnectionAdapter extends ArrayAdapter<Connection> {

    public ConnectionAdapter(Context context, int resource) {
        super(context, resource);
    }

    private static class ConnectionViewHolder {
        public TextView txtViewConnectionService;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.connection_row_layout, null);
            ConnectionViewHolder holder = new ConnectionViewHolder();
            holder.txtViewConnectionService = (TextView) convertView.findViewById(R.id.txtViewConnectionService);
            convertView.setTag(holder);
        }
        ConnectionViewHolder holder = (ConnectionViewHolder)convertView.getTag();
        Connection connection = getItem(position);
        holder.txtViewConnectionService.setText(connection.getType().toString());
        return convertView;
    }

}
