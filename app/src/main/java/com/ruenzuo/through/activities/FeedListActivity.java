package com.ruenzuo.through.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.ruenzuo.through.R;
import com.ruenzuo.through.adapters.MediaAdapter;
import com.ruenzuo.through.models.Media;

import java.util.List;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class FeedListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_list_activity_layout);
        MediaAdapter adapter = new MediaAdapter(this, R.layout.media_row_layout);
        setListAdapter(adapter);
        ParseQuery<Media> query = Media.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByDescending("mediaDate");
        query.setLimit(50);
        query.findInBackground(new FindCallback<Media>() {

            @Override
            public void done(List<Media> feed, ParseException e) {
                if (e != null) {
                    //TODO: Handle error.
                } else {
                    MediaAdapter adapter = (MediaAdapter) getListAdapter();
                    for (int i = 0; i < feed.size(); i++) {
                        Media media = feed.get(i);
                        adapter.insert(media, i);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        return true;
    }

}
