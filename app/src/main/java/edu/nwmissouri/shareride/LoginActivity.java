package edu.nwmissouri.shareride;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mp = null;
    SurfaceView mSurfaceView = null;
    SurfaceHolder holder = null;
    private Button mLogin_bt;
    private TextView mForgotPass, mCreateNew;
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

        mp = new MediaPlayer();
        mp.setVolume(0f, 0f);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        afd = getResources().openRawResourceFd(R.raw.video);
        mLogin_bt = (Button) findViewById(R.id.login_bt);
        mForgotPass = (TextView) findViewById(R.id.forgotpass_tv);
        mCreateNew = (TextView) findViewById(R.id.newuser_tv);
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
                if(kinveyClient.user().isUserLoggedIn()){
                    Toast.makeText(getApplicationContext(),"User alreay Logged in",Toast.LENGTH_SHORT);
                    kinveyClient.user().logout().execute(); // user credentials are cached (which is normally a good thing), but we always want to have to log in, so we log out to start
                }else{
                    Toast.makeText(getApplicationContext(),"Login now",Toast.LENGTH_SHORT);
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
                        Intent rideActivity = new Intent(LoginActivity.this, RideActivity.class);
                        startActivity(rideActivity);
                    }
                });
            }
        });
        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotpass = new Intent(LoginActivity.this, ForgotpassActivity.class);
                startActivity(forgotpass);

            }
        });
        mCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(reg_intent);
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       /* Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.video);*/
        if (!shouldPlay) {
            try {
                shouldPlay = true;
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // mp.setDataSource(String.valueOf(video));
        try {
            mp.setDisplay(holder);
            mp.setLooping(true);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        }


        //Get the dimensions of the video
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video
        //be sure to cast these as floats otherwise the calculation will likely be 0
        //lp.height = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth)+200;
        Log.d("height:", "" + (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth));

        lp.height = screenHeight;
        //Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);

        //Start video
        mp.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("changed:", "inside changed");
        //mp.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
       /* try{
            //mp.reset();

        mp.stop();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }*/
        Log.d("destroyed:", "inside destroyed");

    }
}

