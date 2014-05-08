package sg.edu.astar.ihpc.passenger.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import sg.edu.astar.ihpc.passenger.R;
import sg.edu.astar.ihpc.passenger.entity.Passenger;
import sg.edu.astar.ihpc.passenger.entity.Ride;
import android.content.Context;
import android.util.Log;

public class Registration {
	ObjectMapper mapper = new ObjectMapper();
	Server server;
	Response response;
	public Passenger isUserregistered(Passenger pass,Context context) {
		String responses = "false";
		String url =context.getResources().getString(R.string.authsocialurl) ;
		try {
			return mapper.readValue(Server.getInstance().connect("POST", url,pass)
					.getResponse(), Passenger.class);
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
		return pass;
	}

}
