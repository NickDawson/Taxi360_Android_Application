package sg.edu.astar.ihpc.taxidriver.activity;

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

import sg.edu.astar.ihpc.taxidriver.R;
import sg.edu.astar.ihpc.taxidriver.entity.Driver;
import sg.edu.astar.ihpc.taxidriver.entity.DriverDestination;
import sg.edu.astar.ihpc.taxidriver.utils.PlaceJSONParser;
import sg.edu.astar.ihpc.taxidriver.utils.PlacesAdapter;
import sg.edu.astar.ihpc.taxidriver.utils.Server;
import sg.edu.astar.ihpc.taxidriver.utils.SessionManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MyPlacesActivity extends Activity {
	AutoCompleteTextView atvPlaces;
	PlacesTask placesTask;
	ParserTask parserTask;
	Driver driver;
	private ObjectMapper mapper = new ObjectMapper();
	private ArrayList<DriverDestination> driverDestination;
	private String serverIP;
	private String type;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		setTitle("My Places");
		type = (String) getIntent().getSerializableExtra("type");
		driver = (Driver) getIntent().getSerializableExtra("driver");
		serverIP = getResources().getString(R.string.server_ip);
		Button add = (Button) findViewById(R.id.addNew);
		if (type.equalsIgnoreCase("goto"))
			add.setVisibility(View.GONE);
		String url = serverIP + "taxi360-war/api/driverDestinations/"
				+ driver.getId();
		try {
			Log.d("return = ", Server.getInstance().connect("GET", url) + "");
			if (Server.getInstance().connect("GET", url).getResponse() != null) {
				driverDestination = mapper.readValue(Server.getInstance()
						.connect("GET", url).getResponse(),
						new TypeReference<ArrayList<DriverDestination>>() {
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * atvPlaces.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { placesTask = new PlacesTask();
		 * placesTask.execute(s.toString()); }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { // TODO Auto-generated method stub }
		 * 
		 * @Override public void afterTextChanged(Editable s) { // TODO
		 * Auto-generated method stub } });
		 */
		if (driverDestination != null) {
			PlacesAdapter adapter = new PlacesAdapter(this,
					R.layout.activity_myplaces, driverDestination, type);

			ListView requestView = (ListView) findViewById(R.id.list);
			requestView.setAdapter(adapter);
		}
	}

	// A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "AIzaSyARMPXQPO0q7cle8epyN6lBHO82RTi9ubk";

			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			// Building the parameters to the web service
			String parameters = input + "&" + types + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			atvPlaces.setAdapter(adapter);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
		}
		return false;
	}

	public void gotoplace(View v) {
		if(type.equalsIgnoreCase("myplaces")){
    		
    		DriverDestination dd= (DriverDestination) v.getTag();
    		String url = serverIP + "taxi360-war/api/driverDestinations/"+dd.getId();
    		//dd.setDriverid(SessionManager.getInstance().getDriver());

    		try {
    			Server.getInstance().connect("DELETE", url);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}

    	}
		finish();
	}

	public void addPlace(View v) {

		Intent intent = new Intent(this, AddPlacesActivity.class);

		intent.putExtra("driver", driver);
		startActivity(intent);
		finish();
	}
}
