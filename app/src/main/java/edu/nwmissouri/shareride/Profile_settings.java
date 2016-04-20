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
    EditText mEditusername,mEditName,mEditPass,mEditphone,mEditemail;
    Button update ;
    Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        //mEditusername=(EditText)findViewById(R.id.editnewusername_et);
        mEditName=(EditText)findViewById(R.id.editname_et);
       // mEditPass=(EditText)findViewById(R.id.editpassword_et);
        mEditphone=(EditText)findViewById(R.id.editphone_et);
        mEditemail=(EditText)findViewById(R.id.editnewemail_et);
        update = (Button)findViewById(R.id.editnewsubmit_bt);


        //mEditusername.setText(RideUser.currentUser.getRideUserId());
        mEditName.setText(RideUser.currentUser.getFullname());
        mEditphone.setText(RideUser.currentUser.getPhone());
        mEditemail.setText(RideUser.currentUser.getEmail());
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RideUser.currentUser.setFullname(mEditName.getText().toString());
                RideUser.currentUser.setPhone(mEditphone.getText().toString());
                RideUser.currentUser.setEmail(mEditemail.getText().toString());
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
