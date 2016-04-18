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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by S525339 on 3/24/2016.
 */
public class RideSearchResultsAdapter extends ArrayAdapter<Ride> {
    Context context;
    int textViewResourceId;
    List<Ride> rideDetailses = null;
    String fromAddressLatLong ="";
    String toAddressLatLong = "";
    String searchFromAddress;
    String searchToAddress;
    String searchAvailability;
    String searchRideTime;
    String searchRideDate;

    public RideSearchResultsAdapter(Context context, int textViewResourceId, List<Ride> items,String searchFromAddress,String searchToAddress,String searchAvailability,String searchRideTime,String searchRideDate)
    {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.rideDetailses = items;
        this.searchFromAddress = searchFromAddress;
        this.searchToAddress = searchToAddress;
        this.searchAvailability = searchAvailability;
        this.searchRideTime = searchRideTime;
        this.searchRideDate = searchRideDate;
    }

    private String getLatLongFromGivenAddress(String address)
    {
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

        Toast.makeText(getContext(), fromAddressLatLong.toString(), Toast.LENGTH_SHORT).show();
        String[] toLatLongArrays = toAddressLatLong.split(",");

        Toast.makeText(getContext(), toAddressLatLong.toString(), Toast.LENGTH_SHORT).show();
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

    private boolean isResultValid(String fromAddressLatLong, String toAddressLatLong, String RideAvailability, String RideTime, String RideDate)
    {
        Date dateRideDate = new Date();
        Date SearchDateRideDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

        String searchFromLatLong = getLatLongFromGivenAddress(searchFromAddress);
        String searchToLatLong = getLatLongFromGivenAddress(searchToAddress);

        float distanceFromPlaces = Float.parseFloat(getDistanceBetween(searchFromLatLong, fromAddressLatLong));
        float distanceToPlaces = Float.parseFloat(getDistanceBetween(searchToLatLong, toAddressLatLong));
        float floatRideAvailability = Float.parseFloat(RideAvailability);
        try {
            dateRideDate = formatter.parse(RideDate);
            SearchDateRideDate = formatter.parse(searchRideDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(distanceFromPlaces <= 1.00 && distanceToPlaces <= 1.00 && floatRideAvailability > 0 && (RideTime.equals(searchRideTime)) && (formatter.format(dateRideDate).equals(formatter.format(SearchDateRideDate))))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent)
    {
        View view = oldView;
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        view = inflater.inflate(textViewResourceId, parent, false);
        String[] shortFromRoute = getItem(position).getRouteFrom().toString().split(",");
        fromAddressLatLong = getLatLongFromGivenAddress(getItem(position).getRouteFrom().toString());
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
            fromBuilder.append(", " + shortFromStreet[1]);
        }

        String[] shortToRoute = getItem(position).getRouteTo().toString().split(",");
        toAddressLatLong = getLatLongFromGivenAddress(getItem(position).getRouteTo().toString());
        //boolean isRideValid = isResultValid(fromAddressLatLong,toAddressLatLong,getItem(position).getNoOfAvailability().toString(),getItem(position).getTimeOfTravel().toString(),getItem(position).getFrequency().toString());


            TextView RouteFrom = (TextView)view.findViewById(R.id.itemName);
            TextView RouteTo = (TextView)view.findViewById(R.id.itemPrice);
            TextView NoOfAvailability = (TextView)view.findViewById(R.id.itemQuantity);
            TextView offerId = (TextView)view.findViewById(R.id.itemId);

            String distanceBetween = getDistanceBetween(fromAddressLatLong, toAddressLatLong);
            StringBuilder toBuilder = new StringBuilder();
            String[] shortToStreet= shortToRoute;
            if(shortToStreet.length > 3) {
                shortToStreet = shortToRoute[(shortToRoute.length) - 4].split(" ");
                toBuilder.append(shortToStreet[shortToStreet.length - 2] + " " + shortToStreet[shortToStreet.length - 1]);
                toBuilder.append(", " + shortToRoute[(shortToRoute.length) - 3]);
                toBuilder.append(", " + shortToRoute[(shortToRoute.length) - 2]);
            }
            else
            {
                toBuilder.append(shortToStreet[0]);
                toBuilder.append(", " + shortToStreet[1]);
            }

            RouteFrom.setText("From: " + fromBuilder.toString());
            RouteTo.setText("To: " + toBuilder.toString());
            NoOfAvailability.setText("Availability: " + getItem(position).getNoOfAvailability().toString());
            float distanceFloat = Float.parseFloat(distanceBetween);
            distanceBetween = String.format("%.1f", distanceFloat);
            offerId.setText(distanceBetween + " miles");

        return view;

    }
}
