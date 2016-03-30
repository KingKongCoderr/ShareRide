package edu.nwmissouri.shareride;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by S525339 on 3/2/2016.
 */
public class FragmentRideRecent extends Fragment {

    Activity activity; //**
    ArrayList<Ride> updatedItems;
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

    public interface iRideRequestActivity
    {
        public void onSelectingNewRideRequest();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        inflater.inflate(R.menu.menu_ride_request, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
    public void onAttach(Activity activity)
    {
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
        //itemsArray = items.toArray(new Ride[items.size()]);


        /* BackEnd Service code */
        Toast.makeText(getContext(),"Scheduler called successfully",Toast.LENGTH_SHORT).show();

        /* BackEnd Service code */

        recentRide = rideRequest.getRecentRide();
        items = rides.getRideCollection();

        AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getContext(), RideAlarmReceiver.class);
        myIntent.putExtra(RideAlarmReceiver.ACTION_ALARM, RideAlarmReceiver.ACTION_ALARM);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(),1234567, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 100000, pIntent);
        Toast.makeText(getContext(),"Scheduler running successfully",Toast.LENGTH_SHORT).show();

        if(recentRide != null)
        {
            String searchFromAddress = recentRide.getRouteFrom();
            String searchToAddress = recentRide.getRouteTo();
            String searchAvailability = recentRide.getNoOfAvailability();
            String searchRideTime = recentRide.getTimeOfTravel();
            String searchRideDate = recentRide.getFrequency();

            if (items.size() != 0)
            {
                //noRows.setVisibility(View.INVISIBLE);
                rideSearchResultsAdapter = new RideSearchResultsAdapter(getActivity(), R.layout.list_item, items, searchFromAddress, searchToAddress, searchAvailability, searchRideTime, searchRideDate);
                rideRecentLV = (ListView) theView.findViewById(R.id.riderecentlv);
                rideRecentLV.setAdapter(rideSearchResultsAdapter);
                //AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                //alarms.cancel(pIntent);
            }
        }

        return theView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Intent intent = new Intent(getContext(),RideAlarmReceiver.class);
        intent.putExtra(RideAlarmReceiver.ACTION_ALARM,RideAlarmReceiver.ACTION_ALARM);

        final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), 1234567,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarms.cancel(pIntent);
        Toast.makeText(getContext(), "Canceled...", Toast.LENGTH_SHORT);
    }
}
