package sg.edu.astar.ihpc.passenger;

/**
 The Screen is called once the request is accepted by the taxi driver the location of the driver is updated in map continuously
 **/
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import sg.edu.astar.ihpc.passenger.entity.AvailableDriver;
import sg.edu.astar.ihpc.passenger.entity.Location;
import sg.edu.astar.ihpc.passenger.entity.Request;
import sg.edu.astar.ihpc.passenger.entity.Ride;
import sg.edu.astar.ihpc.passenger.util.CheckConnectivity;

import sg.edu.astar.ihpc.passenger.util.LocationUtil;
import sg.edu.astar.ihpc.passenger.util.Response;
import sg.edu.astar.ihpc.passenger.util.Server;
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
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class RideActivity extends Activity {
	JSONArray requests = null;
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private Location myLocation;
	private Location driverGeo;
	private List<Request> myObjects;
	ObjectMapper mapper = new ObjectMapper();
	Handler handler = new Handler();
	private Ride ride;
	private CompoundButton toggle;
	Button rides;
	AlertDialog.Builder alertDialogBuilder;
	CheckConnectivity connectivity;
	Context context;
	AvailableDriver ad;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride);
		
		
		context = this;
		connectivity = new CheckConnectivity();
		initilizeMap();
		this.driverGeo = setUpMap();
		ad = new AvailableDriver();
		ride = new Ride();
		ride.setId(getIntent().getStringExtra("ride"));
		try {
			new rideoperation().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	private class rideoperation extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			ride = getride(ride);
			handler.post(updateTextRunnable);
			return null;
			
		}
		@Override
	    protected void onPostExecute(String result) {
	      
	    }

	}
	/**
	 * The function is used to get the ride details for the particular request
	 **/
	public Ride getride(Ride ride) {
		String url = getResources().getString(R.string.rideurl)
				+ ride.getId();
		try {
			ride = mapper.readValue(Server.getInstance().connect("GET", url)
					.getResponse(), Ride.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ride;
	}

	/**
	 * The function is used to continuously update the location of the driver in
	 * the map
	 **/

	Runnable updateTextRunnable = new Runnable() {
		public void run() {
			// Loading map

			// fetch current location of Driver
			
            ride=getride(ride);
			googleMap.clear();
			googleMap.addMarker(new MarkerOptions()
		    .position(new LatLng( ride.getPassengerStartLocation().getLatitude(), ride.getPassengerStartLocation().getLongitude()))
		    .title(ride.getPassenger().getName())
		    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
		    
		    );
			Log.d("geo", ride.getDriver().getId().toString());
			// form URL to call service
			/*String url = "http://137.132.247.133:8080/taxi360-war/api/availabledriver/"
					+ ride.getDriver().getId().toString();
			Log.d("taxi number", url);
			try {
				ad = mapper.readValue(Server.getInstance().connect("GET", url)
						.getResponse(), AvailableDriver.class);

			}catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			}  catch (IOException e) {
				e.printStackTrace();
			}*/
			// setting markers
            geteta();
			Marker m=googleMap.addMarker(new MarkerOptions().position(
					new LatLng(ride.getPassengerEndLocation().getLatitude(), ride.getPassengerEndLocation().getLongitude())).title(ride.getDriver().getLicenseid()+ "   ETA : "+ eta));
			m.showInfoWindow();
			handler.postDelayed(this, 30000);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ride, menu);
		return true;
	}

	private void initilizeMap() {
		Log.d("de", "de 1");
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * The function is used to set up the map for the view
	 **/
	private Location setUpMap() {
		// Enable MyLocation Layer of Google Map
		Log.d("de", "de 2");
		//googleMap.setMyLocationEnabled(true);

		// Get LocationManager object from System Service LOCATION_SERVICE
		this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//
		// // Create a criteria object to retrieve provider
		// Criteria criteria = new Criteria();
		//
		//
		// // Get the name of the best provider
		// String provider = locationManager.getBestProvider(criteria, true);
		//
		// // Get Current Location
		// android.location.Location driverLocation=
		// locationManager.getLastKnownLocation(provider);

		// set map type
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		double lat = 0.0;
		double lon = 0.0;

		LocationUtil gps = LocationUtil.getInstance(this);

		// check if GPS enabled
	

			lat = gps.getCurrentLocation().getLatitude();
			lon = gps.getCurrentLocation().getLongitude();
			// url =
			// "http://137.132.247.133:8080/taxi360-war/webresources/generic?lat="+lat+"&lng="+lon+"&radius=1000000";
			// \n is for new line

		

		// Create a LatLng object for the current location
		this.myLocation = new Location(lat, lon);

		// Show the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory
				.newLatLng((new LatLng(lat, lon))));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
		// googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,
		// longitude)).title("driver").visible(false));
		// return latLng;

		Log.d("driver", myLocation.getLatitude().toString());
		return myLocation;
	}

	/**
	 * The function is used to register the clientfor the notifications
	 **/
	
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
	
	String eta;
	
void geteta(){
	String url="http://maps.googleapis.com/maps/api/distancematrix/json?origins="+ride.getPassengerEndLocation().getLatitude()+","+ride.getPassengerEndLocation().getLongitude()+"&destinations="+ride.getPassengerStartLocation().getLatitude()+","+ride.getPassengerStartLocation().getLongitude()+"&mode=driving&language=en-EN&sensor=false";
	JSONObject jsonObject = null;
	eta="NA";
	//url=url+Double.toString(loc.latitude)+","+Double.toString(loc.longitude);
				String reversegeocode=Server.getInstance()
						.connect("GET", url).getResponse();
				Log.d("reverse geocode",reversegeocode);
				try {
					jsonObject = new JSONObject(reversegeocode);
					eta=jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
					Log.d("duration adress",jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
}
