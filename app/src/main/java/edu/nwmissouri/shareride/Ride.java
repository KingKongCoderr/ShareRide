package edu.nwmissouri.shareride;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

/**
 * Created by s525339 on 2/10/2016.
 */
public class Ride extends GenericJson {
    @Key
    private String routeFrom;
    @Key
    private String routeTo;
    @Key
    private String noOfAvailability;
    @Key
    private String timeOfTravel;
    @Key
    private String frequency;
    @Key("_id")
    private String offerID;
    @Key
    private String rideType;
    @Key
    private String rideUserId;

    public static int rideCount = 0;

    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL

    public Ride() {
    }

    public Ride(String offerID, String routeFrom, String routeTo, String noOfAvailability, String timeOfTravel, String frequency, String rideType, String rideUserId) {
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.noOfAvailability = noOfAvailability;
        this.timeOfTravel = timeOfTravel;
        this.frequency = frequency;
        this.offerID = offerID;
        this.rideType = rideType;
        this.rideUserId = rideUserId;
    }

    public String getRideUserId() {
        return rideUserId;
    }

    public void setRideUserId(String rideUserId) {
        this.rideUserId = rideUserId;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public String  getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
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

    public KinveyMetaData getMeta() {
        return meta;
    }

    public void setMeta(KinveyMetaData meta) {
        this.meta = meta;
    }

    public KinveyMetaData.AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(KinveyMetaData.AccessControlList acl) {
        this.acl = acl;
    }
    public void setNoOfAvailability(String noOfAvailability) {
        this.noOfAvailability = noOfAvailability;
    }
}
