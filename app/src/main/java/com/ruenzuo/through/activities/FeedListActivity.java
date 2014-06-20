package com.ruenzuo.through.activities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.ruenzuo.through.R;
import com.ruenzuo.through.adapters.MediaAdapter;
import com.ruenzuo.through.fragments.AboutDialogFragment;
import com.ruenzuo.through.helpers.NavigationHelper;
import com.ruenzuo.through.models.Media;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class FeedListActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeLayout;
    private boolean shouldRefreshOlder = true;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ConnectListActivity.ACTION_DISCONNECT_SERVICE)) {
                MediaAdapter adapter = (MediaAdapter) getListAdapter();
                adapter.clear();
                ParseQuery<Media> query = Media.getQuery();
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.orderByDescending("mediaDate");
                query.setLimit(50);
                query.findInBackground(new FindCallback<Media>() {

                    @Override
                    public void done(List<Media> feed, ParseException e) {
                        if (e != null) {
                            Toast.makeText(FeedListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                        } else {
                            MediaAdapter adapter = (MediaAdapter) getListAdapter();
                            adapter.addAll(feed);
                            adapter.notifyDataSetChanged();
                        }
                    }

                });
            } else if (intent.getAction().equalsIgnoreCase(ConnectListActivity.ACTION_CONNECT_SERVICE)) {
                MediaAdapter adapter = (MediaAdapter) getListAdapter();
                adapter.clear();
                generateFeed();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectListActivity.ACTION_CONNECT_SERVICE);
        intentFilter.addAction(ConnectListActivity.ACTION_DISCONNECT_SERVICE);
        registerReceiver(receiver, intentFilter);
        setContentView(R.layout.feed_list_activity_layout);
        checkForCrashes();
        if (!getResources().getBoolean(R.bool.google_play_build)) {
            checkForUpdates();
        }
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(R.color.dark_through_color,
                R.color.light_through_color,
                R.color.dark_through_color,
                R.color.light_through_color);
        MediaAdapter adapter = new MediaAdapter(this, R.layout.media_row_layout);
        setListAdapter(adapter);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("ShouldGenerateFeed")) {
                generateFeed();
            }
        } else {
            ParseQuery<Media> query = Media.getQuery();
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.orderByDescending("mediaDate");
            query.setLimit(50);
            query.findInBackground(new FindCallback<Media>() {

                @Override
                public void done(List<Media> feed, ParseException e) {
                    if (e != null) {
                        Toast.makeText(FeedListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
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
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount && totalItemCount != 0) {
                    if (shouldRefreshOlder) {
                        refreshOlder();
                    }
                }
            }

        });
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MediaAdapter adapter = (MediaAdapter) getListAdapter();
        Media media = adapter.getItem(position);
        Intent intent = new Intent(this, MediaActivity.class);
        intent.putExtra("MediaURL", media.getURL());
        startActivity(intent);
    }

    private void generateFeed() {
       final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting feed");
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", ParseUser.getCurrentUser().getUsername());
        ParseCloud.callFunctionInBackground("generateFeedsForUser", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(FeedListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<Media> feed = (ArrayList<Media>) o;
                    MediaAdapter adapter = (MediaAdapter) getListAdapter();
                    adapter.addAll(feed);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void refreshOlder() {
        shouldRefreshOlder = false;
        ParseQuery<Media> query = Media.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        MediaAdapter adapter = (MediaAdapter) getListAdapter();
        Media oldestMedia = adapter.getItem(adapter.getCount() - 1);
        query.whereLessThan("mediaDate", oldestMedia.getMediaDate());
        query.orderByDescending("mediaDate");
        query.setLimit(50);
        query.findInBackground(new FindCallback<Media>() {

            @Override
            public void done(List<Media> feed, ParseException e) {
                if (e != null) {
                    Toast.makeText(FeedListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                } else if (feed.size() == 0) {
                    Toast.makeText(FeedListActivity.this, "Can't find more items.", Toast.LENGTH_SHORT).show();
                    shouldRefreshOlder = false;
                } else {
                    MediaAdapter adapter = (MediaAdapter) getListAdapter();
                    adapter.addAll(feed);
                    adapter.notifyDataSetChanged();
                    shouldRefreshOlder = true;
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_services) {
            Intent intent =  new Intent(this, ConnectListActivity.class);
            intent.putExtra("ShouldAllowDisconnect", true);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_log_out) {
            ParseUser.logOut();
            Intent intent =  new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            DialogFragment dialog = new AboutDialogFragment();
            dialog.show(getFragmentManager(), "InfoDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.action_tos) {
            NavigationHelper.openInBroswer(this, "https://dl.dropboxusercontent.com/u/12352209/Through/ThroughToS.html");
            return true;
        } else if (item.getItemId() == R.id.action_privacy) {
            NavigationHelper.openInBroswer(this, "https://www.iubenda.com/privacy-policy/895941");
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onRefresh() {
        ParseQuery<Media> query = Media.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        MediaAdapter adapter = (MediaAdapter) getListAdapter();
        if (adapter.getCount() != 0) {
            Media lastMedia = adapter.getItem(0);
            query.whereGreaterThan("mediaDate", lastMedia.getMediaDate());
        }
        query.orderByDescending("mediaDate");
        query.setLimit(50);
        query.findInBackground(new FindCallback<Media>() {

            @Override
            public void done(List<Media> feed, ParseException e) {
                if (e != null) {
                    Toast.makeText(FeedListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                    swipeLayout.setRefreshing(false);
                } else if (feed.size() == 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", ParseUser.getCurrentUser().getUsername());
                    ParseCloud.callFunctionInBackground("generateFeedsForUser", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, ParseException e) {
                            swipeLayout.setRefreshing(false);
                            if (e != null) {
                                Toast.makeText(FeedListActivity.this, "There was an error with this request, please try again later.", Toast.LENGTH_LONG).show();
                            } else {
                                ArrayList<Media> feed = (ArrayList<Media>) o;
                                if (feed.size() != 0) {
                                    MediaAdapter adapter = (MediaAdapter) getListAdapter();
                                    for (int i = 0; i < feed.size(); i++) {
                                        Media media = feed.get(i);
                                        adapter.insert(media, i);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                } else {
                    swipeLayout.setRefreshing(false);
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

    private void checkForCrashes() {
        CrashManager.register(this, "7b0393573930d8bcbabcaa7d6e7b005b");
    }

    private void checkForUpdates() {
        UpdateManager.register(this, "7b0393573930d8bcbabcaa7d6e7b005b");
    }

}
