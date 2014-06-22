package com.ruenzuo.through.application;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseObject;
import com.ruenzuo.through.R;
import com.ruenzuo.through.models.Media;

import java.util.HashMap;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public class ThroughApplication extends Application {

    public enum TrackerName {
        APP_TRACKER
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker tracker = analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, tracker);
        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "KQiyMjQMTLLYBN2wzEdJZwjNPgU5isrwmVQZtzZf", "FrWaVWOcSUP1ge9E8KWmRq2KRM3v94tNjWm8Rm4j");
        ParseObject.registerSubclass(Media.class);
    }

}