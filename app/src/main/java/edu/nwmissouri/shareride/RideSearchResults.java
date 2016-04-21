package edu.nwmissouri.shareride;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideSearchResults extends AppCompatActivity {

    ListView rideSearchResultsLV;
    ArrayList<Ride> items;
    ArrayList<Ride> filteredItems = new ArrayList<>();
    RideSearchResultsAdapter rideSearchResultsAdapter;
    RideCollection rides = new RideCollection();
    RideRequestCollection rideRequest = new RideRequestCollection();
    Client kinveyClient;
    String searchRequestID;
    String searchFromAddress;
    String searchToAddress;
    String searchAvailability;
    String searchRideTime;
    String searchRideDate;
    String fromAddressLatLong = "";
    String toAddressLatLong = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_search_results);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        Bundle bundle = getIntent().getExtras();
        searchRequestID = bundle.getString("REQUEST_ID");
        searchFromAddress = bundle.getString("FROM_ADDRESS");
        searchToAddress = bundle.getString("TO_ADDRESS");
        searchAvailability = bundle.getString("AVAILABILITY");
        searchRideTime = bundle.getString("RIDETIME");
        searchRideDate = bundle.getString("RIDEDATE");

        items = RideCollection.searchItems;

        for (Ride item : items) {
            String[] shortFromRoute = item.getRouteFrom().toString().split(",");
            fromAddressLatLong = getLatLongFromGivenAddress(item.getRouteFrom().toString());
            String[] shortToRoute = item.getRouteTo().toString().split(",");
            toAddressLatLong = getLatLongFromGivenAddress(item.getRouteTo().toString());
            boolean isRideValid = isResultValid(fromAddressLatLong, toAddressLatLong, item.getNoOfAvailability().toString(), item.getTimeOfTravel().toString(), item.getFrequency().toString());
            if (isRideValid) {
                filteredItems.add(item);
            }
        }

        rideSearchResultsAdapter =
                new RideSearchResultsAdapter(this, R.layout.list_item, filteredItems, searchFromAddress, searchToAddress, searchAvailability, searchRideTime, searchRideDate);
        rideSearchResultsLV = (ListView) findViewById(R.id.rideSearchResultsLV);
        rideSearchResultsLV.setVisibility(View.VISIBLE);
        TextView noRows = (TextView) findViewById(R.id.emptyrideoffer);

        if (filteredItems.size() > 0) {
            rideSearchResultsLV.setAdapter(rideSearchResultsAdapter);
        }

        rideRequest.addRecentRide(new Ride(searchRequestID, searchFromAddress, searchToAddress, searchAvailability, searchRideTime, searchRideDate, "request", kinveyClient.user().getUsername()));
        rideSearchResultsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CLICKED", "Yes");
                Log.d("USERID", filteredItems.get(position).toString());
                String rideUserid = filteredItems.get(position).getRideUserId();
                kinveyClient.appData("RideUser", RideUser.class).getEntity(rideUserid, new KinveyClientCallback<RideUser>() {
                    @Override
                    public void onSuccess(RideUser result) {
                        Log.d("EMAIL",result.getEmail());
                        String title = null;
                        String email = null;
                        String phone = null;
                        String message = null;
                        if(result.getFullname().isEmpty()){
                            title = "Unknown User";
                        }else{
                            title = result.getFullname();
                        }
                        if(result.getEmail().isEmpty()){
                            email = "No Email";
                        }else{
                            email = result.getEmail();
                        }
                        if(result.getPhone().isEmpty()){
                            phone = "No Phone";
                        }else{
                            phone = result.getPhone();
                        }
                        message = "Email: "+email+" , "+"Phone"+phone;
                        AlertDialog.Builder userDetails = new AlertDialog.Builder(RideSearchResults.this);
                        userDetails.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                        userDetails.show();
                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });

            }
        });
    }

    private String getLatLongFromGivenAddress(String address) {
        Geocoder coder = new Geocoder(getBaseContext(), Locale.getDefault());
        boolean geoAvailable = coder.isPresent();
        String strLongitude = "";
        String strLatitude = "";
        StringBuilder latLongResult = new StringBuilder();
        int count = 0;
        try {
            List<Address> list = coder.getFromLocationName(address, 1);
            geoAvailable = coder.isPresent();
            while (count < 10 && list.size() == 0) {
                list = (ArrayList<Address>) coder.getFromLocationName(address, 1);
                count++;
            }
            if (list.size() > 0) {
                double longitude = list.get(0).getLongitude();
                double latitude = list.get(0).getLatitude();
                strLongitude = String.format("%f", longitude);
                strLatitude = String.format("%f", latitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = strLongitude + "," + strLatitude;
        latLongResult.append(result);

        return latLongResult.toString();
    }

    private String getDistanceBetween(String fromAddressLatLong, String toAddressLatLong) {
        String resultValue = "";

        String[] fromLatLongArrays = fromAddressLatLong.split(",");

        //Toast.makeText(getBaseContext(), fromAddressLatLong.toString(), Toast.LENGTH_SHORT).show();
        String[] toLatLongArrays = toAddressLatLong.split(",");

        //Toast.makeText(getBaseContext(), toAddressLatLong.toString(), Toast.LENGTH_SHORT).show();
        Double[] fromArray = new Double[fromLatLongArrays.length];
        Double[] toArray = new Double[toLatLongArrays.length];

        if (fromLatLongArrays.length <= 1 || toLatLongArrays.length <= 1) {
            Toast.makeText(getBaseContext(), "Retry! not a valid input", Toast.LENGTH_SHORT).show();
        } else {

            for (int i = 0; i < fromLatLongArrays.length; i++) {
                fromArray[i] = Double.parseDouble(fromLatLongArrays[i]);
            }

            for (int i = 0; i < toLatLongArrays.length; i++) {
                toArray[i] = Double.parseDouble(toLatLongArrays[i]);
            }

            float[] dist = new float[1];
            //double distance = getDistanceinMiles(fromArray[0],fromArray[1],toArray[0],toArray[1]);
            Location.distanceBetween(fromArray[0] / 1e6, fromArray[1] / 1e6, toArray[0] / 1e6, toArray[1] / 1e6, dist);
            resultValue = String.format("%f", dist[0] * 621.371192f);

        }

        return resultValue;
    }

    private boolean isResultValid(String fromAddressLatLong, String toAddressLatLong, String RideAvailability, String RideTime, String RideDate) {
        Date dateRideDate = new Date();
        Date SearchDateRideDate = new Date();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        String searchFromLatLong = getLatLongFromGivenAddress(searchFromAddress);
        String searchToLatLong = getLatLongFromGivenAddress(searchToAddress);

        float distanceFromPlaces = Float.parseFloat(getDistanceBetween(searchFromLatLong, fromAddressLatLong));
        float distanceToPlaces = Float.parseFloat(getDistanceBetween(searchToLatLong, toAddressLatLong));
        float floatRideAvailability = Float.parseFloat(RideAvailability);
        try {
            Date tempDate = formatter.parse(RideDate);
            Date tempSearchDate = formatter.parse(searchRideDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (distanceFromPlaces <= 1.00 && distanceToPlaces <= 1.00 && floatRideAvailability > 0 && (RideTime.equals(searchRideTime)) && ((dateRideDate.toString().substring(0,10)).equals(SearchDateRideDate.toString().substring(0,10)))) {
            return true;
        } else {
            return false;
        }
    }


}
