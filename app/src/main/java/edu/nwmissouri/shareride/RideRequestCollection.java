package edu.nwmissouri.shareride;

import java.util.ArrayList;

/**
 * Created by S525339 on 3/2/2016.
 */
public class RideRequestCollection {
    public static ArrayList<Ride> items = new ArrayList<Ride>();
    public static Ride recentRide;
    private RideOfferAdapter mListAdapter;

    public RideRequestCollection()
    {
    }

    public ArrayList<Ride> getRideCollection()
    {
        return items;
    }

    public void addRideCollection(Ride ride)
    {
        items.add(ride);
    }

    public void addRecentRide(Ride ride)
    {
        recentRide = ride;
    }

    public Ride getRecentRide()
    {
        return recentRide;
    }

    public Ride getRideObject(String OfferId)
    {
        Ride resultObject = null;
        for(Ride item: items)
        {
            if(item.getOfferID().equals(OfferId))
            {
                resultObject =  item;
            }
        }

        return resultObject;
    }

}
