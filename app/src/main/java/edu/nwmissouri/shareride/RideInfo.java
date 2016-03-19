package edu.nwmissouri.shareride;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

import java.util.Date;

/**
 * Created by S525339 on 3/3/2016.
 */
public class RideInfo extends GenericJson {
    @Key("_id")
    private String rideInfoId;
    @Key
    private String origin;
    @Key
    private float originLat;
    @Key
    private float originLong;
    @Key
    private String destination;
    @Key
    private float destinationLat;
    @Key
    private float destinationLong;
    @Key
    private Date rideDate;
    @Key
    private String frequencyType;
    @Key
    private RideUser createdBy;
    @Key
    private Date createdOn;
    @Key
    private Date updatedOn;
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL

    public RideInfo() {
    }

    public RideInfo(KinveyMetaData.AccessControlList acl, RideUser createdBy, Date createdOn, String destination, float destinationLat, float destinationLong, String frequencyType, KinveyMetaData meta, String origin, float originLat, float originLong, Date rideDate, String rideInfoId, Date updatedOn) {
        this.acl = acl;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.destination = destination;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.frequencyType = frequencyType;
        this.meta = meta;
        this.origin = origin;
        this.originLat = originLat;
        this.originLong = originLong;
        this.rideDate = rideDate;
        this.rideInfoId = rideInfoId;
        this.updatedOn = updatedOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public RideUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(RideUser createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public float getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(float destinationLat) {
        this.destinationLat = destinationLat;
    }

    public float getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(float destinationLong) {
        this.destinationLong = destinationLong;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public float getOriginLat() {
        return originLat;
    }

    public void setOriginLat(float originLat) {
        this.originLat = originLat;
    }

    public float getOriginLong() {
        return originLong;
    }

    public void setOriginLong(float originLong) {
        this.originLong = originLong;
    }

    public Date getRideDate() {
        return rideDate;
    }

    public void setRideDate(Date rideDate) {
        this.rideDate = rideDate;
    }

    public String getRideInfoId() {
        return rideInfoId;
    }

    public void setRideInfoId(String rideInfoId) {
        this.rideInfoId = rideInfoId;
    }
}
