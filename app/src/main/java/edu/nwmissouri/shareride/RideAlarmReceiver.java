package edu.nwmissouri.shareride;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RideAlarmReceiver extends BroadcastReceiver {

    public static String ACTION_ALARM = "com.alarammanager.alaram";

    public RideAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm Receiver", "Entered");

        Bundle bundle = intent.getExtras();
        String action = bundle.getString(ACTION_ALARM);
        if (action.equals(ACTION_ALARM)) {
            Log.i("Alarm Receiver", "If loop");
            Intent inService = new Intent(context, UpdatingService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, inService, PendingIntent.FLAG_NO_CREATE);

            if (pendingIntent == null) {
                context.startService(inService);
            }
        } else {
            Log.i("Alarm Receiver", "Else loop");
        }
    }
}
