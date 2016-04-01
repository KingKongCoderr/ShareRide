package edu.nwmissouri.shareride;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

public class RegistrationActivity extends AppCompatActivity {

    private Client kinveyClient;
    private EditText fullNameET;
    private EditText userNameET;
    private EditText passwordET;
    private EditText confirmPasswordET;
    private EditText phoneNumberET;
    private EditText emailET;
    private Button mRegistration_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fullNameET = (EditText)findViewById(R.id.name_et);
        userNameET = (EditText)findViewById(R.id.newusername_et);
        passwordET = (EditText)findViewById(R.id.password_et);
        confirmPasswordET = (EditText)findViewById(R.id.confirmpassword_et);
        phoneNumberET = (EditText)findViewById(R.id.phone_et);
        emailET = (EditText)findViewById(R.id.newemail_et);
        mRegistration_bt = (Button)findViewById(R.id.newsubmit_bt);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();

        mRegistration_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kinveyClient.user().logout().execute(); // user credentials are cached (which is normally a good thing), but we always want to have to log in, so we log out to start
                Toast.makeText(getApplicationContext(), "Creating user...", Toast.LENGTH_SHORT).show();
                if(confirmPasswordET.getText().toString().equals(passwordET.getText().toString())){
                    Toast.makeText(getApplicationContext(),"password matched",Toast.LENGTH_SHORT).show();
                }
                kinveyClient.user().create(userNameET.getText().toString(), passwordET.getText().toString(), new KinveyUserCallback() {
                    @Override
                    public void onFailure(Throwable t) {
                        CharSequence text = "Could not sign up -> " + t.getMessage();
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        Log.e("Kinvey Sign Up", "Sign-up error", t);

                    }

                    @Override
                    public void onSuccess(User u) {
                        CharSequence text = u.getUsername() + ", your account has been created.";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        RideUser rideUser = new RideUser();
                        rideUser.setFullname(fullNameET.getText().toString());
                        rideUser.setPhone(phoneNumberET.getText().toString());
                        rideUser.setEmail(emailET.getText().toString());
                        rideUser.setUser(u);
                        try{
                            AsyncAppData<RideUser> userInfo = kinveyClient.appData("User Information", RideUser.class);
                            userInfo.save(rideUser, new KinveyClientCallback<RideUser>() {
                                @Override
                                public void onSuccess(RideUser user) {
                                    Log.d("UserSignUp","Successfully user Created");
                                }

                                @Override
                                public void onFailure(Throwable e) {
                                    Log.e("", "failed to save event data", e);
                                }
                            });
                        }catch (Exception e){
                            Log.e("DATAERROR",e.getMessage());
                        }
                        Intent rideActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(rideActivity);
                        RegistrationActivity.this.finish();
                    }
                });
            }
        });
    }
}