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

    private String getLatLongFromGivenAddress(String address)
    {
        Geocoder coder = new Geocoder(context, Locale.getDefault());
        boolean geoAvailable = coder.isPresent();
        String strLongitude = "";
        String strLatitude = "";
        StringBuilder latLongResult = new StringBuilder();
        int count = 0;
        try {
            List<Address> list = coder.getFromLocationName(address, 1);
            geoAvailable = coder.isPresent();
            while (count < 10 && list.size() == 0) {
                list = (ArrayList<Address>) coder.getFromLocationName(address,1);
                count++;
            }
            if(list.size()>0) {
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

    private String getDistanceBetween(String fromAddressLatLong, String toAddressLatLong)
    {
        String resultValue = "";

        String[] fromLatLongArrays = fromAddressLatLong.split(",");

        Toast.makeText(context, fromAddressLatLong.toString(), Toast.LENGTH_SHORT).show();
        String[] toLatLongArrays = toAddressLatLong.split(",");

        Toast.makeText(context, toAddressLatLong.toString(), Toast.LENGTH_SHORT).show();
        Double[] fromArray = new Double[fromLatLongArrays.length];
        Double[] toArray = new Double[toLatLongArrays.length];

        if (fromLatLongArrays.length <= 1 || toLatLongArrays.length <= 1) {
            Toast.makeText(context, "Retry! not a valid input", Toast.LENGTH_SHORT).show();
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
            TextView offerId = (TextView)view.findViewById(R.id.itemId);
            //holder.txtItemQuantity = (TextView)view.findViewById(R.id.itemQuantity);

            //view.setTag(holder);

//        else
//        {
//            holder = (RideDetailsHolder)view.getTag();
//        }
        String[] shortFromRoute = getItem(position).getRouteFrom().toString().split(",");
        StringBuilder fromBuilder = new StringBuilder();
        String[] shortFromStreet= shortFromRoute;
        if(shortFromRoute.length > 3) {
            shortFromStreet = shortFromRoute[(shortFromRoute.length) - 4].split(" ");
            fromBuilder.append(shortFromStreet[shortFromStreet.length - 2] + " " + shortFromStreet[shortFromStreet.length - 1]);
            fromBuilder.append(", " + shortFromRoute[(shortFromRoute.length) - 3]);
            fromBuilder.append(", " + shortFromRoute[(shortFromRoute.length) - 2]);
        }
        else
        {
            fromBuilder.append(shortFromStreet[0]);
            fromBuilder.append(shortFromStreet[1]);
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
            toBuilder.append(shortToStreet[1]);
        }

        RouteFrom.setText("From: " + fromBuilder.toString());
        RouteTo.setText("To: " + toBuilder.toString());
        NoOfAvailability.setText("Availability: " + getItem(position).getNoOfAvailability().toString());
        offerId.setText("RequestID: " + String.valueOf(getItem(position).getOfferID()));
        //holder.txtItemQuantity.setText(String.format("%d", rideDetailses[position].getItemQuantity()));
        //text2.setText(String.format("%1$,.2f",getItem(position).getPrice()));
        return view;
    }

}
