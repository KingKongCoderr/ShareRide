package edu.nwmissouri.shareride;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdatingService extends IntentService {

    RideCollection ride = new RideCollection();

    public UpdatingService() {
        super("UpdatingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ride.refreshRecentSearchData();
        Log.i("updateService", "Service running");

    }
}
