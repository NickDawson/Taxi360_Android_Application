package sg.edu.astar.ihpc.taxidriver.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import sg.edu.astar.ihpc.taxidriver.entity.Location;
import sg.edu.astar.ihpc.taxidriver.entity.Request;
import sg.edu.astar.ihpc.taxidriver.entity.Ride;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class CommonUtils {
	
	Ride ride;
	private ObjectWriter writer;
	private Ride rideresponse;
	private ObjectMapper mapper;
	
	public Ride rideStart(Request req, LatLng loc,Location driverloc) {
		
		ride = new Ride();
		
		
		//ride.setDriver(driverset());
		/*Passenger tempPassenger = new Passenger();
		tempPassenger.setId(id);*/
		ride.setPassenger(req.getPassenger());
		ride.setPassengerKey(req.getPassenger().getAccesskey());
		if(driverloc!=null){
		ride.setDriverStartLocation(driverloc);
		}
		
		ride.setPassengerStartLocation(new Location(loc.latitude,loc.longitude));
		writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String url = "http://137.132.247.133:8080/taxi360-war/api/ride";

			rideresponse = mapper.readValue(
					Server.getInstance().connect("POST", url, ride)
							.getResponse(), Ride.class
					);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rideresponse;
	}

}
