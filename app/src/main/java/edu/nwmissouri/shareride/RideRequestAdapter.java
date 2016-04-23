package edu.nwmissouri.shareride;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by s525339 on 2/10/2016.
 */
public class RideRequestAdapter extends ArrayAdapter<Ride> {
    Context context;
    int textViewResourceId;
    List<Ride> rideDetailses = null;

    public RideRequestAdapter(Context context, int textViewResourceId, List<Ride> items)
    {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.rideDetailses = items;
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        View view = oldView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(textViewResourceId, parent, false);
        TextView RouteFromTV = (TextView) view.findViewById(R.id.itemName);
        TextView RouteToTV = (TextView) view.findViewById(R.id.itemPrice);
        TextView NoOfAvailabilityTV = (TextView) view.findViewById(R.id.itemQuantity);
        TextView offerIdTV = (TextView) view.findViewById(R.id.itemId);
        String[] shortFromRoute = getItem(position).getRouteFrom().toString().split(",");
        StringBuilder fromBuilder = new StringBuilder();
        String[] shortFromStreet = shortFromRoute;
        if(shortFromRoute.length > 3) {
            shortFromStreet = shortFromRoute[(shortFromRoute.length) - 4].split(" ");
            fromBuilder.append(shortFromStreet[shortFromStreet.length - 2] + " " + shortFromStreet[shortFromStreet.length - 1]);
            fromBuilder.append(", " + shortFromRoute[(shortFromRoute.length) - 3]);
            fromBuilder.append(", " + shortFromRoute[(shortFromRoute.length) - 2]);
        }
        else
        {
            fromBuilder.append(shortFromStreet[0]);
            fromBuilder.append(", " + shortFromStreet[1]);
        }

        String[] shortToRoute = getItem(position).getRouteTo().toString().split(",");
        StringBuilder toBuilder = new StringBuilder();
        String[] shortToStreet= shortToRoute;
        if(shortToStreet.length > 3) {
            shortToStreet = shortToRoute[(shortToRoute.length) - 4].split(" ");
            toBuilder.append(shortToStreet[shortToStreet.length - 2] + " " + shortToStreet[shortToStreet.length - 1]);
            toBuilder.append(", " + shortToRoute[(shortToRoute.length) - 3]);
            toBuilder.append(", " + shortToRoute[(shortToRoute.length) - 2]);
        }else
        {
            toBuilder.append(shortToStreet[0]);
            toBuilder.append(", " + shortToStreet[1]);
        }

        RouteFromTV.setText("From: " + fromBuilder.toString());
        RouteToTV.setText("To: " + toBuilder.toString());
        NoOfAvailabilityTV.setText("Availability: " + getItem(position).getNoOfAvailability().toString());
        offerIdTV.setText("RequestID: " + String.valueOf(getItem(position).getOfferID()));
        return view;
    }
}
