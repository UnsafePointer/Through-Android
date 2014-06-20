package com.ruenzuo.through.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ruenzuo.through.R;
import com.ruenzuo.through.extensions.BaseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by renzocrisostomo on 20/06/14.
 */
public class MediaActivity extends BaseActivity {

    private ProgressBar pgrBarMedia;
    private ImageView imgViewMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_activity_layout);
        String url = getIntent().getExtras().getString("MediaURL");
        imgViewMedia = (ImageView) findViewById(R.id.imgViewMedia);
        pgrBarMedia = (ProgressBar) findViewById(R.id.pgrBarMedia);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.with(this).load(url).into(imgViewMedia, new Callback() {

            @Override
            public void onSuccess() {
                pgrBarMedia.setVisibility(View.GONE);
                new PhotoViewAttacher(imgViewMedia);
            }

            @Override
            public void onError() {
                pgrBarMedia.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAnimated();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
