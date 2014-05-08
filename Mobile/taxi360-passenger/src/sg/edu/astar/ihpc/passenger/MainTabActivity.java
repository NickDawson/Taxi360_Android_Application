package sg.edu.astar.ihpc.passenger;

import sg.edu.astar.ihpc.passenger.entity.Passenger;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;




@SuppressWarnings("deprecation")
public class MainTabActivity extends android.app.TabActivity{
	
	Passenger passenger;
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_tab);
	        passenger = (Passenger) (getIntent().getSerializableExtra("Title"));
	        TabHost tabHost = getTabHost();
	         
	        // Tab for Photos
	        TabSpec photospec = tabHost.newTabSpec("NeedTaxi");
	        // setting Title and Icon for the Tab
	       photospec.setIndicator("NeedTaxi");
	        Intent photosIntent = new Intent(this, MainActivity.class);
	        photosIntent.putExtra("Title", passenger);
	        photospec.setContent(photosIntent);
	        
	        TabSpec relocate = tabHost.newTabSpec("Relocate");
	        // setting Title and Icon for the Tab
	       relocate.setIndicator("Relocate");
	        Intent relocateIntent = new Intent(this, RelocateMeActivity.class);
	        relocateIntent.putExtra("Passenger", passenger);
	        relocate.setContent(relocateIntent);
	       
	        TabSpec nearbytaxi = tabHost.newTabSpec("NearbyTaxi");
	        // setting Title and Icon for the Tab
	        nearbytaxi.setIndicator("NearbyTaxi");
	        Intent nearbytaxiIntent = new Intent(this, ViewTaxiActivity.class);
	        nearbytaxiIntent.putExtra("Passenger", passenger);
	        nearbytaxi.setContent(nearbytaxiIntent);
	        
	        tabHost.addTab(photospec);
	        tabHost.addTab(relocate);
	        tabHost.addTab(nearbytaxi);
	        
	        
	        
}
}
