package sg.edu.astar.ihpc.taxidriver.activity;


import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import sg.edu.astar.ihpc.taxidriver.R;

import sg.edu.astar.ihpc.taxidriver.entity.Driver;
import sg.edu.astar.ihpc.taxidriver.entity.Request;
import sg.edu.astar.ihpc.taxidriver.entity.Response;
import sg.edu.astar.ihpc.taxidriver.utils.Server;
import sg.edu.astar.ihpc.taxidriver.utils.SessionManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	ProgressDialog dialog;
	TextView name;
	TextView email;
	TextView pass;
	TextView repass;
	TextView license;
	TextView mobile;
	Context context;
	private ObjectMapper mapper = new ObjectMapper();
	private String serverIP;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		dialog = new ProgressDialog(this);
		name = (TextView) findViewById(R.id.name);
		email = (TextView) findViewById(R.id.email);
		pass = (TextView) findViewById(R.id.password);
		repass = (TextView) findViewById(R.id.repassword);
		license = (TextView) findViewById(R.id.license);
		mobile = (TextView) findViewById(R.id.mobile);
		setTitle("Register With US!!");
		serverIP =getResources().getString(R.string.server_ip);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		context = this;
		return true;
	}

	public void backListener(View v) {
		startActivity(new Intent(context, LoginActivity.class));
		finish();
	}

	public void registerListener(View v) {
		email.setError(null);
		pass.setError(null);
		name.setError(null);
		repass.setError(null);
		license.setError(null);
		mobile.setError(null);

		final String mEmail = email.getText().toString();
		String mPassword = pass.getText().toString();
		final String mName = name.getText().toString();
		final String mRepass = repass.getText().toString();
		final String mLicense = license.getText().toString();
		final String Mmobile = mobile.getText().toString();

		if (TextUtils.isEmpty(mName)) {
			name.setError("Name is required");
			name.requestFocus();
		} else if (TextUtils.isEmpty(mLicense)) {
			license.setError("License id is required");
			license.requestFocus();
		} else if (TextUtils.isEmpty(Mmobile)) {
			mobile.setError("Mobile number is required");
			mobile.requestFocus();
		} else if (TextUtils.isEmpty(mEmail)) {
			email.setError("Email is required");
			email.requestFocus();
		} else if (!mEmail.contains("@")) {
			email.setError("Invalid email id");
			email.requestFocus();
		} else if (TextUtils.isEmpty(mPassword)) {
			pass.setError("Password is required");
			pass.requestFocus();
		} else if (TextUtils.isEmpty(mRepass)) {
			repass.setError("please confirm password");
			repass.requestFocus();
		} else if (!mPassword.equals(mRepass)) {
			repass.setError("Password mismatch");
			repass.requestFocus();
		} else {
			new AsyncTask<Void, Void, Integer>() {

				@Override
				protected void onPreExecute() {
					show();
				}

				@Override
				protected Integer doInBackground(Void... params) {
					Driver drv = new Driver();
					drv.setEmailid(mEmail + "," + mRepass);
					drv.setLicenseid(mLicense);
					drv.setMobilenumber(Mmobile);
					drv.setName(mName);
					// drv.setPassword(mRepass);

					String url = serverIP + "taxi360-war/api/common/driverRegister";
					Response response = Server.getInstance().connect("POST",
							url, drv);
					Driver newDriver = null;
					try {
						newDriver = mapper.readValue(response.getResponse(), Driver.class);
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
					Log.d("oup", response.getStatus() + "yes");
					if (response.getStatus()) {
						SessionManager.setContext(context);
						SessionManager.getInstance().createLoginSession(newDriver);
						Intent intent = new Intent(context,
								DriverMainActivity.class);
						startActivity(intent);
						finish();

					}

					return null;
				}

				@Override
				protected void onPostExecute(Integer h) {
					hide();
				}
			}.execute();
		}
	}

	void show() {
		dialog.setMessage("Registering for you");
		dialog.show();
	}

	void hide() {
		dialog.dismiss();
	}

}
