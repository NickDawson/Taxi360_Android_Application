package sg.edu.astar.ihpc.passenger;

import java.io.IOException;
import java.util.List;

import sg.edu.astar.ihpc.passenger.entity.Location;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.PassengerDestination;

import sg.edu.astar.ihpc.passenger.util.LocationUtil;
import sg.edu.astar.ihpc.passenger.util.Server;


import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
Passenger passenger;
private LatLng loc;
private PassengerDestination dd;
Context context;
Marker currM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_places);
		
		passenger=(Passenger) getIntent().getSerializableExtra("passenger");
		geo = new Geocoder(this.getBaseContext());
		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		context=this;
		     
       
		final LatLng curr = new LatLng(passenger.getLocation().getLatitude(), passenger.getLocation().getLongitude());
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
		  
	}

	
	/**
	 The fuction allows the user to confirm once the location is set
	 **/
	public void add(View v) {
		
		Location temp=new Location(loc.latitude,loc.longitude);
		String url = getResources().getString(R.string.destinationurl);;
		dd= new PassengerDestination();
		dd.setPassengerid(passenger);
		dd.setLocation(temp);
		dd.setLocationAddress(null);
		

		try {
			Server.getInstance().connect("POST", url, dd);
		} catch (Exception e) {
			e.printStackTrace();
		}
			Intent intent = new Intent(context, MyPlacesActivity.class);
		
	 	   intent.putExtra("passenger",passenger);
	 	   startActivity(intent);
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
