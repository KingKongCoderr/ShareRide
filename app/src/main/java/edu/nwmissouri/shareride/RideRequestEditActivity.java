package edu.nwmissouri.shareride;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RideRequestEditActivity extends AppCompatActivity implements View.OnClickListener{

    RideRequestCollection rideCollection = new RideRequestCollection();
    String resultOfferId;
    EditText fromAddressET;
    EditText toAddressET;
    EditText availabilityET;
    Spinner hrsSpinner;
    EditText frequencySpinner;
    TextView offerId;
    Client kinveyClient;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog selectDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_request_edit);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        fromAddressET = (EditText)findViewById(R.id.fromET);
        toAddressET = (EditText)findViewById(R.id.ToET);
        availabilityET = (EditText) findViewById(R.id.offerAvailabilityET);
        hrsSpinner = (Spinner) findViewById(R.id.offertimeSpinner);
        frequencySpinner = (EditText) findViewById(R.id.offerFrequencySpinner);
        offerId = (TextView) findViewById(R.id.offerIDTV);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        Bundle bundle = getIntent().getExtras();
        resultOfferId = bundle.getString("REQUEST_ID");
        Ride rideObject = rideCollection.getRideObject(resultOfferId);
        //TODO here get the string stored in the string variable and do
        if(rideObject !=null) {
            fromAddressET.setText(rideObject.getRouteFrom());
            toAddressET.setText(rideObject.getRouteTo());
            availabilityET.setText(rideObject.getNoOfAvailability());
            hrsSpinner.setSelection(getIndex(hrsSpinner, rideObject.getTimeOfTravel().toString()));
            frequencySpinner.setText(rideObject.getFrequency().toString());
            offerId.setText(resultOfferId);
        }

        Button btnSubmit = (Button) findViewById(R.id.searchBTN);
        frequencySpinner.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        selectDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                frequencySpinner.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                kinveyClient.appData("RideCollection", Ride.class).getEntity(resultOfferId, new KinveyClientCallback<Ride>() {
                    @Override
                    public void onSuccess(Ride ride) {
                        boolean result = rideCollection.setRideObject(resultOfferId, fromAddressET.getText().toString(), toAddressET.getText().toString(), availabilityET.getText().toString(), hrsSpinner.getSelectedItem().toString(), frequencySpinner.getText().toString());
                        if(result == true)
                        {
                            Toast.makeText(getBaseContext(), "Ride updated!", Toast.LENGTH_SHORT).show();
                            Intent rideActivity = new Intent(getBaseContext(),RideActivity.class);
                            startActivity(rideActivity);
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"Ride updated in kinvey but not in ride collection!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Toast.makeText(getBaseContext(),"Ride not updated!", Toast.LENGTH_SHORT).show();
                    }
                });
                boolean result = rideCollection.setRideObject(resultOfferId, fromAddressET.getText().toString(), toAddressET.getText().toString(), availabilityET.getText().toString(), hrsSpinner.getSelectedItem().toString(), frequencySpinner.getText().toString());
                if(result == true)
                {
                    Toast.makeText(getBaseContext(), "Ride updated!", Toast.LENGTH_SHORT).show();
                    Intent rideActivity = new Intent(getBaseContext(),RideActivity.class);
                    startActivity(rideActivity);
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Ride not updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == frequencySpinner) {
            selectDatePickerDialog.show();
        }
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
}
