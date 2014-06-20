package com.ruenzuo.through.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;

import com.ruenzuo.through.R;
import com.ruenzuo.through.extensions.BaseActivity;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by ruenzuo on 19/04/14.
 */
public class LicensesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.licenses_activity_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("licenses/licenses.txt");
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(inputStream, stringWriter);
            String licenses = stringWriter.toString();
            TextView txtViewLicenses = (TextView) findViewById(R.id.txtViewLicenses);
            txtViewLicenses.setText(licenses);
            txtViewLicenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishAnimated();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
