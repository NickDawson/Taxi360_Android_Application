package sg.edu.astar.ihpc.passenger;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.PassengerDestination;
import sg.edu.astar.ihpc.passenger.util.PlacesAdapter;
import sg.edu.astar.ihpc.passenger.util.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MyPlacesActivity extends Activity{
	
	
    Passenger passenger;
    private ObjectMapper mapper = new ObjectMapper();
    private ArrayList<PassengerDestination> passengerDestination;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        passenger=(Passenger) getIntent().getSerializableExtra("passenger");
        
        String url = getResources().getString(R.string.destinationurl)
				+passenger.getId();
        try {
			Log.d("return = ", Server.getInstance().connect("GET", url)+"");
			if(Server.getInstance()
					.connect("GET", url).getResponse()!=null)
			{
				passengerDestination = mapper.readValue(Server.getInstance()
					.connect("GET", url).getResponse(),
					new TypeReference<ArrayList<PassengerDestination>>() {
					});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
       
        /*atvPlaces.addTextChangedListener(new TextWatcher() {
        	 
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }
 
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
                // TODO Auto-generated method stub
            }
 
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });*/
        if(passengerDestination!=null){
        PlacesAdapter adapter = new PlacesAdapter(this, R.layout.activity_myplaces,passengerDestination);
        
        ListView requestView = (ListView)findViewById(R.id.list);
        requestView.setAdapter(adapter);
        }
	}
	
	
	    
	       public void add(View v){
	        	Intent intent=new Intent(this,AddPlacesActivity.class);
	        	
	        	intent.putExtra("passenger", passenger);
	        	startActivity(intent);
	        	finish();
	        	
	           
	           
	    }
	
}