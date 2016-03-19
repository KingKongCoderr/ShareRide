package edu.nwmissouri.shareride;

import java.util.ArrayList;

/**
 * Created by S525339 on 3/2/2016.
 */
public class RideCollection {
    public static ArrayList<Ride> items = new ArrayList<Ride>();
    private RideOfferAdapter mListAdapter;

    public RideCollection()
    {
       // items.add(new Ride("St. Louis Park","Nicollet mall",4));
        //items.add(new Ride("St. Louis Park", "Duluth", 2));
        //items.add(new Ride("Duluth", "Tofte", 2));
    }

    public ArrayList<Ride> getRideCollection()
    {
        return items;
    }

    public void addRideCollection(String fromAddress, String toAddress)
    {
        items.add(new Ride(fromAddress, toAddress, 5));
//        mListAdapter.notifyDataSetChanged();
    }

}
