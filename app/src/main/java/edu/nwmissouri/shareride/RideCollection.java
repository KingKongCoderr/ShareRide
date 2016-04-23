package edu.nwmissouri.shareride;

import android.view.View;

import com.kinvey.android.Client;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by S525339 on 3/2/2016.
 */
public class RideCollection{
    public static ArrayList<Ride> items = new ArrayList<Ride>();
    public static ArrayList<Ride> searchItems = new ArrayList<Ride>();
    private RideOfferAdapter mListAdapter;

    public RideCollection()
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


    public void refreshRecentSearchData()
    {

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
