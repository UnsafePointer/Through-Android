package com.ruenzuo.through.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruenzuo.through.R;
import com.ruenzuo.through.models.Media;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class MediaAdapter extends ArrayAdapter<Media> {

    public MediaAdapter(Context context, int resource) {
        super(context, resource);
    }

    public static class MediaViewHolder {
        public ImageView imgViewPicture;
        public TextView txtViewMediaSource;
        public TextView txtViewMediaText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.media_row_layout, null);
            MediaViewHolder holder = new MediaViewHolder();
            holder.imgViewPicture = (ImageView) convertView.findViewById(R.id.imgViewPicture);
            holder.txtViewMediaSource = (TextView) convertView.findViewById(R.id.txtViewMediaSource);
            holder.txtViewMediaText = (TextView) convertView.findViewById(R.id.txtViewMediaText);
            convertView.setTag(holder);
        }
        MediaViewHolder holder = (MediaViewHolder)convertView.getTag();
        Media media = getItem(position);
        PrettyTime prettyTime = new PrettyTime();
        String ago = prettyTime.format(media.getMediaDate());
        ago = ago.replace("from now", "ago");
        if (media.getText() != null) {
            holder.txtViewMediaSource.setText(media.getUserName() + " on " + media.getType().toString() + " (" + ago + "):");
            holder.txtViewMediaText.setText(media.getText());
        } else {
            holder.txtViewMediaSource.setText(media.getUserName() + " on " + media.getType().toString() + " (" + ago + ")");
            holder.txtViewMediaText.setText("");
        }
        Picasso.with(getContext()).load(media.getURL()).placeholder(R.drawable.placeholder).fit().centerCrop().into(holder.imgViewPicture);
        return convertView;
    }

}
