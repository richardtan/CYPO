package com.tckr.dukcud;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class KeepAliveActivity extends Activity {

    private static final String TAG = "KeepAliveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_alive);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "Finishing the KeepAliveActivity :)");
        finish();
    }

}
