package edu.nwmissouri.shareride;

/**
 * Created by s525339 on 2/10/2016.
 */
public class Ride {
    private String routeFrom;
    private String routeTo;
    private int noOfAvailability;

    public Ride(String routeFrom, String routeTo, int noOfAvailability) {
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.noOfAvailability = noOfAvailability;
    }

    public String getRouteFrom() {
        return routeFrom;
    }

    public void setRouteFrom(String routeFrom) {
        this.routeFrom = routeFrom;
    }

    public String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(String routeTo) {
        this.routeTo = routeTo;
    }

    public int getNoOfAvailabilityy() {
        return noOfAvailability;
    }

    public void setNoOfAvailability(int noOfAvailability) {
        this.noOfAvailability = noOfAvailability;
    }
}
