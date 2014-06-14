package com.ruenzuo.through.activities;

import android.app.ListActivity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;

import com.ruenzuo.through.R;
import com.ruenzuo.through.adapters.MediaAdapter;
import com.ruenzuo.through.extensions.ObservableListView;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class FeedListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_list_activity_layout);
        String[] media = getResources().getStringArray(R.array.pictures);
        MediaAdapter adapter = new MediaAdapter(this, R.layout.media_row_layout);
        adapter.addAll(media);
        setListAdapter(adapter);
        final ObservableListView listView = (ObservableListView) getListView();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for (int i = firstVisibleItem; i < visibleItemCount; i++) {
                    View row = listView.getChildAt(i);
                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                    float velocity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                    float offset = ((listView.getScroll() - listView.getViewHeigth(row)) / px ) * velocity;
                    MediaAdapter.MediaViewHolder holder = (MediaAdapter.MediaViewHolder)row.getTag();
                    Matrix matrix = holder.imgViewPicture.getImageMatrix();
                    matrix.setTranslate(0, offset);
                    holder.imgViewPicture.setImageMatrix(matrix);
                    holder.imgViewPicture.invalidate();
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
