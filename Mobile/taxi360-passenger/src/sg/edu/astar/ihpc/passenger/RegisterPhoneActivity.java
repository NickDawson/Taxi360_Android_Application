package sg.edu.astar.ihpc.passenger;

/**
 The Screen allows the user to register the mobile number for the application
 **/

/** Author :Hemanth **/

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.android.gcm.GCMRegistrar;

import sg.edu.astar.ihpc.passenger.R;
import sg.edu.astar.ihpc.passenger.entity.OTP;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.Ride;
import sg.edu.astar.ihpc.passenger.util.CheckConnectivity;
import sg.edu.astar.ihpc.passenger.util.Server;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterPhoneActivity extends Activity {
	private String phonenumber;
	private Button button;
	private Button button2;
	EditText OTP;
	EditText number;
	Context context;
	Passenger passenger;
	TextView error;
	private Date otpdate;
	CheckConnectivity connectivity;
	ObjectMapper mapper = new ObjectMapper();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_phone);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

		}
		context = this;

		setContentView(R.layout.activity_register_phone);
		passenger = (Passenger) (getIntent().getSerializableExtra("Title"));
		connectivity = new CheckConnectivity();
		OTP = (EditText) findViewById(R.id.otp);
		OTP.setEnabled(false);
		button2 = (Button) findViewById(R.id.validate);
		button2.setEnabled(false);
		button2.setBackgroundResource(R.drawable.ic_orange_button);
		error = (TextView) findViewById(R.id.error);
		error.setEnabled(false);

		mobileNumberEntered();
		number = (EditText) findViewById(R.id.number);
	}

	@Override
	public void onBackPressed() {

	}

	/**
	 * The button allows the user to enter the mobile number
	 **/
	private void mobileNumberEntered() {
		
		button = (Button) findViewById(R.id.register);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!CheckConnectivity.isNetworkAvailable(context)
						&& (!connectivity.isConnectedToServer())) {
					connectivity.alertshow(context);
				} else {
					error.setVisibility(View.GONE);
					button.setEnabled(false);
					button.setBackgroundResource(R.drawable.ic_orange_button);
					button2.setBackgroundResource(R.drawable.ic_green_button);
					number.setEnabled(false);
					phonenumber = number.getText().toString();
					if (validate(phonenumber)) {

						new SendOTPAsynch().execute();
						button2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (!CheckConnectivity.isNetworkAvailable(context)
										&& (!connectivity.isConnectedToServer())) {
									connectivity.alertshow(context);
								} else {
									error.setVisibility(View.GONE);

									Date date = new Date();
									if ((date.getTime() - otpdate.getTime()) > 300000) {
										error.setVisibility(View.VISIBLE);
										error.setEnabled(true);
										error.setText("Please resend as OTP has expired");
										button.setEnabled(true);
										button.setBackgroundResource(R.drawable.ic_green_button);
										number.setEnabled(true);
										mobileNumberEntered();
									} else {
										if (validateotp(OTP.getText()
												.toString())) {
											Registerpassenger();
											Intent intent = new Intent(context,
													MainTabActivity.class);
											intent.putExtra("Title", passenger);
											startActivity(intent);
											finish();
										} else {
											error.setVisibility(View.VISIBLE);
											error.setEnabled(true);
											error.setText("Invalid OTP.Please retry");
										}

									}

								}

							}
						});
					} else {
						error.setVisibility(View.VISIBLE);
						error.setEnabled(true);
						error.setText("Invalid Phone number");
						button.setEnabled(true);
						number.setEnabled(true);

					}
				}

			}
		});

	}

	/**
	 * The button allows the user to validate the OTP
	 **/

	private boolean validate(String phonenumber) {

		Pattern pattern = Pattern.compile("\\d{8}");
		Matcher matcher = pattern.matcher(phonenumber);

		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * The fuction allows the send the request to server
	 **/
	protected void send(String phonenumber) {

		String URL = getResources().getString(R.string.commonurl)+"generateOTP";
		OTP otp = new OTP();
		otp.setMobilenumber(phonenumber);
		Server.getInstance().connect("POST", URL, otp);
		otpdate = new Date();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_phone, menu);
		return true;
	}

	/**
	 * The fuction allows the otp to be validated at server side
	 **/
	private boolean validateotp(String string) {
		Date date = new Date();
		OTP requestotp=new OTP(string);
		requestotp.setMobilenumber(number.getText().toString());
		requestotp.setCreatetime(date);
		String url=getResources().getString(R.string.commonurl)+"validateOTP";
		return Boolean.parseBoolean(Server.getInstance().connect("POST", url,requestotp).getResponse());
	}

	private void Registerpassenger() {passenger.setMobilenumber(number.getText().toString());
	
	
	String url=getResources().getString(R.string.commonurl)+"passengerRegister";
    String responses=null;
    
    	
    	
    	try {
			passenger = mapper.readValue(Server.getInstance().connect("POST", url, passenger).getResponse(), Passenger.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
           
      

	}

	private void sendOTP(String phonenumber) {

		send(phonenumber);

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
	class SendOTPAsynch extends AsyncTask<Void, Integer, String>
	{
	    protected void onPreExecute (){
	        
	    }

	    protected String doInBackground(Void...arg0) {
	    	sendOTP(phonenumber);
			
			return null;
	       
	    }

	    protected void onProgressUpdate(Integer...a){
	        
	    }

	    protected void onPostExecute(String result) {
	    	button2.setEnabled(true);
			OTP.setEnabled(true);
	    }
	}
}
