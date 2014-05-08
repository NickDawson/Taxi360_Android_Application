package sg.edu.astar.ihpc.passenger;

/**
 The Screen is used to see the cabs nearby by the passenger
 **/
/** Author :Hemanth **/
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;

import sg.edu.astar.ihpc.passenger.R;

import sg.edu.astar.ihpc.passenger.entity.AvailableDriver;
import sg.edu.astar.ihpc.passenger.entity.Location;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.Ride;
import sg.edu.astar.ihpc.passenger.util.CheckConnectivity;
import sg.edu.astar.ihpc.passenger.util.Response;
import sg.edu.astar.ihpc.passenger.util.Server;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewTaxiActivity extends Activity {

	protected static final long TIME_DELAY = 5000;

	// contacts JSONArray
	JSONArray requests = null;
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private Location myLocation;
	private Location driverGeo;
	private List<AvailableDriver> myObjects;
	ObjectMapper mapper = new ObjectMapper();
	Handler handler = new Handler();
	private Ride ride;
	private Ride rideresponse;
	private CompoundButton toggle;
	Button rides;
	Passenger passenger;
	Context context;
	CheckConnectivity connectivity;

	public GoogleMap getGoogleMap() {
		return googleMap;
	}

	public void setGoogleMap(GoogleMap googleMap) {
		this.googleMap = googleMap;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_taxi);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

		}

		connectivity = new CheckConnectivity();
		passenger = (Passenger) (getIntent().getSerializableExtra("Passenger"));

		context = this;

		initilizeMap();
		setUpMap();

		try {
			new ViewTaxiOperation().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The function is used to update the available drivers in the location
	 * continuously
	 **/
	
	
	Runnable updateTextRunnable = new Runnable() {
		public void run() {
			// Loading map

			// fetch current location of Driver

			googleMap.clear();
			googleMap.moveCamera(CameraUpdateFactory.newLatLng((new LatLng(
					passenger.getLocation().getLatitude(), passenger.getLocation().getLongitude()))));

			// Zoom in the Google Map
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
			googleMap.addMarker(new MarkerOptions()
		    .position(new LatLng( passenger.getLocation().getLatitude(), passenger.getLocation().getLongitude()))
		    .title(passenger.getName())
		    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
		    
		    );
			
			// form URL to call service
			String url = getResources().getString(R.string.availabledriverurl)
					+ passenger.getLocation().getLatitude().toString()
					+ "&log="
					+ passenger.getLocation().getLongitude().toString() + "&distance=10000";

			try {
				myObjects = mapper.readValue(
						Server.getInstance().connect("GET", url).getResponse(),
						new TypeReference<List<AvailableDriver>>() {
						});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			// setting markers
			for (int i = 0; i < myObjects.size(); i++) {
				Log.d("marker", myObjects.get(i).getDriver().getId().toString());
				HashMap<Integer, AvailableDriver> mapRequest = new HashMap<Integer, AvailableDriver>();
				mapRequest.put(i, myObjects.get(i));
				googleMap
						.addMarker(new MarkerOptions().position(
								new LatLng(myObjects.get(i).getLocation()
										.getLatitude(), myObjects.get(i)
										.getLocation().getLongitude()))
								.title(myObjects.get(i).getDriver().getId()
										.toString()));
			}
			// googleMap.setOnMarkerClickListener(this);

			handler.postDelayed(this, 30000);
		}
	};

	/**
	 * The function is used to setup the map
	 **/

	private void setUpMap() {
		

		// set map type
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		


		// Show the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng((new LatLng(
				passenger.getLocation().getLatitude(), passenger.getLocation().getLongitude()))));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
		// googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,
		// longitude)).title("driver").visible(false));
		// return latLng;
		googleMap.addMarker(new MarkerOptions()
	    .position(new LatLng( passenger.getLocation().getLatitude(), passenger.getLocation().getLongitude()))
	    .title(passenger.getName())
	    .snippet("and snippet")
	    );
		Log.d("de", "de 1");
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 **/
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
	private class ViewTaxiOperation extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			
			handler.post(updateTextRunnable);
			return null;
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

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
        	
        	Toast.makeText(getApplicationContext(), "Please set the destination in main tab",
 				   Toast.LENGTH_LONG).show();
        	
        	
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
	

}
