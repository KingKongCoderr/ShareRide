package edu.nwmissouri.shareride;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static edu.nwmissouri.shareride.NewRideOfferActivity.autocomplete;

public class RideOfferEditActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    RideCollection rideCollection = new RideCollection();
    String resultOfferId;
    AutoCompleteTextView fromAddressET;
    AutoCompleteTextView toAddressET;
    EditText availabilityET;
    Spinner hrsSpinner;
    EditText frequencySpinnerET;
    TextView offerId;
    Client kinveyClient;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog selectDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offer_edit);
        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();
        fromAddressET = (AutoCompleteTextView)findViewById(R.id.fromET);
        toAddressET = (AutoCompleteTextView)findViewById(R.id.ToET);
        availabilityET = (EditText) findViewById(R.id.offerAvailabilityET);
        hrsSpinner = (Spinner) findViewById(R.id.offertimeSpinner);
        frequencySpinnerET = (EditText) findViewById(R.id.offerFrequencySpinner);
        offerId = (TextView) findViewById(R.id.offerIDTV);
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

        Bundle bundle = getIntent().getExtras();
        resultOfferId = bundle.getString("OFFER_ID");
        Ride rideObject = rideCollection.getRideObject(resultOfferId);
        //TODO here get the string stored in the string variable and do
        if(rideObject !=null) {
            fromAddressET.setText(rideObject.getRouteFrom());
            toAddressET.setText(rideObject.getRouteTo());
            availabilityET.setText(rideObject.getNoOfAvailability());
            hrsSpinner.setSelection(getIndex(hrsSpinner, rideObject.getTimeOfTravel().toString()));
            frequencySpinnerET.setText(rideObject.getFrequency().toString());
            offerId.setText(resultOfferId);
        }

        fromAddressET.setAdapter(new GooglePlacesAutoCompleteAdapter(this, R.layout.places_result));
        fromAddressET.setOnItemClickListener(this);

        toAddressET.setAdapter(new GooglePlacesAutoCompleteAdapter(this, R.layout.places_result));
        toAddressET.setOnItemClickListener(this);

        Button btnSubmit = (Button) findViewById(R.id.searchBTN);
        frequencySpinnerET.setOnClickListener(this);
        frequencySpinnerET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectDatePickerDialog.show();
                    frequencySpinnerET.setInputType(0);
                } else {
                    selectDatePickerDialog.hide();
                    frequencySpinnerET.setInputType(0);
                }
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        selectDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                frequencySpinnerET.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String fromLatLong = getLatLongFromGivenAddress(fromAddressET.getText().toString());
                String toLatLong = getLatLongFromGivenAddress(toAddressET.getText().toString());

                if (fromAddressET.getText().toString().length() == 0 || toAddressET.getText().toString().length() == 0 || availabilityET.getText().toString().length() == 0 || hrsSpinner.getSelectedItem().toString().length() == 0 || frequencySpinnerET.getText().toString().length() == 0 || fromLatLong.indexOf("Invalid") != -1 || toLatLong.indexOf("Invalid") != -1) {
                    Toast.makeText(getBaseContext(), "Invalid Inputs", Toast.LENGTH_SHORT).show();
                } else {
                    Ride ride = rideCollection.getRideObject(resultOfferId);
                    ride.setRouteFrom(fromAddressET.getText().toString());
                    ride.setRouteTo(toAddressET.getText().toString());
                    ride.setNoOfAvailability(availabilityET.getText().toString());
                    ride.setTimeOfTravel(hrsSpinner.getSelectedItem().toString());
                    ride.setFrequency(frequencySpinnerET.getText().toString());

                    kinveyClient.appData("RideCollection", Ride.class).save(ride, new KinveyClientCallback<Ride>() {
                        @Override
                        public void onSuccess(Ride result) {
                            kinveyClient.appData("RideCollection", Ride.class).get(new KinveyListCallback<Ride>() {
                                @Override
                                public void onSuccess(Ride[] result) {
                                    Log.d("Length of the data", String.valueOf(result.length));
                                    RideCollection.items.clear();
                                    for (Ride ride : result) {
                                        if (ride.getRideType().equals("offer") && ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                            rideCollection.addRideCollection(ride);
                                        }
                                    }
                                    if (RideCollection.items.size() > 0) {
                                        Ride.rideOfferCount = Integer.parseInt(RideCollection.items.get(RideCollection.items.size() - 1).getOfferID());
                                    } else {
                                        Ride.rideOfferCount = 0;
                                    }
                                    Log.d("OFFER LIST", RideCollection.items.toString());
                                    final Intent rideActivityIntent = new Intent(getBaseContext(), RideActivity.class);
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
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == frequencySpinnerET) {
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

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        String distance = getLatLongFromGivenAddress(str);
    }

    public String getLatLongFromGivenAddress(String address) {
        Geocoder coder = new Geocoder(getBaseContext(), Locale.getDefault());
        String strLongitude = "";
        String strLatitude = "";
        StringBuilder latLongResult = new StringBuilder();
        int count = 0;
        try {
            List<Address> list = coder.getFromLocationName(address, 1);
            while (count < 10 && list.size() == 0) {
                list = (ArrayList<Address>) coder.getFromLocationName(address, 1);
                count++;
            }
            if (list.size() > 0) {
                double longitude = list.get(0).getLongitude();
                double latitude = list.get(0).getLatitude();
                strLongitude = String.format("%f", longitude);
                strLatitude = String.format("%f", latitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(strLongitude != "" || strLatitude != "") {
            String result = strLongitude + "," + strLatitude;
            latLongResult.append(result);
        }
        else
        {
            latLongResult.append("Invalid");
        }

        return latLongResult.toString();
    }
}
/*class GPACA4 extends GooglePlacesAutoCompleteAdapter{

    public GPACA4(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
}*/

