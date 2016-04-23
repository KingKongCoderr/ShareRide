package edu.nwmissouri.shareride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import static edu.nwmissouri.shareride.MainActivity.autocomplete;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    private static final String LOG_TAG = " Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyA36XH-x4gJIQvAc7p7LHN55xm_oDVEkhw";
    private String fromLatLong = "";
    private String toLatLong = "";

    //Kinvey Details
    public static final String TAG = "ShareRideKinvey";
    private String appKey="kid_Z10tuBjTCx";
    private String appSecret="41f0212a449c472fab5e423f7c225f98";
    private Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionbar = getSupportActionBar();

        // Enable the Up button
        actionbar.setDisplayHomeAsUpEnabled(true);
        AutoCompleteTextView fromET = (AutoCompleteTextView) findViewById(R.id.fromET);
        AutoCompleteTextView toET = (AutoCompleteTextView) findViewById(R.id.ToET);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button searchBTN = (Button) findViewById(R.id.searchBTN);

        fromET.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.places_result));
        fromET.setOnItemClickListener(this);

        toET.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.places_result));
        toET.setOnItemClickListener(this);

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fromET = (EditText) findViewById(R.id.fromET);
                EditText toET = (EditText) findViewById(R.id.ToET);

                String fromStr = fromET.getText().toString();
                fromLatLong = getLatLongFromGivenAddress(fromStr);
                String[] fromLatLongArrays = fromLatLong.split(",");
                String toStr = toET.getText().toString();
                toLatLong = getLatLongFromGivenAddress(toStr);
                String[] toLatLongArrays = toLatLong.split(",");
                Double[] fromArray = new Double[fromLatLongArrays.length];
                Double[] toArray = new Double[toLatLongArrays.length];

                if(fromLatLongArrays.length <= 1 || toLatLongArrays.length <= 1)
                {
                    Toast.makeText(getBaseContext(),"Retry! not a valid input",Toast.LENGTH_SHORT).show();
                }
                else {

                    for (int i = 0; i < fromLatLongArrays.length; i++) {
                        fromArray[i] = Double.parseDouble(fromLatLongArrays[i]);
                    }

                    for (int i = 0; i < toLatLongArrays.length; i++) {
                        toArray[i] = Double.parseDouble(toLatLongArrays[i]);
                    }

                    float [] dist = new float[1];
                    Location.distanceBetween(fromArray[0]/1e6,fromArray[1]/1e6,toArray[0]/1e6,toArray[1]/1e6,dist);
                    String resultValue = String.format("%f", dist[0]*621.371192f);
                    TextView distanceTV = (TextView) findViewById(R.id.distanceTV);

                 distanceTV.setText("Distance is: " + resultValue + " miles");
                }
            }
        });

        kinveyClient = new Client.Builder(appKey, appSecret
                , this.getApplicationContext()).build();

        if (!kinveyClient.user().isUserLoggedIn()) {
            kinveyClient.user().login(new KinveyUserCallback() {
                @Override
                public void onSuccess(User result) {
                    Log.i(TAG, "Logged in to Kinvey successfully!" + result.getId());
                }

                @Override
                public void onFailure(Throwable error) {
                    Log.e(TAG, "Login Failure", error);
                }
            });
        }
    }


    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        String distance = getLatLongFromGivenAddress(str);
        Toast.makeText(this, distance.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * This method gets the latitude and longitude of the given address
     * @param address - holds the string value of the address
     * @return - returns latitude,longitude
     */

    public String getLatLongFromGivenAddress(String address)
    {
        Geocoder coder = new Geocoder(getBaseContext(),Locale.getDefault());
        String strLongitude = "";
        String strLatitude = "";
        StringBuilder latLongResult = new StringBuilder();
        int count = 0;
        try {
            List<Address> list = coder.getFromLocationName(address, 1);
            while (count < 10 && list.size() == 0) {
                list = (ArrayList<Address>) coder.getFromLocationName(address,1);
                count++;
            }
            if(list.size()>0) {
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

    /**
     * This method allows autofill of address in from and return addresses
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent profile_intent=new Intent(getApplicationContext(),Profile_settings.class);
            startActivity(profile_intent);
            return true;
        }else if (id == R.id.logout) {
            kinveyClient.user().logout().execute();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);

        } else if(id == R.id.stats){
            Intent stats_intent=new Intent(getApplicationContext(),StatisticsActivity.class);
            startActivity(stats_intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable
{
    private ArrayList resultList;
    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId)
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

