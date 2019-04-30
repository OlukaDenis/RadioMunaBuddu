package com.premar.radiomunabuddu;

import android.content.Context;
import android.content.Intent;

public class IntentReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Intent myIntent = new Intent(context, RadioMediaPlayerService.class);
            context.stopService(myIntent);
        }
    }
}
