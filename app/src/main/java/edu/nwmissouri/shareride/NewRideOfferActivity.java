package edu.nwmissouri.shareride;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.view.View.OnClickListener;

import static edu.nwmissouri.shareride.NewRideOfferActivity.autocomplete;

public class NewRideOfferActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener, OnClickListener  {

    private static final String LOG_TAG = " Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyA36XH-x4gJIQvAc7p7LHN55xm_oDVEkhw";
    private DatePickerDialog selectDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText frequencyET;
    public static final String TAG = "ShareRideKinvey";
    private String appKey = "kid_ZJCDL-Jpy-";
    private String appSecret = "7ba9e5e0015849b790845e669ab87992";
    private Client kinveyClient;
    private Date date = null;
    String distance = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ride_offer);
        kinveyClient = new Client.Builder(appKey, appSecret
                , this.getApplicationContext()).build();
       ActionBar actionbar = getSupportActionBar();
       actionbar.setDisplayHomeAsUpEnabled(true);
        AutoCompleteTextView fromET = (AutoCompleteTextView) findViewById(R.id.fromET);
        AutoCompleteTextView toET = (AutoCompleteTextView) findViewById(R.id.ToET);

        final RideCollection rideCollection = new RideCollection();
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        dateFormatter.setLenient(false);

        Button searchBTN = (Button) findViewById(R.id.searchBTN);
        TextView OfferIdTV = (TextView) findViewById(R.id.offerIDTV);
        frequencyET = (EditText) findViewById(R.id.offerFrequencySpinner);
        frequencyET.setOnClickListener(this);
        frequencyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectDatePickerDialog.show();
                    frequencyET.setInputType(0);
                } else {
                    selectDatePickerDialog.hide();
                    frequencyET.setInputType(0);
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        selectDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                  Calendar newDate = Calendar.getInstance();
                  newDate.set(year, monthOfYear, dayOfMonth);
                   frequencyET.setText(dateFormatter.format(newDate.getTime()));
                    }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        OfferIdTV.setText(String.valueOf(Ride.rideOfferCount +1));

        fromET.setAdapter(new GooglePlacesAutoCompleteAdapter(this, R.layout.places_result));
        fromET.setOnItemClickListener(this);

        toET.setAdapter(new GooglePlacesAutoCompleteAdapter(this, R.layout.places_result));
        toET.setOnItemClickListener(this);
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fromET = (EditText) findViewById(R.id.fromET);
                EditText toET = (EditText) findViewById(R.id.ToET);
                EditText availabilityET = (EditText) findViewById(R.id.offerAvailabilityET);
                Spinner hrsSpinner = (Spinner) findViewById(R.id.offertimeSpinner);
                String fromStr = fromET.getText().toString();
                String toStr = toET.getText().toString();
                Log.d("MAXOFFERID", Ride.rideOfferCount + "");
                String noOfPersons = availabilityET.getText().toString();
                String travelHrs = hrsSpinner.getSelectedItem().toString();
                String frequencyHrs = frequencyET.getText().toString();
                try
                {
                    date = dateFormatter.parse(frequencyHrs);
                }
                catch (ParseException e) {

                }

                String fromLatLong = getLatLongFromGivenAddress(fromStr);
                String toLatLong = getLatLongFromGivenAddress(toStr);

                if (fromStr.length() == 0 || toStr.length()==0 || noOfPersons.length() == 0 || travelHrs.length() == 0 || frequencyHrs.length() == 0 || date == null || fromLatLong.indexOf("Invalid") != -1 || toLatLong.indexOf("Invalid") != -1)
                {
                    Toast.makeText(getBaseContext(),"Invalid Inputs",Toast.LENGTH_SHORT).show();
                }
                else {
                    Ride ride = new Ride(String.valueOf(Ride.rideOfferCount + 1), fromStr, toStr, noOfPersons, travelHrs, frequencyHrs, "offer", kinveyClient.user().getUsername());
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
                                            Toast.makeText(getApplicationContext(), "Your Ride offer will now be visible for other requests", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    if (RideCollection.items.size() > 0) {
                                        Ride.rideOfferCount = Integer.parseInt(RideCollection.items.get(RideCollection.items.size() - 1).getOfferID());
                                    } else {
                                        Ride.rideOfferCount = 0;
                                    }
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
                            Log.e(TAG, "AppData.save Failure", error);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == frequencyET) {
            selectDatePickerDialog.show();
        }
    }


    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        distance = getLatLongFromGivenAddress(str);
        //Toast.makeText(this, distance.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * This method gets the latitude and longitude of the given address
     *
     * @param address - holds the string value of the address
     * @return - returns latitude,longitude
     */

    public String getLatLongFromGivenAddress(String address) {
        Geocoder coder = new Geocoder(getBaseContext(), Locale.getDefault());
        String strLongitude = "";
        String strLatitude = "";
        String result = "";
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
            result = strLongitude + "," + strLatitude;
            latLongResult.append(result);
        }
        else
        {
            latLongResult.append("Invalid");
        }

        return latLongResult.toString();
    }

    /**
     * This method allows autofill of address in from and return addresses
     *
     * @param input
     * @return
     */
    public static ArrayList autocomplete(String input) {

        ArrayList resultList = null;

        HttpURLConnection conn = null;

        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:us");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}
/*class GPACA2 extends GooglePlacesAutoCompleteAdapter{

    public GPACA2(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
}*/
