package sg.edu.astar.ihpc.passenger.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.edu.astar.ihpc.passenger.R;
import sg.edu.astar.ihpc.passenger.entity.PassengerDestination;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<PassengerDestination>{
	private ArrayList<PassengerDestination> driverDestination;
	private int layoutResourceId;
	private Context context;
	private Geocoder geocoder;
	
	private List<Address> addresses;
	public PlacesAdapter(Context context, int layoutResourceId, ArrayList<PassengerDestination> driverDestination) {
		super(context, layoutResourceId, driverDestination);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.driverDestination = driverDestination;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RequestHolder holder = null;
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		holder = new RequestHolder();
		holder.request = driverDestination.get(position);
		holder.name = (TextView)row.findViewById(R.id.des);
		row.setTag(holder);
		setupItem(holder);
		return row;
	}
private void setupItem(RequestHolder holder) {
		
		geocoder = new Geocoder(this.context,Locale.getDefault());
		//addresses=LocationUtil.getInstance(context).getaddress(holder.request.getLocation());
		//addresses = geocoder.getFromLocation(holder.request.getLocation().getLatitude(),holder.request.getLocation().getLongitude(), 1);
		holder.name.setText(LocationUtil.getInstance(context).getaddress(holder.request.getLocation()));
}
	public static class RequestHolder {
		PassengerDestination request;
		TextView name;
	}
}
