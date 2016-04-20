package edu.nwmissouri.shareride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;

public class RiderOfferDetailActivity extends AppCompatActivity {

    RideCollection rideCollection = new RideCollection();
    TextView fromAddressET;
    TextView toAddressET;
    TextView availabilityET;
    TextView hrsSpinner;
    TextView frequencySpinner;
    TextView offerId;

    Client kinveyClient;
    private String appKey = "kid_ZJCDL-Jpy-";
    private String appSecret = "7ba9e5e0015849b790845e669ab87992";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_offer_detail);
        kinveyClient = new Client.Builder(appKey, appSecret
                , getApplicationContext()).build();
        fromAddressET = (TextView) findViewById(R.id.fromET);
        toAddressET = (TextView) findViewById(R.id.ToET);
        availabilityET = (TextView) findViewById(R.id.offerAvailabilityET);
        hrsSpinner = (TextView) findViewById(R.id.offertimeSpinner);
        frequencySpinner = (TextView) findViewById(R.id.offerFrequencySpinner);
        offerId = (TextView) findViewById(R.id.offerIDTV);
        Bundle bundle = getIntent().getExtras();
        Ride rideObject = rideCollection.getRideObject(bundle.getString("INDEX_LOCATION"));
        //TODO here get the string stored in the string variable and do
        if (rideObject != null) {
            fromAddressET.setText(rideObject.getRouteFrom());
            toAddressET.setText(rideObject.getRouteTo());
            availabilityET.setText(rideObject.getNoOfAvailability());
            hrsSpinner.setText(rideObject.getTimeOfTravel());
            frequencySpinner.setText(rideObject.getFrequency());
            offerId.setText(bundle.getString("INDEX_LOCATION").toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ride_offer_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.Edit_Ride_Offer) {
            //Toast.makeText(this, "Navigating to Edit Ride Offer Screen!", Toast.LENGTH_SHORT).show();
            Intent RideEditIntent = new Intent(this, RideOfferEditActivity.class);
            RideEditIntent.putExtra("OFFER_ID", offerId.getText());
            startActivity(RideEditIntent);
            return true;
        }else if (id == R.id.logout) {
            kinveyClient.user().logout().execute();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);

        } else if(id == R.id.stats){
            Intent stats_intent=new Intent(getApplicationContext(),Statistics.class);
            startActivity(stats_intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

