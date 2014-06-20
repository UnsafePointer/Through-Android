package com.ruenzuo.through.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by renzocrisostomo on 20/06/14.
 */
public class NavigationHelper {

    public static void openInBroswer(Context context, String URL) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        context.startActivity(intent);
    }

}
