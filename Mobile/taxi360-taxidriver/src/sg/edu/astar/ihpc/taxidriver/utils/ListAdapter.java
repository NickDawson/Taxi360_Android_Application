package sg.edu.astar.ihpc.taxidriver.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import sg.edu.astar.ihpc.taxidriver.R;


import sg.edu.astar.ihpc.taxidriver.entity.Request;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Request>{
	private List<Request> request;
	private int layoutResourceId;
	private Context context;
	private Geocoder geocoder;
	private Address address;
	private List<Address> addresses;
	

	public ListAdapter(Context context, int layoutResourceId, List<Request> request) {
		super(context, layoutResourceId, request);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.request = request;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RequestHolder holder = null;
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		holder = new RequestHolder();
		holder.request = request.get(position);
		holder.name = (TextView)row.findViewById(R.id.request_id);
		holder.value=(TextView)row.findViewById(R.id.request_address);
		holder.number=(TextView)row.findViewById(R.id.request_phone);
		holder.acceptButton = (ImageButton)row.findViewById(R.id.accept);
		holder.acceptButton.setTag(holder.request);
		row.setTag(holder);
		setupItem(holder);
		return row;
	}

	private void setupItem(RequestHolder holder) {
		
		try {
			JSONObject jsonObject = null;
			String url="http://maps.googleapis.com/maps/api/geocode/json?sensor=false&language=en&latlng=";
			 url=url+Double.toString(holder.request.getLocation().getLatitude())+","+Double.toString(holder.request.getLocation().getLongitude());
						String reversegeocode=Server.getInstance()
								.connect("GET", url).getResponse();
						Log.d("reverse geocode",reversegeocode);
						
							jsonObject = new JSONObject(reversegeocode);
							List<String> items = Arrays.asList(jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address").split("\\s*,\\s*"));
							holder.value.setText(items.get(0));	
							holder.name.setText(holder.request.getPassenger().getName().toString());
							holder.number.setText(holder.request.getPassenger().getMobilenumber().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

	public static class RequestHolder {
		Request request;
		TextView name;
		TextView value;
		TextView number;
		ImageButton acceptButton;
	}
	

}
