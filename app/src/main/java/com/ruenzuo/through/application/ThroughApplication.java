package com.ruenzuo.through.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.ruenzuo.through.models.Media;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class ThroughApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "9ITnzSGyHOgg76x2arpk0vz4kcpPRVlYG8NgNNqY", "mIffCyXqAQ6R7SZ3GwoQNCqMpnrm0PvLy79BKPWL");
        ParseObject.registerSubclass(Media.class);
    }

}