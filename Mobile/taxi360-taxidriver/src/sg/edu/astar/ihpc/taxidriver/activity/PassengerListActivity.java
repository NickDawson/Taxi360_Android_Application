package sg.edu.astar.ihpc.taxidriver.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import sg.edu.astar.ihpc.taxidriver.R;
import sg.edu.astar.ihpc.taxidriver.entity.Location;
import sg.edu.astar.ihpc.taxidriver.entity.Request;
import sg.edu.astar.ihpc.taxidriver.entity.Ride;
import sg.edu.astar.ihpc.taxidriver.utils.ListAdapter;
import sg.edu.astar.ihpc.taxidriver.utils.Prop;
import sg.edu.astar.ihpc.taxidriver.utils.Server;
import sg.edu.astar.ihpc.taxidriver.utils.SessionManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

/**
 *  Activity which is used to display the 
 *  list of passengers requested for a Cab in a List View
 *  Mohammed Althaf
	 * A0107629
 * */
public class PassengerListActivity extends Activity{
	private ArrayList<Request> myObjects;
	private DriverMainActivity drivermain;
	private Ride rideresponse;
	private ObjectWriter writer;
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Loading the Passenger List Screen
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle("Find a Passenger!!");
        myObjects=(ArrayList<Request>) this.getIntent().getSerializableExtra("array");
        Button add=(Button) findViewById(R.id.addNew);
       	add.setVisibility(View.GONE);
        ListAdapter adapter = new ListAdapter(this, R.layout.activity_passengerlist,myObjects);
        ListView requestView = (ListView)findViewById(R.id.list);
        requestView.setAdapter(adapter);
	}
	
	/**
	 * When driver accepts a request ,
	 * passenger is notified that the request
	 * is accepted and the ride is started
	 * 
	 */
		public void sendnotification(View v){
		Request req=(Request)v.getTag();
		Log.d("selected", req.getPassenger().getId().toString());
		
//		rideresponse=
				rideStart(req, new LatLng(req.getLocation().getLatitude(), req.getLocation().getLongitude()),(Location)this.getIntent().getSerializableExtra("mylocation"));
		
//		Intent intent = new Intent(this, RideStartActivity.class);
// 	   	intent.putExtra("ride", rideresponse);
// 	   	startActivity(intent);
// 	   	finish();
		}
		
		public Ride rideStart(Request req, LatLng loc, Location driverloc) {
			 Ride ride = new Ride();

			ride.setDriver(SessionManager.getInstance().getDriver());
			/*
			 * Passenger tempPassenger = new Passenger(); tempPassenger.setId(id);
			 */
			ride.setPassenger(req.getPassenger());
			ride.setPassengerKey(req.getPassenger().getAccesskey());
			if (driverloc != null) {
				ride.setDriverStartLocation(driverloc);
			} else {
				ride.setDriverStartLocation(driverloc);
			}
			ride.setPassengerStartLocation(new Location(loc.latitude, loc.longitude));
			writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
			try {
				String url = Prop.URL_START_RIDE;

				ride = mapper.readValue(Server.getInstance().connect("POST", url, ride)
								.getResponse(), Ride.class);
				Log.d("sondfkjsdn", ride.getId());
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Intent intent = new Intent(this, RideStartActivity.class);
	 	   	intent.putExtra("ride", ride);
	 	   	startActivity(intent);
	 	   	finish();
			return rideresponse;	
		}
		
		
}
