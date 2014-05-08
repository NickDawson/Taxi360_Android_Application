package sg.edu.astar.ihpc.passenger;

/** Author :Hemanth/Varun **/
/** The screen takes care of the general login**/
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import sg.edu.astar.ihpc.passenger.entity.Credential;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.util.CheckConnectivity;
import sg.edu.astar.ihpc.passenger.util.LocationUtil;
import sg.edu.astar.ihpc.passenger.util.Registration;
import sg.edu.astar.ihpc.passenger.util.Response;
import sg.edu.astar.ihpc.passenger.util.Server;
import sg.edu.astar.ihpc.passenger.util.SessionManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends Activity implements OnClickListener,
ConnectionCallbacks, OnConnectionFailedListener{
	Passenger pass;
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	private boolean mIntentInProgress;	
    private boolean mSignInClicked;
	LoginButton button;	 
	private ConnectionResult mConnectionResult;
	private static final int RC_SIGN_IN = 0;
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	Context context;
	public static final String TAG = "LoginActivity.java";
	private GoogleApiClient mGoogleApiClient;
	private SignInButton btnSignIn;
	CheckConnectivity register;
	IntentFilter gcmFilter;
	Registration rg=new Registration();
	LocationUtil gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		gps=LocationUtil.getInstance(this);
		PROJECT_ID=getResources().getString(R.string.projectid);
		registerInBackground();
		setContentView(R.layout.activity_login);
		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");
		register=new CheckConnectivity();
		pass=new Passenger();
		SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
		String username = pref.getString(PREF_USERNAME, null);
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		if(username!=null){
			mEmailView.setText(username);
		}
		
		btnSignIn = (SignInButton) findViewById(R.id.googlesignin);
		btnSignIn.setOnClickListener(this);
		  mGoogleApiClient = new GoogleApiClient.Builder(this)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this).addApi(Plus.API, null)
          .addScope(Plus.SCOPE_PLUS_LOGIN).build();
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		Session session = Session.getActiveSession();
	    if (session != null) {

	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	        }
	    } 
	    if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            
        }
	    
	    button=(LoginButton)findViewById(R.id.loginfacebook);
	    button.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						Intent intent = new Intent(context, LoginView.class);
						startActivity(intent);
						finish();
					}
				});
	    
	    final TextView view = (TextView) findViewById(R.id.link_sign_up);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }

        });
	}

	

	public void register(View v) {

		Intent intent = new Intent(context, RegistrationActivity.class);

		startActivity(intent);
	}

	

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;
		
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			
			focusView = mPasswordView;
			focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			focusView.setBackgroundColor(Color.parseColor("#F5DC49"));
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
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
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERNAME = "username";
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		Passenger passenger;
		Server server;
		Response response;
		Boolean result;
		Credential credential;

		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d(TAG, "Start =");
			getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
	        .edit()
	        .putString(PREF_USERNAME, mEmail)
	        
	        .commit();
			credential = new Credential();
			credential.setUsername(mEmail);
			credential.setPassword(mPassword);
			server = Server.getInstance();
			String url = getResources().getString(R.string.authurl);;
			response = server.connect("POST", url, credential);
			return response.getStatus();
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			Log.d(TAG, "success =" + success);
			mAuthTask = null;
			showProgress(false);

			if (success) {
				try {
					Log.d(TAG, "response =" + response.getResponse());
					passenger = (new ObjectMapper()).readValue(
							response.getResponse(), Passenger.class);
				} catch (JsonParseException e) {
					Log.d(TAG, "JsonParseException= " + e.getMessage());
				} catch (JsonMappingException e) {
					Log.d(TAG, "JsonMappingException= " + e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, "IOException= " + e.getMessage());
				} catch (Exception e) {
					Log.d(TAG, "Exception= " + e.getMessage());
				}
				
				passenger.setRelocated(false);
				Intent intent = new Intent(context, MainTabActivity.class);
				intent.putExtra("Title", passenger);
				startActivity(intent);
				finish();

			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            
        }
        
    }
 
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
 
    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
 
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            
            return;
        }
 
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
           // mSignInClicked = true;
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
 
    }
 
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
            Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
 
            mIntentInProgress = false;
 
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
 
    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        
 
        // Update the UI after signin
        updateUI(true);
 
    }
 
    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
                       
            pass.setName(Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient).getName().getGivenName().toString());
            pass.setRegistrationtype("google");
            pass.setEmailid(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getUrl());
            new GoogleUserLoginTask().execute(); 
         }
    }
 
    /**
     * Fetching user's information name
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
 
                Log.d(TAG, "Name: " + personName);
                
               
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }
 
  
 
    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.googlesignin:
            // Signin button clicked
        	//mGoogleApiClient.connect();
            signInWithGplus();
            break;
        case R.id.loginfacebook:
        	Intent intent = new Intent(context, LoginView.class);
			startActivity(intent);
			break;
        
        }
    }
 
    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
    	mSignInClicked = true;
    	mGoogleApiClient.connect();
        if (!mGoogleApiClient.isConnecting()) {
        	mGoogleApiClient.reconnect();
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }
    String regId;
    String PROJECT_ID;
	String PROPERTY_REG_ID = "regid";
    String registrationStatus;
    
        @Override
	protected void onPause() {

		unregisterReceiver(register);
		super.onPause();
	}

	// When an activity is resumed, be sure to register any
	// broadcast receivers with the appropriate intent
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(register, gcmFilter);

	}

	// There are no menus for this demo app. This is just
	// boilerplate code.

	// NOTE the call to GCMRegistrar.onDestroy()
	@Override
	public void onDestroy() {

		//GCMRegistrar.onDestroy(this);

		super.onDestroy();
	}
	GoogleCloudMessaging gcm;
	private void registerInBackground() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regId = gcm.register(PROJECT_ID);
	                Log.d("REGID---B", regId);
	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	               

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId();
	            } catch (IOException ex) {
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
				return null;
			}

			private void storeRegistrationId() {
				final SharedPreferences prefs = getSharedPreferences("regid",
			            Context.MODE_PRIVATE);
			    SharedPreferences.Editor editor = prefs.edit();
			    editor.putString(PROPERTY_REG_ID, regId);
			    editor.commit();
			}

			
	    }.execute(null, null, null);

	}

	private String getRegistrationId() {
		final SharedPreferences prefs = getSharedPreferences("regid",
	            Context.MODE_PRIVATE);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        //Log.i(TAG, "Registration not found.");
	        return "";
	    }
		return registrationId;
	}
	
	public class GoogleUserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			pass=rg.isUserregistered(pass,context);
			return (pass.getId()!=0);
		}
			
		

		@Override
		protected void onPostExecute(final Boolean success) {
			Log.d(TAG, "success =" + success);
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Log.d("insideif", pass.getName());
				pass.setRelocated(false);
				Intent intent = new Intent(context,
						MainTabActivity.class);
				intent.putExtra("Title", pass);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(context,
						RegisterPhoneActivity.class);
				
				pass.setRelocated(false);
				intent.putExtra("Title", pass);
				startActivity(intent);
				finish();
			}
		}}
}