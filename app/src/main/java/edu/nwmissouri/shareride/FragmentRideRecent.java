package edu.nwmissouri.shareride;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by S525339 on 3/2/2016.
 */
public class FragmentRideRecent extends Fragment {

    Activity activity; //**
    ArrayList<Ride> updatedItems;
    ArrayList<Ride> filteredItems = new ArrayList<>();
    ListView rideRecentLV;
    iRideRequestActivity rideActivityInterface;
    Button addRideOffer;
    public static final String ITEM_POSITION = "position";
    ArrayList<Ride> items;
    RideSearchResultsAdapter rideSearchResultsAdapter;
    Ride[] itemsArray;
    RideCollection rides = new RideCollection();
    RideRequestCollection rideRequest = new RideRequestCollection();
    //ResultsListAdapter resultsListAdapter;
    Ride recentRide;
    String searchFromAddress = "";
    String searchToAddress = "";
    String searchAvailability;
    String searchRideTime;
    String searchRideDate;
    String fromAddressLatLong = "";
    String toAddressLatLong = "";

    Client kinveyClient;
    private String appKey = "kid_ZJCDL-Jpy-";
    private String appSecret = "7ba9e5e0015849b790845e669ab87992";

    public interface iRideRequestActivity {
        public void onSelectingNewRideRequest();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kinveyClient = new Client.Builder(appKey, appSecret
                , this.getContext()).build();
        setHasOptionsMenu(true);
    }

    public static FragmentRideRecent newInstance(int num) {
        FragmentRideRecent f = new FragmentRideRecent();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("from", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent profile_intent=new Intent(getContext(),Profile_settings.class);
            startActivity(profile_intent);
            return true;
        } else if (id == R.id.logout) {
            kinveyClient.user().logout().execute();
            Intent loginActivity = new Intent(getContext(), LoginActivity.class);
            startActivity(loginActivity);

        } else if(id == R.id.stats){
            Intent stats_intent=new Intent(getContext(),Statistics.class);
            startActivity(stats_intent);
        }
//        else if (id == R.id.Add_Ride_Request)
//        {
//            Toast.makeText(getActivity(), "Navigating to Ride Request Screen!", Toast.LENGTH_SHORT).show();
//            Intent RideRequestIntent = new Intent(getContext(), NewRideRequestActivity.class);
//            startActivity(RideRequestIntent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        rideActivityInterface = (iRideRequestActivity) activity;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_recentride, container, false);
        TextView noRows = (TextView) theView.findViewById(R.id.emptyriderequest);
        kinveyClient = new Client.Builder(appKey, appSecret
                , this.getContext()).build();
        //itemsArray = items.toArray(new Ride[items.size()]);

        /* BackEnd Service code */

        recentRide = rideRequest.getRecentRide();
        rideRecentLV = (ListView) theView.findViewById(R.id.riderecentlv);
        if(null != recentRide ) {
            Log.d("RECENT RIDE FRAGMENT",recentRide.toString());
        }else{
            Log.d("RECENT RIDE FRAGMENT","Nothing in recent ride");
        }
        if (recentRide != null) {

            items = RideCollection.searchItems;

            AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(getContext(), RideAlarmReceiver.class);
            myIntent.putExtra(RideAlarmReceiver.ACTION_ALARM, RideAlarmReceiver.ACTION_ALARM);
            //Toast.makeText(getContext(), "Scheduler called successfully", Toast.LENGTH_SHORT).show();
            final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), 1234567, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000000, pIntent);

            searchFromAddress = recentRide.getRouteFrom();
            searchToAddress = recentRide.getRouteTo();
            searchAvailability = recentRide.getNoOfAvailability();
            searchRideTime = recentRide.getTimeOfTravel();
            searchRideDate = recentRide.getFrequency();

            //items.clear();
            items = RideCollection.searchItems;
            filteredItems.clear();

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
                    new RideSearchResultsAdapter(getContext(), R.layout.list_item, filteredItems, searchFromAddress, searchToAddress, searchAvailability, searchRideTime, searchRideDate);

            if (filteredItems.size() > 0) {
                rideRecentLV.setAdapter(rideSearchResultsAdapter);
            }
//list on click listener

            rideRecentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("CLICKED", "Yes");
//                    Log.d("USERID", filteredItems.get(position).toString());
                    String rideUserid = filteredItems.get(position).getRideUserId();
                    System.setProperty("http.keepAlive", "false");
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
                            AlertDialog.Builder userDetails = new AlertDialog.Builder(getContext());
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





//                if (items.size() != 0) {
//                    //noRows.setVisibility(View.INVISIBLE);
//                    rideSearchResultsAdapter = new RideSearchResultsAdapter(getActivity(), R.layout.list_item, items, searchFromAddress, searchToAddress, searchAvailability, searchRideTime, searchRideDate);
//                    rideRecentLV = (ListView) theView.findViewById(R.id.riderecentlv);
//                    rideRecentLV.setAdapter(rideSearchResultsAdapter);
//                    //AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//                    //alarms.cancel(pIntent);
//                }
        }

        return theView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Intent intent = new Intent(getContext(), RideAlarmReceiver.class);
        intent.putExtra(RideAlarmReceiver.ACTION_ALARM, RideAlarmReceiver.ACTION_ALARM);

        final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), 1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarms.cancel(pIntent);
        Toast.makeText(getContext(), "Canceled...", Toast.LENGTH_SHORT);
    }

    private String getLatLongFromGivenAddress(String address) {
        Geocoder coder = new Geocoder(getContext(), Locale.getDefault());
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


        String[] toLatLongArrays = toAddressLatLong.split(",");


        Double[] fromArray = new Double[fromLatLongArrays.length];
        Double[] toArray = new Double[toLatLongArrays.length];

        if (fromLatLongArrays.length <= 1 || toLatLongArrays.length <= 1) {
            Toast.makeText(getContext(), "Retry! not a valid input", Toast.LENGTH_SHORT).show();
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

        if (distanceFromPlaces <= 1.00 && distanceToPlaces <= 1.00 && floatRideAvailability > 0 && (RideTime.equals(searchRideTime)) && (dateRideDate.equals(SearchDateRideDate))) {
            return true;
        } else {
            return false;
        }
    }

}
