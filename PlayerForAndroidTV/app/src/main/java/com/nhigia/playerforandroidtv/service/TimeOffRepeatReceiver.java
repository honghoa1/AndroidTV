package com.nhigia.playerforandroidtv.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.nhigia.playerforandroidtv.view.PlayerListVideoActivity;

public class TimeOffRepeatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            context.startActivity(new Intent(context, PlayerListVideoActivity.class));
        }
    }
}
