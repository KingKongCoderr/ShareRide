package edu.nwmissouri.shareride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class Profile_settings extends AppCompatActivity {
    EditText mEditusername,mEditName,mEditPass,mEditphone,mEditemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        mEditusername=(EditText)findViewById(R.id.editnewusername_et);
        mEditName=(EditText)findViewById(R.id.editname_et);
        mEditPass=(EditText)findViewById(R.id.editpassword_et);
        mEditphone=(EditText)findViewById(R.id.editphone_et);
        mEditemail=(EditText)findViewById(R.id.editnewemail_et);
    }
}
