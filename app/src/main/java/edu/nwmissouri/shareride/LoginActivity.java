package edu.nwmissouri.shareride;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mp = null;
    SurfaceView mSurfaceView = null;
    SurfaceHolder holder = null;
    private Button mLogin_bt;
    private TextView mForgotPassTV, mCreateNewTV;
    private EditText mUsername_et, mPassword_et;
    public String mUsername, mPassword;
    boolean shouldPlay = false;
    AssetFileDescriptor afd;
    Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        final RideCollection rideCollection = new RideCollection();
        final RideRequestCollection rideRequestCollection = new RideRequestCollection();
        mp = new MediaPlayer();
        mp.setVolume(0f, 0f);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        afd = getResources().openRawResourceFd(R.raw.background);
        mLogin_bt = (Button) findViewById(R.id.login_bt);
        mForgotPassTV = (TextView) findViewById(R.id.forgotpass_tv);
        mCreateNewTV = (TextView) findViewById(R.id.newuser_tv);
        mUsername_et = (EditText) findViewById(R.id.username_et);
        mPassword_et = (EditText) findViewById(R.id.password_et);

        try {
            mUsername = mUsername_et.getText().toString();
            mPassword = mPassword_et.getText().toString();

        } catch (Exception e) {

        }
        mLogin_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kinveyClient.user().isUserLoggedIn()) {
                    kinveyClient.user().logout().execute(); // user credentials are cached (which is normally a good thing), but we always want to have to log in, so we log out to start
                }

                kinveyClient.user().login(mUsername_et.getText().toString(), mPassword_et.getText().toString(), new KinveyUserCallback() {
                    @Override
                    public void onFailure(Throwable t) {
                        CharSequence text = "Wrong username or password";
                        Log.d("TAGGER", t.toString());
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(User u) {
                        CharSequence text = "Welcome back," + u.getUsername() + ".";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                        kinveyClient.appData("RideUser", RideUser.class).getEntity(kinveyClient.user().getUsername(), new KinveyClientCallback<RideUser>() {
                            @Override
                            public void onSuccess(RideUser result) {
                                RideUser.currentUser = result;
                                RideRequestCollection.recentRide = result.getRideRecent();
                            }

                            @Override
                            public void onFailure(Throwable error) {

                            }
                        });

                        if (null != RideRequestCollection.recentRide) {
                            Log.d("RECENT RIDE LOGIN CLICK", RideRequestCollection.recentRide.toString());
                        } else {
                            Log.d("RECENT RIDE LOGIN CLICK", "Nothing in recent ride");
                        }

                        kinveyClient.appData("RideCollection", Ride.class).get(new KinveyListCallback<Ride>() {
                            @Override
                            public void onSuccess(Ride[] result) {
                                Log.d("Length of the data", String.valueOf(result.length));
                                RideCollection.items.clear();
                                RideRequestCollection.items.clear();
                                for (Ride ride : result) {
                                    if (ride.getRideType().equals("offer") && ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                        rideCollection.addRideCollection(ride);
                                    } else if (ride.getRideType().equals("request") && ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                        rideRequestCollection.addRideCollection(ride);
                                    }
                                }
                                if (RideCollection.items.size() > 0) {
                                    Ride.rideOfferCount = Integer.parseInt(RideCollection.items.get(RideCollection.items.size() - 1).getOfferID());
                                } else {
                                    Ride.rideOfferCount = 0;
                                }
                                if (RideRequestCollection.items.size() > 0) {
                                    Ride.rideRequestCount = Integer.parseInt(RideRequestCollection.items.get(RideRequestCollection.items.size() - 1).getOfferID());
                                } else {
                                    Ride.rideRequestCount = 0;
                                }
                                RideCollection.searchItems.clear();
                                for (Ride ride : result) {
                                    if (ride.getRideType().equals("offer")) {
                                        if (!ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                            RideCollection.searchItems.add(ride);
                                        }
                                    }
                                }

                                final Intent rideActivityIntent = new Intent(getBaseContext(), RideActivity.class);
                                startActivity(rideActivityIntent);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                Log.e("ALL DATA", "AppData.get all Failure", error);
                            }
                        });
                        Intent rideActivity = new Intent(LoginActivity.this, RideActivity.class);
                        startActivity(rideActivity);
                    }
                });
            }
        });
        mForgotPassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotpass = new Intent(LoginActivity.this, ForgotpassActivity.class);
                startActivity(forgotpass);

            }
        });
        mCreateNewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(reg_intent);
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!shouldPlay) {
            try {
                shouldPlay = true;
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            mp.setDisplay(holder);
            mp.setLooping(true);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        }

        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        lp.height = screenHeight;
        //Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);

        //Start video
        mp.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("changed:", "inside changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}

