package sg.edu.astar.ihpc.passenger;
/**
The screen allows the user to relocate himself in the map
**/
/** Author :Thilak **/
import java.io.IOException;
import java.util.List;

import sg.edu.astar.ihpc.passenger.entity.Location;
import sg.edu.astar.ihpc.passenger.entity.Passenger;

import sg.edu.astar.ihpc.passenger.util.LocationUtil;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RelocateMeActivity extends FragmentActivity {
	private GoogleMap gMap;
	private Geocoder geo;
Passenger passenger;
private LatLng loc;
Context context;
Marker currM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relocate_me);
		
		passenger=(Passenger)(getIntent().getSerializableExtra("Passenger"));
		geo = new Geocoder(this.getBaseContext());
		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		context=this;
		LocationUtil  gps = LocationUtil.getInstance(context);
		LatLng curr;
        // check if GPS enabled       
       try{
		  curr = new LatLng(gps.getCurrentLocation().getLatitude(), gps.getCurrentLocation().getLongitude());
       }catch(NullPointerException e){
    	   curr = new LatLng(1.3, 103.22);
    	   
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
		  
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
	/**
	 The fuction allows the user to confirm once the location is set
	 **/
	public void relocate(View v) {
		//loc=currM.getPosition();
		Location temp=new Location(loc.latitude,loc.longitude);
		passenger.setLocation(temp);
		passenger.setRelocated(true);
		Intent intent = new Intent(context, MainTabActivity.class);
		
	 	   intent.putExtra("Title",passenger);
	 	   startActivity(intent);
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
				RelocateMeActivity.this.loc = marker.getPosition();
			
			List<android.location.Address> address;
				address = RelocateMeActivity.this.geo.getFromLocation(loc.latitude, loc.longitude,1);
				marker.setTitle(address.get(0).getLocality());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}

	
		
		
		@Override
		public void onMarkerDragStart(Marker marker) {
			
		}
		
		
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
