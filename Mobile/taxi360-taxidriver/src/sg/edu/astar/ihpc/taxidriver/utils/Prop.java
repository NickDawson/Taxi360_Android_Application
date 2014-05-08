package sg.edu.astar.ihpc.taxidriver.utils;

import sg.edu.astar.ihpc.taxidriver.R;

public class Prop {
	private String serverIP;
	
	public static String HOST = "http://192.168.237.176:8080/taxi360-war/api";
	 public static String URL_GET_REQUESTS = HOST+"/request";
	    public static String URL_AVAILABLEDRIVER_ON = HOST+"/availabledriver/on";
	    public static String URL_AVAILABLEDRIVER_OFF = HOST+"/availabledriver/off";
	    public static String URL_START_RIDE= HOST+"/ride";
	    public static String URL_UPDATEDRIVER_LOCATION= HOST+"/availabledriver";
	    public static String URL_ADD_DRIVERDESTINATION= HOST+"/driverDestinations";
	    public static String URL_DRIVER_LOGOUT= HOST+"auth/dlogout";
	    public static String URL_DRIVER_LOGIN= HOST+"auth/driver";
	    public static String URL_DRIVER_DESTINATION= HOST+"/passenger/accesscontrol/driverDestinations/";
	    public static String URL_PASSENGER_RATING= HOST+"/ride/passengerRating";
	    public static String URL_REGISTER_DRIVER= HOST+"/driver";
	    public static String URL_RIDE_COMPLETED= HOST+"/ride/end";
	    public static String URL_RIDE_UPDATEDRIVERLOC= HOST+"/ride/updateDriverLoc";
	    public static String URL_RIDE_REACHEDPASSENGER= HOST+"/ride/reachedPassenger";
	    public static String URL_RIDE_PASSENGERNOTFOUND= HOST+"/ride/passengerNotFound";
}
