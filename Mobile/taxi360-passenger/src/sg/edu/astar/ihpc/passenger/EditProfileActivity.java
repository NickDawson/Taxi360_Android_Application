package sg.edu.astar.ihpc.passenger;

import com.google.android.gcm.GCMRegistrar;

import sg.edu.astar.ihpc.passenger.LoginActivity.UserLoginTask;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.util.Server;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class EditProfileActivity extends Activity {
Passenger passenger;
EditText editname;
EditText editrepassword;
EditText editpassword;
EditText editemail;
Passenger passnew;
public static final String PREFS_NAME = "MyPrefsFile";
private static final String PREF_USERNAME = "username";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		passenger=new Passenger();
		passenger=(Passenger)(getIntent().getSerializableExtra("Passenger"));
		setview();
		
	}

	private void setview() {
		// TODO Auto-generated method stub
		 editname=(EditText) findViewById(R.id.editname);
		 editrepassword=(EditText) findViewById(R.id.editrepassword);
		 editpassword= (EditText) findViewById(R.id.editpassword);
		 editemail= (EditText) findViewById(R.id.editemail);
		 
		 
		 editname.setText(passenger.getName());
		 editemail.setText(passenger.getEmailid());
	}
public void editregister_listener(View v){
	passnew=passenger;
	passnew.setName(editname.getText().toString());
	passnew.setEmailid(editemail.getText().toString() + "*" + editrepassword.getText().toString());
	attemptLogin();
}

private void Registerpassenger() {
String url=getResources().getString(R.string.passengerurl);;
String responses=null;

	Server.getInstance().connect("PUT", url, passnew);

       
  

}


public void attemptLogin() {
	

	// Reset errors.
	editname.setError(null);
	editpassword.setError(null);
	
	editrepassword.setError(null);
	editemail.setError(null);
	
	

	boolean cancel = false;
	View focusView = null;
	
	// Check for a valid password.
	if (TextUtils.isEmpty(editpassword.getText().toString())) {
		editpassword.setError(getString(R.string.error_field_required));
		
		focusView = editpassword;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	} else if (editpassword.getText().toString().length() < 4) {
		editpassword.setError(getString(R.string.error_invalid_password));
		focusView = editpassword;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	}

	// Check for a valid email address.
	if (TextUtils.isEmpty(editname.getText().toString())) {
		editname.setError(getString(R.string.error_field_required));
		focusView = editname;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	} 
	if (TextUtils.isEmpty(editrepassword.getText().toString())) {
		editrepassword.setError(getString(R.string.error_field_required));
		
		focusView = editrepassword;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	} else if (editrepassword.getText().toString().length() < 4) {
		editrepassword.setError(getString(R.string.error_invalid_password));
		focusView = editrepassword;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	}

	// Check for a valid email address.
	if (TextUtils.isEmpty(editemail.getText().toString())) {
		editemail.setError(getString(R.string.error_field_required));
		focusView = editemail;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	} else if (!editemail.getText().toString().contains("@")) {
		editemail.setError(getString(R.string.error_invalid_email));
		focusView = editemail;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	}
	if (!editrepassword.getText().toString().equals(editpassword.getText().toString())) {
		editrepassword.setError("Passwords not same");
		focusView = editrepassword;
		focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
		cancel = true;
	}

	if (cancel) {
		// There was an error; don't attempt login and focus the first
		// form field with an error.
		focusView.requestFocus();
	} else {
		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		Registerpassenger();
		Intent intent = new Intent(this,
				MainTabActivity.class);
		passnew.setEmailid(editemail.getText().toString());
		intent.putExtra("Title", passnew);
		startActivity(intent);
	}
}


	

}
