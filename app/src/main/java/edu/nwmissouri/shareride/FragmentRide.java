package edu.nwmissouri.shareride;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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

        RideOfferAdapter =
                    new RideOfferAdapter(getActivity(), R.layout.list_item, items);
        rideOfferLV = (ListView) theView.findViewById(R.id.riderOfferLV);
        rideOfferLV.setVisibility(View.VISIBLE);
        TextView noRows = (TextView) theView.findViewById(R.id.emptyrideoffer);

        if(items != null) {
            rideOfferLV.setVisibility(View.VISIBLE);
            noRows.setVisibility(View.INVISIBLE);

            rideOfferLV.setAdapter(RideOfferAdapter);
        }
        else{
            rideOfferLV.setVisibility(View.INVISIBLE);
            noRows.setVisibility(View.VISIBLE);
            noRows.setText("No Ride Offers Found!");
        }


        rideOfferLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newRideOfferIntent = new Intent(getActivity(), RiderOfferDetailActivity.class);
                newRideOfferIntent.putExtra("INDEX_LOCATION", position);
                startActivity(newRideOfferIntent);
            }
        });

        return theView;
    }

}
