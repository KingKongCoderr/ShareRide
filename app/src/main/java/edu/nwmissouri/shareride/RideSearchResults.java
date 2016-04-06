package edu.nwmissouri.shareride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kinvey.android.Client;

import java.util.ArrayList;

public class RideSearchResults extends AppCompatActivity {

    ListView rideSearchResultsLV;
    ArrayList<Ride> items;
    RideSearchResultsAdapter rideSearchResultsAdapter;
    RideCollection rides = new RideCollection();
    RideRequestCollection rideRequest = new RideRequestCollection();
    Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_search_results);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        Bundle bundle = getIntent().getExtras();
        String searchRequestID = bundle.getString("REQUEST_ID");
        String searchFromAddress = bundle.getString("FROM_ADDRESS");
        String searchToAddress = bundle.getString("TO_ADDRESS");
        String searchAvailability = bundle.getString("AVAILABILITY");
        String searchRideTime = bundle.getString("RIDETIME");
        String searchRideDate = bundle.getString("RIDEDATE");

        items = rides.getRideCollection();
        rideRequest.addRecentRide(new Ride(searchRequestID,searchFromAddress,searchToAddress,searchAvailability,searchRideTime,searchRideDate,"request",kinveyClient.user().getUsername()));
        rideSearchResultsAdapter =
                new RideSearchResultsAdapter(this, R.layout.list_item, items,searchFromAddress,searchToAddress,searchAvailability,searchRideTime,searchRideDate);
        rideSearchResultsLV = (ListView) findViewById(R.id.rideSearchResultsLV);
        rideSearchResultsLV.setVisibility(View.VISIBLE);
        TextView noRows = (TextView) findViewById(R.id.emptyrideoffer);

        if(items.size() != 0) {
            rideSearchResultsLV.setVisibility(View.VISIBLE);

            rideSearchResultsLV.setAdapter(rideSearchResultsAdapter);
        }
    }


}
