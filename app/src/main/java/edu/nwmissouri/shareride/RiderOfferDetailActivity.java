package edu.nwmissouri.shareride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class RiderOfferDetailActivity extends AppCompatActivity {

    RideCollection rideCollection = new RideCollection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_offer_detail);

        TextView fromAddressET = (TextView)findViewById(R.id.fromAddressET);
        TextView toAddressET = (TextView)findViewById(R.id.toAddressET);
        Bundle bundle = getIntent().getExtras();

        //TODO here get the string stored in the string variable and do
        fromAddressET.setText(rideCollection.items.get(bundle.getInt("INDEX_LOCATION")).getRouteFrom());
        toAddressET.setText(rideCollection.items.get(bundle.getInt("INDEX_LOCATION")).getRouteTo());
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
        }
        else if (id == R.id.Edit_Ride_Offer)
        {
            Toast.makeText(this, "Navigating to Edit Ride Offer Screen!", Toast.LENGTH_SHORT).show();
           // Intent RideIntent = new Intent(this, MainActivity.class);
           // startActivity(RideIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

