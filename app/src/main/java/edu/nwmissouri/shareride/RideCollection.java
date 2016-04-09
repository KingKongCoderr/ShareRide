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
       // items.add(new Ride("St. Louis Park","Nicollet mall",4));
        //items.add(new Ride("St. Louis Park", "Duluth", 2));
        //items.add(new Ride("Duluth", "Tofte", 2));
    }

    public ArrayList<Ride> getRideCollection()
    {
        return items;
    }

    public void addRideCollection(Ride ride)
    {
        items.add(ride);
//        mListAdapter.notifyDataSetChanged();
    }

    public void deleteRideCollection()
    {
        //items.add(new Ride(OfferId,fromAddress, toAddress, availability, timeOftravel, frequency));
//        mListAdapter.notifyDataSetChanged();
    }

    public void refreshRecentSearchData()
    {
        Ride tempRide;
        tempRide = temporaryRide();
        items.add(tempRide);
    }

    public Ride temporaryRide()
    {
        Ride myRide = new Ride("1", "1351 Hampshire Avenue South, Saint Louis Park, MN, United States","Nicollet mall, Minneapolis, MN, United States","4","3:00","29-03-2016","offer","rider");
        return myRide;
    }

    public int getMaxOfferId()
    {
        int maxId = 0;
        for(int i =0; i<items.size();i++)
        {
            if(items.get(i) != null)
            {
                if(Integer.parseInt(items.get(i).getOfferID()) > maxId)
                {
                    maxId = Integer.parseInt(items.get(i).getOfferID());
                }
            }
        }
        return maxId;
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

    public boolean setRideObject(String resultOfferId, String fromAddressET,String toAddressET, String availabilityET, String hrsSpinner,String frequencySpinner)
    {
        Ride resultObject = null;
        for(Ride item: items)
        {
            if(item.getOfferID() == resultOfferId)
            {
                item.setRouteFrom(fromAddressET);
                item.setRouteTo(toAddressET);
                item.setNoOfAvailability(availabilityET);
                item.setTimeOfTravel(hrsSpinner);
                item.setFrequency(frequencySpinner);

                return true;
            }
        }

        return false;
    }


}
