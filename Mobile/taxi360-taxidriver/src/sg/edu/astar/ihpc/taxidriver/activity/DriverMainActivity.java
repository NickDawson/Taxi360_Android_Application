package sg.edu.astar.ihpc.taxidriver.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import sg.edu.astar.ihpc.taxidriver.R;
import sg.edu.astar.ihpc.taxidriver.entity.Driver;
import sg.edu.astar.ihpc.taxidriver.entity.Location;
import sg.edu.astar.ihpc.taxidriver.entity.Passenger;
import sg.edu.astar.ihpc.taxidriver.entity.Request;
import sg.edu.astar.ihpc.taxidriver.entity.Ride;
import sg.edu.astar.ihpc.taxidriver.utils.AvailableDriver;
import sg.edu.astar.ihpc.taxidriver.utils.LocationUtil;
import sg.edu.astar.ihpc.taxidriver.utils.Prop;
import sg.edu.astar.ihpc.taxidriver.utils.RectView;
import sg.edu.astar.ihpc.taxidriver.utils.RequestDensity;
import sg.edu.astar.ihpc.taxidriver.utils.Server;
import sg.edu.astar.ihpc.taxidriver.utils.SessionManager;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Activity which is used to display Home Screen of the Driver where the driver
 * is able to find all the passengers who have requested for taxi Mohammed
 * Althaf A0107629
 * */

@SuppressLint("NewApi")
public class DriverMainActivity extends Activity implements
		OnMarkerClickListener, OnInfoWindowClickListener {

	protected static final long TIME_DELAY = 20000;

	// contacts JSONArray

	private GoogleMap googleMap;
	private LocationManager locationManager;
	private Location myLocation;
	private Location driverGeo;
	private List<Request> myObjects;
	private List<LatLng> reqLatLng = new ArrayList<LatLng>();
	private ArrayList<Request> myObject;
	private ObjectMapper mapper = new ObjectMapper();
	private Handler handler = new Handler();
	private Ride ride;
	private Ride rideresponse;
	private CompoundButton toggle;
	private org.codehaus.jackson.map.ObjectWriter writer;
	private Runnable updateTextRunnable;
	private View v = null;
	private VisibleRegion visibleRegion;
	private String serverIP;
	private Button passengerList;
	private Passenger passenger;
	private AvailableDriver ad;
	private Driver driver;
	private Context context;

	private HashMap<Integer, Request> mapRequest;
	// array indices start from top right(0) in anti-clockwise
	private RectView rectView[] = new RectView[8];

	/**
	 * Loading the Driver Home Screen
	 * 
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SessionManager.setContext(this);
		if (!SessionManager.getInstance().checkLogin())
			return;
		else {
			setContentView(R.layout.driver_activity_main);
			serverIP = getResources().getString(R.string.server_ip);
			final FrameLayout layout = (FrameLayout) findViewById(R.id.topLayout);
			ViewTreeObserver vto = layout.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					int width = layout.getWidth();
					int height = layout.getHeight();
					int y = (int) (height - (height * 0.01));
					int x = (int) (width - (width * 0.01));
					rectView[0] = new RectView(DriverMainActivity.this,
							width / 2, 0, width, (int) (height * 0.01));
					rectView[1] = new RectView(DriverMainActivity.this, 0, 0,
							width / 2, (int) (height * 0.01));
					rectView[2] = new RectView(DriverMainActivity.this, 0, 0,
							(int) (width * 0.01), height / 2);
					rectView[3] = new RectView(DriverMainActivity.this, 0,
							height / 2, (int) (width * 0.01), height);
					rectView[4] = new RectView(DriverMainActivity.this, 0, y,
							width / 2, height);
					rectView[5] = new RectView(DriverMainActivity.this,
							width / 2, y, width, height);
					rectView[6] = new RectView(DriverMainActivity.this, x,
							height / 2, width, height);
					rectView[7] = new RectView(DriverMainActivity.this, x, 0,
							width, height / 2);
					layout.addView(rectView[0]);
					layout.addView(rectView[1]);
					layout.addView(rectView[2]);
					layout.addView(rectView[3]);
					layout.addView(rectView[4]);
					layout.addView(rectView[5]);
					layout.addView(rectView[6]);
					layout.addView(rectView[7]);
					layout.getViewTreeObserver().removeGlobalOnLayoutListener(
							this);
				}
			});
			setTitle("Find a Passenger!!");

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);

			}
			initialize();
			start();
		}
	}

	/*
	 * private void getRequestsPerDirection() { // TODO Auto-generated method
	 * stub LatLng center = googleMap.getCameraPosition().target; visibleRegion
	 * = googleMap.getProjection() .getVisibleRegion(); Point upperRight =
	 * googleMap.getProjection().toScreenLocation(visibleRegion.farRight); if (
	 * pointNotEmpty(upperRight) ) { Point upperLeft =
	 * googleMap.getProjection().toScreenLocation(visibleRegion.farLeft); Point
	 * midPoint = new Point((upperRight.x + upperLeft.x)/2 , (upperRight.y +
	 * upperLeft.y)/2); LatLng midLatLng =
	 * googleMap.getProjection().fromScreenLocation(midPoint); LatLngBounds
	 * viewBound = googleMap.getProjection().getVisibleRegion().latLngBounds;
	 * Log.d("mid", midLatLng.toString()); float[] results = new float[1];
	 * android.location.Location.distanceBetween(center.latitude,
	 * center.longitude, midLatLng.latitude, midLatLng.longitude, results);
	 * float radius = results[0] + 3000; String url =
	 * "http://137.132.247.133:8080/taxi360-war/api/request/range?clat="
	 * +center.latitude +"&clog=" + center.longitude +"&minlat=" +
	 * viewBound.southwest.latitude +"&minlog=" + viewBound.southwest.longitude
	 * +"&maxlat=" + viewBound.northeast.latitude +"&maxlog=" +
	 * viewBound.northeast.longitude +"&radius=" + radius; try { String response
	 * = Server.getInstance().connect("GET", url).getResponse(); if ( response
	 * != null ) { List<RequestDensity> req = mapper.readValue(response, new
	 * TypeReference<List<RequestDensity>>() {}); Log.d("sfsf", req.toString());
	 * setColorScale(req); } } catch(Exception e) { e.printStackTrace(); } } }
	 */

	private void updateColorScale() {
		LatLngBounds viewBound = googleMap.getProjection().getVisibleRegion().latLngBounds;
		LatLng center = googleMap.getCameraPosition().target;
		List<RequestDensity> reqDen = new ArrayList<RequestDensity>();
		for (int i = 0; i < 4; i++)
			reqDen.add(new RequestDensity(i));
		for (LatLng l : reqLatLng) {
			if (!viewBound.contains(l)) {
				if (l.longitude > center.longitude) {
					if (l.latitude > center.latitude) {
						// quarters[0]++;
						reqDen.get(0).incrementCount();
					} else {
						// quarters[3]++;
						reqDen.get(3).incrementCount();
					}
				} else {
					if (l.latitude > center.latitude) {
						// quarters[1]++;
						reqDen.get(1).incrementCount();
					} else {
						// quarters[2]++;
						reqDen.get(2).incrementCount();
					}
				}
			}
		}
		setColorScale(reqDen);
	}

	private void setColorScale(List<RequestDensity> reqDen) {
		// TODO Auto-generated method stub
		int high = Color.RED;
		int medium = Color.YELLOW;
		int low = Color.GREEN;
		int currColor = low;
		Collections.sort(reqDen);
		int lowestCount = reqDen.get(0).getCount();
		int highestCount = reqDen.get(reqDen.size() - 1).getCount();
		/*
		 * if lowest & highest is equal, there is no actual lowest or highest,
		 * all are same, so make it to invalid value(-1)
		 */
		if (lowestCount == highestCount) {
			if (lowestCount == 0)
				currColor = low;
			else
				currColor = high;
			lowestCount = highestCount = -1;
		}
		for (RequestDensity temp : reqDen) {
			if (temp.getCount() < highestCount && temp.getCount() > lowestCount)
				currColor = medium;
			else if (temp.getCount() == highestCount)
				currColor = high;
			switch (temp.getQuarter()) {
			case 0:
				rectView[0].changeColor(currColor);
				rectView[7].changeColor(currColor);
				break;
			case 1:
				rectView[1].changeColor(currColor);
				rectView[2].changeColor(currColor);
				break;
			case 2:
				rectView[3].changeColor(currColor);
				rectView[4].changeColor(currColor);
				break;
			case 3:
				rectView[5].changeColor(currColor);
				rectView[6].changeColor(currColor);
				break;
			}
		}
	}

	private boolean pointNotEmpty(Point p) {
		if (p.x > 0 || p.y > 0)
			return true;
		else
			return false;
	}

	/**
	 * Initializes the Layout Button and gets the drivers location
	 * 
	 */

	public void initialize() {
		this.toggle = (CompoundButton) findViewById(R.id.toggleAvail);
		toggle.setActivated(true);

		passengerList = (Button) findViewById(R.id.textView);
		context = this;

		passengerList.setVisibility(View.VISIBLE);
		final SharedPreferences prefs = getApplicationContext()
				.getSharedPreferences(DriverMainActivity.class.getSimpleName(),
						Context.MODE_PRIVATE);
		boolean avail = prefs.getBoolean("availability", true);
		toggle.setChecked(false);

		initilizeMap();
		this.driverGeo = setUpMap();
		googleMap.moveCamera(CameraUpdateFactory.newLatLng((new LatLng(
				driverGeo.getLatitude(), driverGeo.getLongitude()))));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
		driver = driverset();
		SessionManager.setContext(this);
		SessionManager.getInstance().createLoginSession(driver);
	}

	public void start() {
		updateTextRunnable = new Runnable() {
			public void run() {
				updatelocationtoserver();

				// fetch current location of Driver
				try {

					googleMap.clear();
					if (toggle.isChecked()) {
						Log.d("geo", driverGeo.getLatitude().toString() + "lng"
								+ driverGeo.getLongitude().toString());
						// form URL to call service
						String url = serverIP + "taxi360-war/api/request?lat="
								+ driverGeo.getLatitude().toString() + "&log="
								+ driverGeo.getLongitude().toString()
								+ "&distance=3000";

						try {
							Log.d("return = ",
									Server.getInstance().connect("GET", url)
											+ "");
							if (Server.getInstance().connect("GET", url)
									.getResponse() != null) {
								myObjects = mapper.readValue(Server
										.getInstance().connect("GET", url)
										.getResponse(),
										new TypeReference<List<Request>>() {
										});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// Log.d("myobjects", myObjects.toString());
					if (myObjects.size() > 0) {
						mapRequest = new HashMap<Integer, Request>();
						for (int i = 0; i < myObjects.size(); i++) {
							Log.d("marker", myObjects.get(i).getPassenger()
									.getId().toString());

							mapRequest
									.put(Integer.parseInt(myObjects.get(i)
											.getPassenger().getId().toString()),
											myObjects.get(i));
							LatLng tLatLng = new LatLng(myObjects.get(i)
									.getLocation().getLatitude(), myObjects
									.get(i).getLocation().getLongitude());
							reqLatLng.add(tLatLng);
							googleMap.addMarker(new MarkerOptions().position(
									tLatLng).title(
									myObjects.get(i).getPassenger().getId()
											.toString()));

						}
					}
					updateColorScale();
					googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

						@Override
						public View getInfoWindow(Marker marker) {
							v = getLayoutInflater().inflate(
									R.layout.windowslayout, null);

							for (int i = 0; i < myObjects.size(); i++) {
								// Getting view from the layout file
								// info_window_layout
								if (!((marker.getTitle()
										.equalsIgnoreCase("driver")) || (marker
										.getTitle()
										.equalsIgnoreCase("accepted")))) {

									Log.d("marker id", marker.getTitle()
											.toString());
									Log.d("Title id", marker.getTitle()
											.toString());
									if ((marker.getTitle().toString())
											.equalsIgnoreCase(myObjects.get(i)
													.getPassenger().getId()
													.toString())) {
										Log.d("Matched for", marker.getTitle()
												.toString());
										TextView name = (TextView) v
												.findViewById(R.id.name);
										TextView detail = (TextView) v
												.findViewById(R.id.detail);
										name.setText(myObjects.get(i)
												.getPassenger().getName()
												.toString());
										if (myObjects.get(i).getPassenger()
												.getMobilenumber() != null)
											detail.setText(myObjects.get(i)
													.getPassenger()
													.getMobilenumber()
													.toString());
										// Use your button like this
										Button mBtn = (Button) v
												.findViewById(R.id.Accept);
										mBtn.setBackgroundColor(color.white);
									}
								} else {
									return v;
								}

							}

							return v;
						}

						// Bringing Window above the marker when touched
						@Override
						public View getInfoContents(Marker marker) {

							return null;
						}

					});

					// On clicking the window
					googleMap
							.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

								@Override
								public void onInfoWindowClick(Marker marker) {
									if (!((marker.getTitle()
											.equalsIgnoreCase("driver")) || (marker
											.getTitle()
											.equalsIgnoreCase("accepted")))) {
										passenger = mapRequest
												.get(Integer.parseInt(marker
														.getTitle().toString()))
												.getPassenger();
										googleMap.clear();
										toggle.setChecked(false);
										rideresponse = rideStart(
												mapRequest.get(Integer
														.parseInt(marker
																.getTitle()
																.toString())),
												marker.getPosition(), null);
										Log.d("d", rideresponse.toString());
										googleMap.addMarker(new MarkerOptions()
												.position(marker.getPosition())
												.title("accepted"));
										Intent intent = new Intent(context,
												RideStartActivity.class);
										intent.putExtra("ride", rideresponse);
										startActivity(intent);
										finish();
										/*
										 * rides.setOnClickListener(new
										 * OnClickListener() {
										 * 
										 * @Override public void onClick(View
										 * arg0) {
										 * 
										 * Intent intent = new Intent( context,
										 * RideStartActivity.class);
										 * intent.putExtra("ride",
										 * rideresponse); startActivity(intent);
										 * 
										 * } });
										 */
										// googleMap.addMarker(new
										// MarkerOptions().position(new
										// LatLng(myLocation.getLatitude(),myLocation.getLongitude())).title("driver").visible(false));
										handler.removeCallbacks(updateTextRunnable);
										Log.d("window", marker.getTitle());
									}
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.postDelayed(this, TIME_DELAY);
			}
		};
		updateTextRunnable.run();
	}

	/**
	 * Gets the current Location of the driver and zooms the camera to focus on
	 * the Driver Location
	 * 
	 */

	private Location setUpMap() {
		// Enable MyLocation Layer of Google Map
		Log.d("de", "de 2");
		googleMap.setMyLocationEnabled(true);

		// Get LocationManager object from System Service LOCATION_SERVICE
		this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Create a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Get the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Get Current Location
		android.location.Location driverLocation = locationManager
				.getLastKnownLocation(provider);
		if (driverLocation.toString().isEmpty())
			driverLocation = LocationUtil.getInstance(context)
					.getCurrentLocation();
		// set map type
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Get latitude of the current location
		double latitude = driverLocation.getLatitude();

		// Get longitude of the current location
		double longitude = driverLocation.getLongitude();

		// Create a LatLng object for the current location
		this.myLocation = new Location(latitude, longitude);

		/*
		 * // Show the current location in Google Map
		 * googleMap.moveCamera(CameraUpdateFactory.newLatLng((new LatLng(
		 * latitude, longitude))));
		 * 
		 * // Zoom in the Google Map
		 * googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
		 */
		// googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,
		// longitude)).title("driver").visible(false));
		// return latLng;

		Log.d("driver", myLocation.getLatitude().toString());
		return myLocation;
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		Log.d("de", "de 1");
		if (googleMap == null) {
			this.googleMap = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
		googleMap
				.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
					@Override
					public void onCameraChange(CameraPosition arg0) {
						Log.d("center", arg0.toString());
						updateColorScale();
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		Log.d("infowind", "infowin");
		// TODO Auto-generated method stub

	}

	public AvailableDriver getAvailableDriver() {
		ad = new AvailableDriver();
		ad.setDriver(driver);
		driverGeo = setUpMap();
		ad.setLocation(driverGeo);
		return ad;
	}

	/**
	 * Updates the Server whether the Driver is available or not.During Ride it
	 * is turned off
	 * 
	 */

	public void onToggleClicked(View view) {
		// Is the toggle on?
		// boolean on = ((CompoundButton)view).isChecked();
		boolean on = toggle.isChecked();

		if (on) {
			Log.d("avail", "on");
			String url = serverIP + "taxi360-war/api/availabledriver/on";
			// writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

			Server.getInstance().connect("PUT", url, getAvailableDriver());
			handler.postDelayed(updateTextRunnable, TIME_DELAY);
		} else {
			Log.d("avail", "Off");
			String url = serverIP + "taxi360-war/api/availabledriver/off";
			// writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

			Server.getInstance().connect("PUT", url, getAvailableDriver());
			googleMap.clear();
			initilizeMap();
			handler.removeCallbacks(updateTextRunnable);
			for (RectView rv : rectView)
				rv.resetColor();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		final SharedPreferences prefs = getApplicationContext()
				.getSharedPreferences(DriverMainActivity.class.getSimpleName(),
						Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("availability", toggle.isChecked());
		editor.commit();
	}

	/*
	 * Method to display available Passengers in List Mohammed Althaf A0107629B
	 */
	/**
	 * Navigates the screen to PassengerList screen where the requested
	 * passengers are displayed in a list view
	 * 
	 */

	@SuppressWarnings("deprecation")
	public void PassengerList(View view) {
		Log.d("list", "list" + driverGeo.getLatitude().toString());
		String url = serverIP + "taxi360-war/api/request?lat="
				+ driverGeo.getLatitude().toString() + "&log="
				+ driverGeo.getLongitude().toString() + "&distance=1000000";
		try {

			myObject = mapper.readValue(Server.getInstance()
					.connect("GET", url).getResponse(),
					new TypeReference<List<Request>>() {
					});
			if (!myObject.isEmpty()) {
				Intent intent = new Intent(context, PassengerListActivity.class);
				intent.putExtra("array", myObject);
				intent.putExtra("mylocation", myLocation);
				intent.putExtra("drivergeo", driverGeo);

				// intent.putParcelableArrayListExtra("array", (ArrayList<?
				// extends Parcelable>) myObject);
				startActivity(intent);
				
			} else {
				AlertDialog alert = new AlertDialog(context) {
				};
				alert.setMessage("No Passenger");
				alert.setTitle("Taxi-360");
				alert.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// here you can add functions
					}
				});

				alert.show();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Driver driverset() {

		driver = SessionManager.getInstance().getDriver();
		return driver;
	}

	public Ride rideStart(Request req, LatLng loc, Location driverloc) {
		ride = new Ride();

		ride.setDriver(driverset());
		/*
		 * Passenger tempPassenger = new Passenger(); tempPassenger.setId(id);
		 */
		ride.setPassenger(req.getPassenger());
		ride.setPassengerKey(req.getPassenger().getAccesskey());
		if (driverloc != null) {
			ride.setDriverStartLocation(driverloc);
		} else {
			ride.setDriverStartLocation(driverGeo);
		}
		ride.setPassengerStartLocation(new Location(loc.latitude, loc.longitude));
		writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String url = Prop.URL_START_RIDE;

			rideresponse = mapper.readValue(
					Server.getInstance().connect("POST", url, ride)
							.getResponse(), Ride.class);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rideresponse;
	}

	/**
	 * Updates the current location of the driver to the server
	 * 
	 */

	public void updatelocationtoserver() {
		String url = serverIP + "taxi360-war/api/availabledriver";
		// writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

		Server.getInstance().connect("PUT", url, getAvailableDriver());

		Log.d("id", ad.getDriver().getId() + "driver" + driver.getId());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String url = "";
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.Logout:

			SessionManager.getInstance().logoutUser();
			url = serverIP + "taxi360-war/api/auth/dlogout";
			finish();
			return true;
		case R.id.MyPlaces:

			Intent intent = new Intent(context, MyPlacesActivity.class);
			intent.putExtra("type", "myplaces");
			intent.putExtra("driver", driver);
			startActivity(intent);

			return true;
		case R.id.Goto:
			Intent gotoIntent = new Intent(context, MyPlacesActivity.class);
			gotoIntent.putExtra("type", "goto");
			gotoIntent.putExtra("driver", driver);
			startActivity(gotoIntent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			Log.d("back", Boolean.toString(moveTaskToBack(true)));

			return true;
		}
		return false;
	}

}
