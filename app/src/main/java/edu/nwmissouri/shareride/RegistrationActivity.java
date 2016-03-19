package edu.nwmissouri.shareride;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

public class RegistrationActivity extends AppCompatActivity {

    Client client;
    EditText userNameET;
    EditText passwordET;
    private Button mRegistration_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        userNameET = (EditText)findViewById(R.id.newusername_et);
        passwordET = (EditText)findViewById(R.id.password_et);
        mRegistration_bt = (Button)findViewById(R.id.newsubmit_bt);
        client = new Client.Builder("kid_Z10tuBjTCx", "41f0212a449c472fab5e423f7c225f98", this.getApplicationContext()).build();
        client.user().logout(); // user credentials are cached (which is normally a good thing), but we always want to have to log in, so we log out to start
        pingKinvey();

        mRegistration_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                client.user().create(userNameET.getText().toString(), passwordET.getText().toString(), new KinveyUserCallback() {
                    @Override
                    public void onFailure(Throwable t) {
                        CharSequence text = "Could not sign up.";
                        Log.d("TAGGER", t.toString());
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(User u) {
                        CharSequence text = u.getUsername() + ", your account has been created.";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        client.user().logout().execute(); // once we register, let's log out, so we have to log in explicitly.
                        Intent rideActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(rideActivity);
                    }
                });

            }
        });
    }

    // Tests the kinvey connection
    public void pingKinvey(){

        client.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("TAGGER", "Kinvey Ping Failed", t);
            }

            public void onSuccess(Boolean b) {
                Log.d("TAGGER", "Kinvey Ping Success");
            }
        });
    }

}