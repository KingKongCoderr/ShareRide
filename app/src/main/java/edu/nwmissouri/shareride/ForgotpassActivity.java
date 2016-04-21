package edu.nwmissouri.shareride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ForgotpassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        String message = "Please! send an email to shareride.support@gmail.com by mentioning your User ID.";
        TextView messageTV = (TextView) findViewById(R.id.email_et);
        messageTV.setText(message);
    }
}
