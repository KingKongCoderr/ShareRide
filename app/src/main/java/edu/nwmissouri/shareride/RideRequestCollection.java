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
        //items.add(new Ride(1, "1351, Hampshire avenue south,St. Louis Park, MN,US, 55426","Nicollet mall, MN,US, 55426","4","3:00","Daily"));
    }

    public ArrayList<Ride> getRideCollection()
    {
        return items;
    }

    public void addRideCollection(int OfferId,String fromAddress, String toAddress, String availability, String timeOftravel, String frequency)
    {
        items.add(new Ride(OfferId,fromAddress, toAddress, availability, timeOftravel, frequency));
//        mListAdapter.notifyDataSetChanged();
    }

    public void addRecentRide(Ride ride)
    {
        recentRide = ride;
    }

    public Ride getRecentRide()
    {
        return recentRide;
    }

    public int getMaxOfferId()
    {
        int maxId = 0;
        for(int i =0; i<items.size();i++)
        {
            if(items.get(i) != null)
            {
                if(items.get(i).getOfferID() > maxId)
                {
                    maxId = items.get(i).getOfferID();
                }
            }
        }
        return maxId;
    }

    public Ride getRideObject(int OfferId)
    {
        Ride resultObject = null;
        for(Ride item: items)
        {
            if(item.getOfferID() == OfferId)
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
            if(item.getOfferID() == Integer.parseInt(resultOfferId))
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
