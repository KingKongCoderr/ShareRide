package edu.nwmissouri.shareride;

/**
 * Created by s525339 on 2/10/2016.
 */
public class Ride {
    private String routeFrom;
    private String routeTo;
    private String noOfAvailability;
    private String timeOfTravel;
    private String frequency;
    private int offerID = 0;

    public Ride(int offerID, String routeFrom, String routeTo, String noOfAvailability, String timeOfTravel,String frequency) {
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.noOfAvailability = noOfAvailability;
        this.timeOfTravel = timeOfTravel;
        this.frequency = frequency;
        this.offerID = offerID;
    }

    public int getOfferID() {
        return offerID;
    }

    public void setOfferID(int offerID) {
        this.offerID = offerID;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getNoOfAvailability() {
        return noOfAvailability;
    }

    public String getTimeOfTravel() {
        return timeOfTravel;
    }

    public void setTimeOfTravel(String timeOfTravel) {
        this.timeOfTravel = timeOfTravel;
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

    public void setNoOfAvailability(String noOfAvailability) {
        this.noOfAvailability = noOfAvailability;
    }
}
