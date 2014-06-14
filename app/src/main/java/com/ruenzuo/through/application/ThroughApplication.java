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
        Parse.initialize(this, "13M9oMhAklF3jK9XjjVZ8cHa4qOwQZqYFM6CiXLL", "o5A6XY0VXa7I1RggWh0kTKnZ31zHLzHLeTd2odJV");
        ParseObject.registerSubclass(Media.class);
    }

}