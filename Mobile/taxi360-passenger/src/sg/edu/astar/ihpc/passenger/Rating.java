package sg.edu.astar.ihpc.passenger;

/** The Screen is called once the ride is completed and is used to give the rating for the ride**/
/** Author :Althaf **/


import sg.edu.astar.ihpc.passenger.R;
import sg.edu.astar.ihpc.passenger.entity.Ride;
import sg.edu.astar.ihpc.passenger.util.CheckConnectivity;
import sg.edu.astar.ihpc.passenger.util.Server;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RatingBar;

public class Rating extends Activity {

	private Ride ride;
	private CheckConnectivity connectivity;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
		context = this;
		connectivity = new CheckConnectivity();
	}

	/** The function helps in storing the rating for the ride **/
	public void saveRating(View view) {

		ride = (Ride) (getIntent().getSerializableExtra("ride"));
		RatingBar mBar = (RatingBar) findViewById(R.id.rating);
		ride.setDriverRating((double) (mBar.getRating()));
		String url = getResources().getString(R.string.ratingurl);;
		Server.getInstance().connect("PUT", url, ride);
			Intent intent = new Intent(context, MainTabActivity.class);
			intent.putExtra("Title", ride.getPassenger());
			startActivity(intent);
		

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    switch(keyCode)
	    {
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
