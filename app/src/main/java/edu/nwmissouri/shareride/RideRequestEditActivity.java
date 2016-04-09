package edu.nwmissouri.shareride;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.core.KinveyClientCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RideRequestEditActivity extends AppCompatActivity implements View.OnClickListener {

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
        fromAddressET = (EditText) findViewById(R.id.fromET);
        toAddressET = (EditText) findViewById(R.id.ToET);
        availabilityET = (EditText) findViewById(R.id.offerAvailabilityET);
        hrsSpinner = (Spinner) findViewById(R.id.offertimeSpinner);
        frequencySpinner = (EditText) findViewById(R.id.offerFrequencySpinner);
        offerId = (TextView) findViewById(R.id.offerIDTV);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        final RideRequestCollection rideRequestCollection = new RideRequestCollection();
        Bundle bundle = getIntent().getExtras();
        resultOfferId = bundle.getString("REQUEST_ID");
        Ride rideObject = rideCollection.getRideObject(resultOfferId);
        //TODO here get the string stored in the string variable and do
        if (rideObject != null) {
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
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ride ride = rideCollection.getRideObject(resultOfferId);
                ride.setRouteFrom(fromAddressET.getText().toString());
                ride.setRouteTo(toAddressET.getText().toString());
                ride.setNoOfAvailability(availabilityET.getText().toString());
                ride.setTimeOfTravel(hrsSpinner.getSelectedItem().toString());
                ride.setFrequency(frequencySpinner.getText().toString());
                kinveyClient.appData("RideCollection", Ride.class).save(ride, new KinveyClientCallback<Ride>() {
                    @Override
                    public void onSuccess(Ride result) {
                        Toast.makeText(getApplicationContext(), "Ride request created", Toast.LENGTH_LONG).show();
                        Log.d("REQUEST", "Sucuess");
                        kinveyClient.appData("RideCollection", Ride.class).get(new KinveyListCallback<Ride>() {
                            @Override
                            public void onSuccess(Ride[] result) {
                                Log.d("Length of the data", String.valueOf(result.length));
                                RideRequestCollection.items.clear();
                                for (Ride ride:result){
                                    if(ride.getRideType().equals("request") && ride.getRideUserId().equals(kinveyClient.user().getUsername())){
                                        rideRequestCollection.addRideCollection(ride);
                                    }
                                }
                                if(RideRequestCollection.items.size()>0){
                                    Ride.rideRequestCount = Integer.parseInt(RideRequestCollection.items.get(RideRequestCollection.items.size()-1).getOfferID());
                                }else{
                                    Ride.rideRequestCount = 0;
                                }
                                Log.d("REQUEST LIST",RideRequestCollection.items.toString());
                                final Intent rideActivityIntent = new Intent(getBaseContext(),RideActivity.class);
                                startActivity(rideActivityIntent);

                            }

                            @Override
                            public void onFailure(Throwable error) {
                                Log.e("ALL DATA", "AppData.get all Failure", error);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.e("TAG", "AppData.save Failure", error);
                        Toast.makeText(getApplicationContext(), "Save error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == frequencySpinner) {
            selectDatePickerDialog.show();
        }
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
