//This Class is used for checking the connectivity with the internet and used for all the HTTP requests send
//Author:Hemanth
package sg.edu.astar.ihpc.passenger.util;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import sg.edu.astar.ihpc.passenger.MainActivity;
import sg.edu.astar.ihpc.passenger.MainTabActivity;
import sg.edu.astar.ihpc.passenger.R;
import sg.edu.astar.ihpc.passenger.Rating;
import sg.edu.astar.ihpc.passenger.RideActivity;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.Ride;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

public class CheckConnectivity extends BroadcastReceiver {
	Context contexts;
	private String broadcastMessage = "No broadcast message";
	String ids="";
	Passenger passenger;
	ObjectMapper mapper = new ObjectMapper();
	public static boolean isNumeric(String str)
	{
	    return str.matches("-?\\d+(.\\d+)?");
	}
	// This is called whenever there is a lost connection
	@Override
	public void onReceive(final Context context, Intent intent) {
this.contexts=context;
		broadcastMessage = intent.getExtras().getString("message");

		if (broadcastMessage != null) {
			// display our received message
			if (!isNumeric(broadcastMessage)) {
				createNotification(context, broadcastMessage);
				
			} 
			
			
			else {
				ids=broadcastMessage;
				Log.d("gcm", broadcastMessage);
				
				final SharedPreferences prefs = context.getSharedPreferences("rideid",
			            Context.MODE_PRIVATE);
			    SharedPreferences.Editor editor = prefs.edit();
			    editor.putString("rideid", broadcastMessage);
			    editor.commit();
				createNotification(context, "Driver Accepted");


						
								

					
			}
		}}
			public void createNotification(Context context, String payload) {
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(R.drawable.icon,
						"Message received", System.currentTimeMillis());
				// hide the notification after its selected
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				Intent intent;
				final SharedPreferences prefs = context.getSharedPreferences("rideid",
			            Context.MODE_PRIVATE);
			    String rideid = prefs.getString("rideid", "");
			    getride(rideid);
				if (!payload.equals("Ride Finished")){
					if(ids.equals("")){
						
						 }
					if(payload.equals("Sorry, the taxi was not able to find you.")){ 
					intent = new Intent(context, MainTabActivity.class);
					intent.putExtra("Title", ride.getPassenger());}else{
				 intent = new Intent(context, RideActivity.class);
				intent.putExtra("ride", rideid);}}
				else
				{
					 intent = new Intent(context,
							Rating.class);
					intent.putExtra("ride", ride);
					
				}
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
						intent, PendingIntent.FLAG_CANCEL_CURRENT);
				notification.setLatestEventInfo(context, "Message", payload,
						pendingIntent);
				notificationManager.notify(0, notification);

			}
	// Used to check whether the network is available
	public static boolean isNetworkAvailable(Context context) {
		return ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo() != null;
	}

	// Used to show the alert window whenever error occurs
	public void alertshow(Context context) {
		AlertDialogBuilder.alertshow(context, "Error",
				"Internet Connection lost cannot render your request", "OK");
	}

	// Used to check whether able to connect to server
	public boolean isConnectedToServer() {
		return Server.getInstance().isConnectedToServer();
	}
	Ride ride;
	public Ride getride(String rideid) {
		ride=new Ride();
		
		String url = contexts.getResources().getString(R.string.rideurl)
				+ rideid;
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

}
