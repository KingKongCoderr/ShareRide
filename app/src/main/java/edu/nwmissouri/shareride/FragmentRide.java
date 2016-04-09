package edu.nwmissouri.shareride;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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
public class FragmentRide extends Fragment {

    Activity activity; //**
    ArrayList<Ride> updatedItems;
    ListView rideOfferLV;
    iRideActivity rideActivityInterface;
    Button addRideOffer;

    Client kinveyClient;
    private String appKey = "kid_ZJCDL-Jpy-";
    private String appSecret = "7ba9e5e0015849b790845e669ab87992";

    public static final String ITEM_POSITION = "position";

    ArrayList<Ride> items;
    RideOfferAdapter RideOfferAdapter;
    Ride[] itemsArray;
    RideCollection rides = new RideCollection();
    //ResultsListAdapter resultsListAdapter;

    public interface iRideActivity
    {
        public void onSelectingNewRide();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static FragmentRide newInstance(int num) {
        FragmentRide f = new FragmentRide();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("from", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ride, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.Add_Ride_Offer)
        {
            Toast.makeText(getActivity(), "Navigating to Ride Offer Screen!", Toast.LENGTH_SHORT).show();
            Intent RideIntent = new Intent(getContext(), NewRideOfferActivity.class);
            startActivity(RideIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
        rideActivityInterface = (iRideActivity) activity;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_ride, container, false);

        items = rides.getRideCollection();
        //itemsArray = items.toArray(new Ride[items.size()]);
        kinveyClient = new Client.Builder(appKey, appSecret
                , getContext()).build();
        RideOfferAdapter =
                    new RideOfferAdapter(getActivity(), R.layout.list_item, items);
        rideOfferLV = (ListView) theView.findViewById(R.id.riderOfferLV);
        rideOfferLV.setVisibility(View.VISIBLE);
        TextView noRows = (TextView) theView.findViewById(R.id.emptyrideoffer);

        if(items.size() != 0) {
            rideOfferLV.setVisibility(View.VISIBLE);

            rideOfferLV.setAdapter(RideOfferAdapter);
        }



        rideOfferLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newRideOfferIntent = new Intent(getActivity(), RiderOfferDetailActivity.class);
                String currentOfferId = ((TextView) view.findViewById(R.id.itemId)).getText().toString();
                String selected = currentOfferId.replace("OfferID: ","");
                newRideOfferIntent.putExtra("INDEX_LOCATION", selected);
                Log.d("Selected Offer Id", selected.toString());
                startActivity(newRideOfferIntent);
            }
        });

        rideOfferLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(getActivity());
                deleteAlert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("POSITION",String.valueOf(position));
                        Log.d("WHICH",String.valueOf(which));
                        Ride ride = RideCollection.items.get(position);
                        Log.d("RIDEOBJECT",ride.toString());
                        kinveyClient.appData("RideCollection", Ride.class).delete(ride.getRideID(), new KinveyDeleteCallback() {
                            @Override
                            public void onSuccess(KinveyDeleteResponse result) {
                                Toast.makeText(getContext(), "Number of Entities Deleted: " + result.getCount(), Toast.LENGTH_LONG).show();
                                final RideCollection rideCollection = new RideCollection();
                                kinveyClient.appData("RideCollection", Ride.class).get(new KinveyListCallback<Ride>() {
                                    @Override
                                    public void onSuccess(Ride[] result) {
                                        Log.d("Length of the data", String.valueOf(result.length));
                                        RideCollection.items.clear();
                                        for (Ride ride : result) {
                                            if (ride.getRideType().equals("offer") && ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                                //RideCollection.items.add(ride);
                                                rideCollection.addRideCollection(ride);
                                            }
                                        }
                                        if(RideCollection.items.size()>0){
                                            Ride.rideOfferCount = Integer.parseInt(RideCollection.items.get(RideCollection.items.size()-1).getOfferID());
                                        }else{
                                            Ride.rideOfferCount = 0;
                                        }
                                        Log.d("OFFER LIST", RideCollection.items.toString());
                                        final Intent rideActivityIntent = new Intent(getContext(), RideActivity.class);
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
                                Toast.makeText(getContext(), "Delete error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        RideOfferAdapter.notifyDataSetChanged();
                    }
                });
                deleteAlert.show();
                return true;
            }
        });

        return theView;
    }
}
