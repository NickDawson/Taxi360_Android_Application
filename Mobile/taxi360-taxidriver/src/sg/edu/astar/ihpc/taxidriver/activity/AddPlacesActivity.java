package sg.edu.astar.ihpc.taxidriver.activity;

import java.io.IOException;
import java.util.List;

import sg.edu.astar.ihpc.taxidriver.R;
import sg.edu.astar.ihpc.taxidriver.entity.Driver;
import sg.edu.astar.ihpc.taxidriver.entity.DriverDestination;
import sg.edu.astar.ihpc.taxidriver.entity.Location;
import sg.edu.astar.ihpc.taxidriver.entity.Passenger;
import sg.edu.astar.ihpc.taxidriver.utils.AvailableDriver;
import sg.edu.astar.ihpc.taxidriver.utils.GPSTracker;
import sg.edu.astar.ihpc.taxidriver.utils.LocationUtil;
import sg.edu.astar.ihpc.taxidriver.utils.Server;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddPlacesActivity extends FragmentActivity {

	private GoogleMap gMap;
	private Geocoder geo;
Driver driver;
private LatLng loc;
private DriverDestination dd;
Context context;
Marker currM;
private String serverIP;
private android.location.Location currentlocation;
private LatLng curr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_places);
		setTitle("Add a favourite place");
		driver=(Driver) getIntent().getSerializableExtra("driver");
		geo = new Geocoder(this.getBaseContext());
		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gMap.setMyLocationEnabled(true);
		context=this;
	    GPSTracker  gps = new GPSTracker(AddPlacesActivity.this);

        // check if GPS enabled       
         if(gps.canGetLocation()){
        	  curr = new LatLng(gps.getLatitude(), gps.getLongitude());
        	
         }
         else{
        	 currentlocation= LocationUtil.getInstance(context).getCurrentLocation();
         	 curr = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
         }
		 
		 currM = gMap.addMarker(new MarkerOptions()
		                          .position(curr)
		                          .draggable(true));
		gMap.setOnMarkerDragListener(new MarkerDragListener());
		CameraUpdate center=
		        CameraUpdateFactory.newLatLng(new LatLng(curr.latitude,
		                                                 curr.longitude));
		    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
		gMap.moveCamera(center);
		gMap.animateCamera(zoom);
		loc = curr;
		serverIP =getResources().getString(R.string.server_ip);  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 The fuction allows the user to confirm once the location is set
	 **/
	public void add(View v) {
		
		Location temp=new Location(loc.latitude,loc.longitude);
		String url = serverIP + "taxi360-war/api/driverDestinations";
		
		dd= new DriverDestination();
		dd.setDriverid(driver);
		dd.setLocation(temp);
		dd.setLocationAddress(null);
		

		try {
			Server.getInstance().connect("POST", url, dd);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	 	   finish();
	}

	/**
	 The listner is called once the marker is moved
	 **/
	class MarkerDragListener implements OnMarkerDragListener {

		@Override
		public void onMarkerDrag(Marker marker) {
			
		}
		
		
		@Override
		public void onMarkerDragEnd(Marker marker) {
			try {
				AddPlacesActivity.this.loc = marker.getPosition();
			
			List<android.location.Address> address;
				address = AddPlacesActivity.this.geo.getFromLocation(loc.latitude, loc.longitude,1);
				marker.setTitle(address.get(0).getLocality());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onMarkerDragStart(Marker marker) {
			
		}
		
	}


}
