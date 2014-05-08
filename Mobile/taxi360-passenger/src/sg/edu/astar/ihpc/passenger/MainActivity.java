package sg.edu.astar.ihpc.passenger;

/** Author :Hemanth **/
/**
 * this is the main screen for the passenger once the user logs in . This screen  allows to request a taxi and cancel the taxi request
 */
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.astar.ihpc.passenger.entity.Location;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.Request;
import sg.edu.astar.ihpc.passenger.util.CheckConnectivity;

import sg.edu.astar.ihpc.passenger.util.LocationUtil;
import sg.edu.astar.ihpc.passenger.util.Response;
import sg.edu.astar.ihpc.passenger.util.Server;
import sg.edu.astar.ihpc.passenger.util.SessionManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	TextView passengerAddress;
	Button button;
	TextView name;
	Passenger passenger;
	Button logout;
	Context context;
	AlertDialog.Builder alertDialogBuilder;
	CheckConnectivity connectivity;
	Handler handler = new Handler();
	Runnable refresh;
	
	private static final String PROPERTY_REG_ID = "regid";

	// This string will hold the lengthy registration id that comes
	// from GCMRegistrar.register()
	private String regId = "";

	
	Geocoder geocoder;
	List<Address> addresses;
	Request reqest;
	LocationUtil gps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		gps = LocationUtil.getInstance(this);
		getRegistrationId();
		 reqest= new Request();
		geocoder = new Geocoder(this.getBaseContext());
		
		//registerClient();
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button1);
		name = (TextView) findViewById(R.id.name);
		passenger = (Passenger) (getIntent().getSerializableExtra("Title"));
		context = this;
		SessionManager.setContext(context);
		SessionManager.getInstance().createLoginSession(passenger);
		Log.d("passid", passenger.toString());
		connectivity = new CheckConnectivity();
		checkrequest();
		passengerAddress = (TextView) findViewById(R.id.address);
		setlocation();
		
		name.setText("Welcome   "+passenger.getName().toString());

		alertDialogBuilder=new AlertDialog.Builder(
				context);
		 
		

		
	}

	
	/**
	 * The function takes care of request fot taxi and cancel the taxi request
	 */
	private void checkrequest() {
		try {
			new LongOperation().execute().get();
			Log.d("hi", "hi");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private class LongOperation extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			gps = LocationUtil.getInstance(context);
			String url = getResources().getString(R.string.requesturl)
					+ passenger.getId().toString();
			Response response = Server.getInstance().connect("GET", url);
			Log.d("requesttaxi", "Response =" + response.getResponse());
			if (Boolean.parseBoolean(response.getResponse())) {
				button.setText("Cancel Request");
				button.setBackgroundResource(R.drawable.ic_orange_button);
			}
			
			return "Need Taxi";
		}
		 @Override
		    protected void onPostExecute(String result) {
			
		    }

	}
public void setlocation(){
	String address="";
	if (!passenger.isRelocated()) {
		
		
		double lat = 0.0;
		double lon = 0.0;
		// check if GPS enabled
		
try{
			lat = gps.getCurrentLocation().getLatitude();
			lon = gps.getCurrentLocation().getLongitude();
			// url =
			// "http://137.132.247.133:8080/taxi360-war/webresources/generic?lat="+lat+"&lng="+lon+"&radius=1000000";
			// \n is for new line
}catch (NullPointerException e){
	
	 Intent relocateIntent = new Intent(this, RelocateMeActivity.class);
     relocateIntent.putExtra("Passenger", passenger);
    startActivity(relocateIntent);
    finish();
}
		
		Location l = new Location(lat, lon);
		passenger.setLocation(l);
	}
		
			
			
		
			
			 
		
			address=gps.getaddress(passenger.getLocation());
			
		
			passengerAddress.setText(address);

		

		
	}
 
	public void addListenerOnButton(View v) {

		


				if (!CheckConnectivity.isNetworkAvailable(context)
						&& (!connectivity.isConnectedToServer())) {
					connectivity.alertshow(context);
				} else {

					if (button.getText().toString() != "Cancel Request") {

						double lat, lon;

						

						
						

						Log.d("name", name.getText().toString());
						String url = getResources().getString(R.string.requesturl);
						try {
							reqest.setPassengerKey(regId);
							reqest.setLocation(passenger.getLocation());
							reqest.setPassenger(passenger);
							Server.getInstance()
									.connect("POST", url, reqest);
								name.setEnabled(false);
								button.setBackgroundResource(R.drawable.ic_orange_button);
								button.setText("Cancel Request");
								
							
						}

						catch (Exception e) {
							e.printStackTrace();

						}

					} else {

						// set title
						alertDialogBuilder.setTitle("Confirmation!!!");

						// set dialog message
						alertDialogBuilder
								.setMessage("Click yes to Cancel!")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity

												String url = getResources().getString(R.string.requesturl)
														+ passenger.getId();
												Server.getInstance()
														.connect("DELETE", url);
												button.setBackgroundResource(R.drawable.ic_green_button);	
													button.setText("Need Taxi");
													name.setEnabled(true);
												

											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// just close
												// the dialog box and do nothing
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();

						// show it
						alertDialog.show();

					}
				}

			

	}

	/**
	 * Attempts to register the client phone to the google server
	 */
	
	@Override
	protected void onPause() {

		
		super.onPause();
	}

	// When an activity is resumed, be sure to register any
	// broadcast receivers with the appropriate intent
	@Override
	protected void onResume() {
		super.onResume();
		

	}

	// There are no menus for this demo app. This is just
	// boilerplate code.

	// NOTE the call to GCMRegistrar.onDestroy()
	@Override
	public void onDestroy() {

		

		super.onDestroy();
	}

	/**
	 * The button allows to relocate the user to a noew location
	 */
	public void relocateme(View v) {
		handler.removeCallbacks(refresh);
		Intent intent = new Intent(context, RelocateMeActivity.class);
		intent.putExtra("Passenger", passenger);
		startActivity(intent);
	}

	/**
	 * The button allows the user to view the taxi drivers around
	 */
	public void viewtaxiaround(View v) {

		Intent intent = new Intent(context, ViewTaxiActivity.class);
		intent.putExtra("Passenger", passenger);
		startActivity(intent);
	}

	
	public void destinationset() {
		Intent intent = new Intent(MainActivity.this,BookActivity.class);
		intent.putExtra("Passenger", passenger);
	    startActivityForResult(intent,90);
		
	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	    case 90:
	        if (resultCode == RESULT_OK) {
	        	Bundle res = data.getExtras();
	            reqest.setDestination(res.getString("param_result"));
	            Log.d("FIRST", "result:"+reqest.getDestination());
	        }
	        break;
	    }
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.menu_editprofile:
        	if(passenger.getRegistrationtype().equals("facebook")|| passenger.getRegistrationtype().equals("google")){
        		
        		Toast.makeText(getApplicationContext(), "Cannot Edit Social Media Profile",
        				   Toast.LENGTH_LONG).show();
        	}
        	
        	else{
        	Intent intent = new Intent(context,EditProfileActivity.class);
    		intent.putExtra("Passenger", passenger);
    		startActivity(intent);}
            
            return true;
        case R.id.menu_logout:
        	Intent intent1 = new Intent(context,LoginActivity.class);
    		//intent.putExtra("Passenger", passenger);
    		startActivity(intent1);
            
            return true;
        case R.id.menu_destination:
        	
        	destinationset();
        	
        	
        	return true;
        
        	
        case R.id.menu_addplaces:
        	
        	Intent intent2 = new Intent(
					context,
					MyPlacesActivity.class);
			intent2.putExtra("passenger", passenger);
			startActivity(intent2);
			
			return true;
         
        default:
            return super.onOptionsItemSelected(item);
        }
    }  
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    switch(keyCode)
	    {
	        case KeyEvent.KEYCODE_BACK:
	        	Intent i = new Intent();
	        	i.setAction(Intent.ACTION_MAIN);
	        	i.addCategory(Intent.CATEGORY_HOME);
	        	this.startActivity(i);
	           Log.d("back", Boolean.toString(moveTaskToBack(true)));

	            return true;
	    }
	    return false;
	}
	
	
	 
	private String getRegistrationId() {
		final SharedPreferences prefs = getSharedPreferences("regid",
	            Context.MODE_PRIVATE);
	    regId = prefs.getString(PROPERTY_REG_ID, "");
	    Log.d("registration",regId );
	    if (regId.isEmpty()) {
	        //Log.i(TAG, "Registration not found.");
	        return "";
	    }
		return regId;
	}
}
