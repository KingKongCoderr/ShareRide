package edu.nwmissouri.shareride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

public class Profile_settings extends AppCompatActivity {
    EditText mEditNameET, mEditphoneET, mEditemailET;
    Button update ;
    Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        mEditNameET =(EditText)findViewById(R.id.editname_et);
        mEditphoneET =(EditText)findViewById(R.id.editphone_et);
        mEditemailET =(EditText)findViewById(R.id.editnewemail_et);
        update = (Button)findViewById(R.id.editnewsubmit_bt);
        mEditNameET.setText(RideUser.currentUser.getFullname());
        mEditphoneET.setText(RideUser.currentUser.getPhone());
        mEditemailET.setText(RideUser.currentUser.getEmail());
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RideUser.currentUser.setFullname(mEditNameET.getText().toString());
                RideUser.currentUser.setPhone(mEditphoneET.getText().toString());
                RideUser.currentUser.setEmail(mEditemailET.getText().toString());
                kinveyClient.appData("RideUser", RideUser.class).save(RideUser.currentUser, new KinveyClientCallback<RideUser>() {
                    @Override
                    public void onSuccess(RideUser rideUser) {
                        RideUser.currentUser = rideUser;
                        RideRequestCollection.recentRide = rideUser.getRideRecent();
                        Log.d("RIDEUSER UPDATE", " UPdated SUCSESS");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });


            }
        });
    }
}
