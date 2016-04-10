package edu.nwmissouri.shareride;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static edu.nwmissouri.shareride.NewRideOfferActivity.autocomplete;

public class NewRideRequestActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String LOG_TAG = " Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBVGNHxtt0rBeU4jOs427su-0Vc4M06wZs";
    private String fromLatLong = "";
    private String toLatLong = "";
    private DatePickerDialog selectDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText frequencySpinner;
    private static int maxOfferId = 0;

    //Kinvey Details
    public static final String TAG = "ShareRideKinvey";
    private String appKey = "kid_ZJCDL-Jpy-";
    private String appSecret = "7ba9e5e0015849b790845e669ab87992";
    private Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ride_request);
        kinveyClient = new Client.Builder(appKey, appSecret
                , this.getApplicationContext()).build();
        ActionBar actionbar = getSupportActionBar();

        // Enable the Up button
        actionbar.setDisplayHomeAsUpEnabled(true);

        AutoCompleteTextView fromET = (AutoCompleteTextView) findViewById(R.id.fromET);
        AutoCompleteTextView toET = (AutoCompleteTextView) findViewById(R.id.ToET);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        final RideRequestCollection rideRequestCollection = new RideRequestCollection();
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

        Button searchBTN = (Button) findViewById(R.id.searchBTN);
        TextView OfferIdTV = (TextView) findViewById(R.id.offerIDTV);
        frequencySpinner = (EditText) findViewById(R.id.offerFrequencySpinner);
        frequencySpinner.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        selectDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                frequencySpinner.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        OfferIdTV.setText(String.valueOf(Ride.rideRequestCount+1));

        fromET.setAdapter(new GooglePlacesAutocompleteAdapter3(this, R.layout.places_result));
        fromET.setOnItemClickListener(this);

        toET.setAdapter(new GooglePlacesAutocompleteAdapter3(this, R.layout.places_result));
        toET.setOnItemClickListener(this);
        final Intent rideActivityIntent = new Intent(this,RideActivity.class);

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
                String frequencyHrs = frequencySpinner.getText().toString();

                if (fromStr.length() == 0 || toStr.length() == 0 || noOfPersons.length() == 0 || travelHrs.length() == 0 || frequencyHrs.length() == 0) {
                    Toast.makeText(getBaseContext(), "Invalid Inputs", Toast.LENGTH_LONG).show();
                } else {

                    Ride ride = new Ride(String.valueOf(Ride.rideRequestCount + 1), fromStr, toStr, noOfPersons, travelHrs, frequencyHrs, "request", kinveyClient.user().getUsername());
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
                                    for (Ride ride : result) {
                                        if (ride.getRideType().equals("request") && ride.getRideUserId().equals(kinveyClient.user().getUsername())) {
                                            rideRequestCollection.addRideCollection(ride);
                                        }
                                    }
                                    if (RideRequestCollection.items.size() > 0) {
                                        Ride.rideRequestCount = Integer.parseInt(RideRequestCollection.items.get(RideRequestCollection.items.size() - 1).getOfferID());
                                    } else {
                                        Ride.rideRequestCount = 0;
                                    }
                                    Log.d("REQUEST LIST", RideRequestCollection.items.toString());
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
                            Toast.makeText(getApplicationContext(), "Save error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
//                rideActivityIntent.putExtra("fromAddress", fromStr);
//                rideActivityIntent.putExtra("toAddress", toStr);
                }
            }
        });


        if (!kinveyClient.user().isUserLoggedIn()) {
            kinveyClient.user().login(new KinveyUserCallback() {
                @Override
                public void onSuccess(User result) {
                    Log.i(TAG, "Logged in to Kinvey successfully!" + result.getId());
                    Toast.makeText(NewRideRequestActivity.this, "Logged in to Kinvey successfully as " + result.getId(),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Throwable error) {
                    Log.e(TAG, "Login Failure", error);
                    Toast.makeText(NewRideRequestActivity.this, "Login error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Using cached implicit user " + kinveyClient.user().getId(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View view) {
        if(view == frequencySpinner) {
            selectDatePickerDialog.show();
        }
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        String distance = getLatLongFromGivenAddress(str);
        Toast.makeText(this, distance.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * This method gets the latitude and longitude of the given address
     *
     * @param address - holds the string value of the address
     * @return - returns latitude,longitude
     */

    public String getLatLongFromGivenAddress(String address) {
        Geocoder coder = new Geocoder(getBaseContext(), Locale.getDefault());
        boolean geoAvailable = coder.isPresent();
        String strLongitude = "";
        String strLatitude = "";
        StringBuilder latLongResult = new StringBuilder();
        int count = 0;
        try {
            List<Address> list = coder.getFromLocationName(address, 1);
            geoAvailable = coder.isPresent();
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
        String result = strLongitude + "," + strLatitude;
        latLongResult.append(result);

        return latLongResult.toString();
    }


    private double getDistanceinMiles(double fromLat, double fromLong, double toLat, double toLong) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(toLat - fromLat);
        double dLng = Math.toRadians(toLong - fromLong);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(toLat)) * Math.cos(Math.toRadians(fromLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist;
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

            // Extract the Place descriptions from the results
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

class GooglePlacesAutocompleteAdapter3 extends ArrayAdapter implements Filterable
{
    private ArrayList resultList;
    public GooglePlacesAutocompleteAdapter3(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index).toString();
    }

    @Override

    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override

            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                    // Retrieve the autocomplete results.

                    resultList = autocomplete(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

                if (results != null && results.count > 0) {

                    notifyDataSetChanged();

                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
