/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sg.edu.astar.ihpc.passenger.util;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.astar.ihpc.passenger.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location. To
 * track changes to the users location on the map, we request updates from the
 * {@link LocationClient}.
 */
public class LocationUtil 
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    private Context ctx;

    private LocationClient mLocationClient;
    private static LocationUtil instance;
    
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private LocationUtil(Context ctx){
    	this.ctx = ctx;
    	setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    public static synchronized LocationUtil getInstance(Context ctx) {
		if (instance == null)
			instance = new LocationUtil(ctx);
		return instance;
	}
    

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
            		ctx,
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    public Location getCurrentLocation() {
    	checkLocationServices();
    	
//       if (mLocationClient != null && mLocationClient.isConnected()) {
            return mLocationClient.getLastLocation();
//        }
//        return null;
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
        System.out.println("Util connected");
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }
    
    public void checkLocationServices(){
    	LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;
           if(lm==null)
               lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
           try{
           gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
           }catch(Exception ex){}
           try{
           network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
           }catch(Exception ex){}

          if(!network_enabled){
        	  AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
               dialog.setMessage("alert");
               dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                       // TODO Auto-generated method stub
                       Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                       ctx.startActivity(myIntent);
                       //get gps
                   }
               });
               dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                       // TODO Auto-generated method stub

                   }
               });
               dialog.show();

           }
    }
    String address;
    
    public String getaddress(sg.edu.astar.ihpc.passenger.entity.Location location){
		String url="http://maps.googleapis.com/maps/api/geocode/json?sensor=false&language=en&latlng=";
		JSONObject jsonObject=null;
		url=url+Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude());
		String reversegeocode=Server.getInstance()
				.connect("GET", url).getResponse();
		Log.d("reverse geocode",reversegeocode);
		try {
			jsonObject = new JSONObject(reversegeocode);
			
			 address=jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return address;
	}
}
