package io.bistroo.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SunmiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SunmiPlugin";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, action);
    }
}
