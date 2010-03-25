package edu.uci.ecopath;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class EcoPath extends MapActivity
{
	private static final String TAG = "ECOPATH";
	private TextView sv;
	private TextView dv;
	private EditText lat, lng;

	private LocationManager lm;
	private MapController mc;
	private MapView mapView;
	
	double curLat = 33.642937;
	double curLng = -117.841411;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.v(TAG,"View set, logging working");

		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);    
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		
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
			sv.setText("You clicked the button! Now take a picture.");

			//take a picture
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("TempPicture")));
			startActivityForResult(intent, 0);

			
			//sv.setText("You clicked the button! Sending Request...");
						
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		sv.setText("In onActivityResult");
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			if (data == null) {
				sv.setText("No Returned Intent");
				Log.v("CAMERA RESULT", "NO RETURNED INTENT");
				return;
			} else {
				sv.setText("I got something back!");
				Log.v("CAMERA RESULT", "I GOT SOMETHING BACK!!!!");
				return;
			}
		}
	}
	
	//from: http://www.devx.com/wireless/Article/39239/1954
	private class MyLocationListener implements LocationListener 
	{
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				curLat = loc.getLatitude();
				curLng = loc.getLongitude();
				Toast.makeText(getBaseContext(), 
						"Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), 
						Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Latitude: "+curLat+" Longitude: "+curLng);
				GeoPoint p = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
				mc.animateTo(p);
				mc.setZoom(16);                
				mapView.invalidate();
			}
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}

	protected boolean isRouteDisplayed()
	{ return false; }

}
