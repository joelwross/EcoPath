package edu.uci.ecopath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EcoPath extends Activity
{
	private static final String TAG = "ECOPATH";
	TextView sv;
	TextView dv;
	EditText lat, lng;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.v(TAG,"View set, logging working");
		
		Button goButton = (Button) findViewById(R.id.go_button);
		goButton.setOnClickListener(goButtonListener);
		sv = (TextView) findViewById(R.id.status);
		dv = (TextView) findViewById(R.id.data);
		lat = (EditText) findViewById(R.id.latitude);
		lng = (EditText) findViewById(R.id.longitude);
	}

	// Create an anonymous implementation of OnClickListener
	private OnClickListener goButtonListener = new OnClickListener() {
		public void onClick(View v)
		{
			sv.setText("You clicked the button! Sending Request...");
						
			//String url = "http://dhcp-v000-183.mobile.uci.edu/~joel/ecopath_test.php";
			String url = "http://10.0.2.2:3000/markers";
			Map<String, String> kvPairs = new HashMap<String, String>();
			kvPairs.put("uid", "12345");
			kvPairs.put("lat", lat.getText().toString());
			kvPairs.put("lon", lng.getText().toString());

			String response = "";
			try
			{
				HttpResponse re = HTTPPoster.doPost(url, kvPairs);
				response = EntityUtils.toString(re.getEntity());
			}
			catch (ClientProtocolException e) {dv.setText(e.toString());}
			catch (IOException e){dv.setText(e.toString());}

			dv.setText(response);
		}		
	};

	
}
