package edu.nwmissouri.shareride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ForgotpassActivity extends AppCompatActivity {

    /**
     * This activity is used when forgot password button is clicked from login activity
     */

    TextView message_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        String message = "Please! send an email to shareride.support@gmail.com by mentioning your User ID.";
        message_tv = (TextView) findViewById(R.id.email_tv);
        message_tv.setText(message);
    }
}
