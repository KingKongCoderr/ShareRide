package edu.nwmissouri.shareride;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.ArrayList;

public class RiderRequestDetailActivity extends AppCompatActivity {

    RideRequestCollection rideCollection = new RideRequestCollection();
    TextView fromAddressET;
    TextView toAddressET;
    TextView availabilityET;
    TextView hrsSpinner;
    TextView frequencySpinner;
    TextView offerId;
    Client kinveyClient;
    Ride rideObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request_detail);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        fromAddressET = (TextView) findViewById(R.id.fromET);
        toAddressET = (TextView) findViewById(R.id.ToET);
        availabilityET = (TextView) findViewById(R.id.offerAvailabilityET);
        hrsSpinner = (TextView) findViewById(R.id.offertimeSpinner);
        frequencySpinner = (TextView) findViewById(R.id.offerFrequencySpinner);
        offerId = (TextView) findViewById(R.id.offerIDTV);
        Bundle bundle = getIntent().getExtras();
        rideObject = rideCollection.getRideObject(bundle.getString("INDEX_LOCATION"));
        Log.d("SEARCH ITEM", String.valueOf(rideCollection.getRideObject(bundle.getString("INDEX_LOCATION").toString())));
        if (rideObject != null) {
            fromAddressET.setText(rideObject.getRouteFrom());
            toAddressET.setText(rideObject.getRouteTo());
            availabilityET.setText(rideObject.getNoOfAvailability());
            hrsSpinner.setText(rideObject.getTimeOfTravel());
            frequencySpinner.setText(rideObject.getFrequency());
            offerId.setText(bundle.getString("INDEX_LOCATION").toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ride_request_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent profile_intent = new Intent(getApplicationContext(), Profile_settings.class);
            startActivity(profile_intent);
            return true;
        } else if (id == R.id.Edit_Ride_Request) {
            Intent RideEditIntent = new Intent(this, RideRequestEditActivity.class);
            RideEditIntent.putExtra("REQUEST_ID", offerId.getText());
            startActivity(RideEditIntent);
            return true;
        } else if (id == R.id.logout) {
            kinveyClient.user().logout().execute();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
        } else if (id == R.id.stats) {
            Intent stats_intent = new Intent(getApplicationContext(), StatisticsActivity.class);
            startActivity(stats_intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSearchClick(View view) {
        final Intent searchIntent = new Intent(this, RideSearchResults.class);
        searchIntent.putExtra("REQUEST_ID", offerId.getText());
        searchIntent.putExtra("FROM_ADDRESS", fromAddressET.getText());
        searchIntent.putExtra("TO_ADDRESS", toAddressET.getText());
        searchIntent.putExtra("AVAILABILITY", availabilityET.getText());
        searchIntent.putExtra("RIDETIME", hrsSpinner.getText());
        searchIntent.putExtra("RIDEDATE", frequencySpinner.getText());
        RideRequestCollection.recentRide = rideObject;
        RideUser.currentUser.setRideRecent(rideObject);
        kinveyClient.appData("RideUser", RideUser.class).save(RideUser.currentUser, new KinveyClientCallback<RideUser>() {
            @Override
            public void onSuccess(RideUser rideUser) {
                RideUser.currentUser = rideUser;
                RideRequestCollection.recentRide = rideUser.getRideRecent();
                Log.d("SEARCH CLICK", " User SUCSESS");
                startActivity(searchIntent);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        startActivity(searchIntent);
        kinveyClient.appData("RideCollection", Ride.class).

                get(new KinveyListCallback<Ride>() {
                        @Override
                        public void onSuccess(Ride[] result) {
                            Log.d("Length of the data", String.valueOf(result.length));
                            RideCollection.searchItems.clear();
                            for (Ride ride : result) {
                                Log.d("SEARCH BEFORE", ride.toString());
                                Log.d("SEARCH TYPE", ride.getRideType());
                                if (ride.getRideType().equals("offer")) {
                                    Log.d("SEARCH AFTER", ride.toString());
                                    if (!ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                        Log.d("SEARCH RIDE", ride.toString());
                                        RideCollection.searchItems.add(ride);
                                    }
                                }
                            }
                            Log.d("SEARCH ITEMS", RideCollection.searchItems.toString());
                            startActivity(searchIntent);
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e("ALL DATA", "AppData.get all Failure", error);
                        }
                    }

                );
    }
}

