package edu.nwmissouri.shareride;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by s525339 on 2/10/2016.
 */
public class RideOfferAdapter extends ArrayAdapter<Ride> {
    Context context;
    int textViewResourceId;
    List<Ride> rideDetailses = null;

    public RideOfferAdapter(Context context, int textViewResourceId, List<Ride> items)
    {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.rideDetailses = items;
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        //View view = super.getView(position, oldView, parent);
        View view = oldView;
        //RideDetailsHolder holder = null;
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(textViewResourceId, parent, false);
            //holder = new RideDetailsHolder();
            TextView RouteFrom = (TextView)view.findViewById(R.id.itemName);
            TextView RouteTo = (TextView)view.findViewById(R.id.itemPrice);
            TextView NoOfAvailability = (TextView)view.findViewById(R.id.itemQuantity);
            //holder.txtItemQuantity = (TextView)view.findViewById(R.id.itemQuantity);

            //view.setTag(holder);

//        else
//        {
//            holder = (RideDetailsHolder)view.getTag();
//        }
        String[] shortFromRoute = getItem(position).getRouteFrom().toString().split(",");
        StringBuilder fromBuilder = new StringBuilder();
        String[] shortFromStreet =shortFromRoute[(shortFromRoute.length)-4].split(" ");
        fromBuilder.append(shortFromStreet[shortFromStreet.length-2] + " " + shortFromStreet[shortFromStreet.length-1]);
        fromBuilder.append(", " + shortFromRoute[(shortFromRoute.length)-3]);
        fromBuilder.append(", " + shortFromRoute[(shortFromRoute.length)-2]);

        String[] shortToRoute = getItem(position).getRouteTo().toString().split(",");
        StringBuilder toBuilder = new StringBuilder();
        String[] shortToStreet =shortFromRoute[(shortToRoute.length)-4].split(" ");
        toBuilder.append(shortToStreet[shortFromStreet.length-2] + " " + shortToStreet[shortFromStreet.length-1]);
        toBuilder.append(", " + shortToRoute[(shortToRoute.length)-3]);
        toBuilder.append(", " + shortToRoute[(shortToRoute.length)-2]);

        RouteFrom.setText("From: " + fromBuilder.toString());
        RouteTo.setText("To: " + toBuilder.toString());
        NoOfAvailability.setText("Availability: " + String.format("%d",getItem(position).getNoOfAvailabilityy()));
        //holder.txtItemQuantity.setText(String.format("%d", rideDetailses[position].getItemQuantity()));
        //text2.setText(String.format("%1$,.2f",getItem(position).getPrice()));
        return view;
    }

}
