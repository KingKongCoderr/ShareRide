package edu.nwmissouri.shareride;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.model.KinveyDeleteResponse;

import java.util.ArrayList;

/**
 * Created by S525339 on 3/2/2016.
 */
public class FragmentRideRequest extends Fragment {

    Activity activity; //**
    ListView rideRequestLV;
    iRideRequestActivity rideActivityInterface;
    ArrayList<Ride> items;
    RideRequestAdapter rideRequestAdapter;
    RideRequestCollection rides = new RideRequestCollection();
    Client kinveyClient;
    private String appKey = "kid_ZJCDL-Jpy-";
    private String appSecret = "7ba9e5e0015849b790845e669ab87992";

    public interface iRideRequestActivity
    {
        public void onSelectingNewRideRequest();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kinveyClient = new Client.Builder(appKey, appSecret
                , this.getContext()).build();
        setHasOptionsMenu(true);
    }

    public static FragmentRideRequest newInstance(int num) {
        FragmentRideRequest f = new FragmentRideRequest();
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

        if (id == R.id.action_settings) {
            Intent profile_intent=new Intent(getContext(),Profile_settings.class);
            startActivity(profile_intent);
            return true;
        }
        else if (id == R.id.Add_Ride_Request)
        {
            Intent RideRequestIntent = new Intent(getContext(), NewRideRequestActivity.class);
            startActivity(RideRequestIntent);
            return true;
        }else if (id == R.id.logout) {
            kinveyClient.user().logout().execute();
            Intent loginActivity = new Intent(getContext(), LoginActivity.class);
            startActivity(loginActivity);

        } else if(id == R.id.stats){
            Intent stats_intent=new Intent(getContext(),StatisticsActivity.class);
            startActivity(stats_intent);
        }

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
        View theView = inflater.inflate(R.layout.fragment_riderequest, container, false);

        items = rides.getRideCollection();
        kinveyClient = new Client.Builder(appKey, appSecret
                , getContext()).build();

        rideRequestAdapter = new RideRequestAdapter(getActivity(), R.layout.list_item, items);
            rideRequestLV = (ListView) theView.findViewById(R.id.riderRequestLV);

        TextView noRowsTV = (TextView) theView.findViewById(R.id.emptyriderequest);

        if(items != null)
        {
           noRowsTV.setVisibility(View.INVISIBLE);
           rideRequestLV.setAdapter(rideRequestAdapter);
        }
        else
        {
            rideRequestLV.setVisibility(View.INVISIBLE);
            noRowsTV.setVisibility(View.VISIBLE);
            noRowsTV.setText("No Ride Requests Found!");
        }

        rideRequestLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newRideOfferIntent = new Intent(getActivity(), RiderRequestDetailActivity.class);
                String currentOfferId = ((TextView) view.findViewById(R.id.itemId)).getText().toString();
                String selected = currentOfferId.replace("RequestID: ","");
                newRideOfferIntent.putExtra("INDEX_LOCATION", selected);
                startActivity(newRideOfferIntent);
            }
        });

        rideRequestLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(getActivity());
                deleteAlert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d("POSITION", String.valueOf(position));
                        Log.d("WHICH",String.valueOf(which));
                        Ride ride = RideRequestCollection.items.get(position);
                        Log.d("RIDEOBJECT",ride.toString());
                        kinveyClient.appData("RideCollection", Ride.class).delete(ride.getRideID(), new KinveyDeleteCallback() {
                            @Override
                            public void onSuccess(KinveyDeleteResponse result) {
                                final RideRequestCollection rideCollection = new RideRequestCollection();
                                kinveyClient.appData("RideCollection", Ride.class).get(new KinveyListCallback<Ride>() {
                                    @Override
                                    public void onSuccess(Ride[] result) {
                                        Log.d("Length of the data", String.valueOf(result.length));
                                        RideRequestCollection.items.clear();
                                        for (Ride ride : result) {
                                            if (ride.getRideType().equals("request") && ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                                rideCollection.addRideCollection(ride);
                                            }
                                        }
                                        if(RideRequestCollection.items.size()>0){
                                            Ride.rideRequestCount = Integer.parseInt(RideRequestCollection.items.get(RideRequestCollection.items.size()-1).getOfferID());
                                        }else{
                                            Ride.rideRequestCount = 0;
                                        }
                                        final Intent rideActivityIntent = new Intent(getContext(), RideActivity.class);
                                        RideRequestCollection.tabPosition = 1;
                                        startActivity(rideActivityIntent);
                                    }

                                    @Override
                                    public void onFailure(Throwable error) {
                                        Log.e("ALL DATA", "AppData.get all Failure", error);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                Log.e("TAG", "AppData.delete Failure", error);
                            }
                        });
                        rideRequestAdapter.notifyDataSetChanged();
                    }
                });
                deleteAlert.show();
                return true;
            }
        });

        return theView;
    }

}
