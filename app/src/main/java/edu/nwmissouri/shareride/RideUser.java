package edu.nwmissouri.shareride;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.User;
import com.kinvey.java.model.KinveyMetaData;

/**
 * Created by S525339 on 3/3/2016.
 */
public class RideUser extends GenericJson {
    @Key("_id")
    private String rideUserId;
    @Key
    private String fullname;
    @Key
    private String phone;
    @Key
    private String email;

    public RideUser() {
    }

    public RideUser(String email, String phone, String fullname, String rideUserId) {
        this.email = email;
        this.phone = phone;
        this.fullname = fullname;
        this.rideUserId = rideUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRideUserId() {
        return rideUserId;
    }

    public void setRideUserId(String rideUserId) {
        this.rideUserId = rideUserId;
    }

    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL

    public KinveyMetaData.AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(KinveyMetaData.AccessControlList acl) {
        this.acl = acl;
    }

    public KinveyMetaData getMeta() {
        return meta;
    }

    public void setMeta(KinveyMetaData meta) {
        this.meta = meta;
    }
}
