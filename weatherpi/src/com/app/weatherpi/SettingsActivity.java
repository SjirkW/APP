package com.app.weatherpi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.rvg.rangeseekbar.RangeSeekBar;
import com.rvg.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;

public class SettingsActivity extends Activity {

	private String TAG = "Settings";

	private double mTempMin = 15;
	private double mTempMax = 23;
	private double mHumMin = 40;
	private double mHumMax = 70;

	private String regId = "";

	private Context context;

	private final int MIN_TEMP = 0;
	private final int MAX_TEMP = 30;
	private final int MIN_HUM = 0;
	private final int MAX_HUM = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		//hide the action bar

		//retrieve the stored values from shared preferences
		context = getApplicationContext();
		getSettingsFromSP();

		//get the reg id from the previous activity
		Intent intent = getIntent();
		regId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		// create the temperature seekbar
		final TextView minTemp = (TextView) findViewById(R.id.minValueTemp);
		final TextView maxTemp = (TextView) findViewById(R.id.maxValueTemp);
		// create RangeSeekBar as Integer range between 20 and 75
		RangeSeekBar<Integer> seekBarTemp = new RangeSeekBar<Integer>(MIN_TEMP,
				MAX_TEMP, this);
		// give the thumbs the values from the database
		seekBarTemp.setThumbValue(mTempMin, mTempMax);
		minTemp.setText(Double.toString(mTempMin));
		maxTemp.setText(Double.toString(mTempMax));
		seekBarTemp
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mTempMin = minValue;
						minTemp.setText(Double.toString(mTempMin));
						mTempMax = maxValue;
						maxTemp.setText(Double.toString(mTempMax));
					}
				});
		// add RangeSeekBar to pre-defined layout
		ViewGroup layoutTemp = (ViewGroup) findViewById(R.id.layoutTemp);
		layoutTemp.addView(seekBarTemp);

		// create the humidity seekbar
		final TextView minHum = (TextView) findViewById(R.id.minValueHum);
		final TextView maxHum = (TextView) findViewById(R.id.maxValueHum);
		// create RangeSeekBar as Integer range between 20 and 75
		RangeSeekBar<Integer> seekBarHum = new RangeSeekBar<Integer>(MIN_HUM,
				MAX_HUM, this);
		// give the thumbs the values from the database
		seekBarHum.setThumbValue(mHumMin, mHumMax);
		minHum.setText(Double.toString(mHumMin));
		maxHum.setText(Double.toString(mHumMax));
		// set onchangelistener
		seekBarHum
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mHumMin = minValue;
						minHum.setText(Double.toString(mHumMin));
						mHumMax = maxValue;
						maxHum.setText(Double.toString(mHumMax));
					}
				});
		// add RangeSeekBar to pre-defined layout
		ViewGroup layoutHum = (ViewGroup) findViewById(R.id.layoutHum);
		layoutHum.addView(seekBarHum);
	}

	/**
	 * Start a thread that handles the storing of information in the database
	 * @param view
	 */
	public void submitToDB(View view) {
		Button postBtn = (Button) findViewById(R.id.buttonPost);
		postBtn.setText("Versturen...");
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					post();
					//close activity
					SettingsActivity.this.finish();
					Log.i(TAG, "settings activity ended");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	/**
	 * store values in a database and in the shared preferences
	 */
	private void post() {

		if (regId.length() > 1) {
			// generate params
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("reg_id", regId));
			nameValuePairs.add(new BasicNameValuePair("temp_min", Double
					.toString(mTempMin)));
			nameValuePairs.add(new BasicNameValuePair("temp_max", Double
					.toString(mTempMax)));
			nameValuePairs.add(new BasicNameValuePair("hum_min", Double
					.toString(mHumMin)));
			nameValuePairs.add(new BasicNameValuePair("hum_max", Double
					.toString(mHumMax)));

			// send them to the database
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://standpi.com/php/app_settings.php");
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				Log.i(TAG, EntityUtils.toString(httpEntity));


			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		storeSettings();
	}

	/**
	 * read stored settings from shared preferences
	 */
	private void getSettingsFromSP() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		mTempMin = prefs.getInt(TEMP_LOW, 15);
		mTempMax = prefs.getInt(TEMP_HIGH, 23);
		mHumMin = prefs.getInt(HUM_LOW, 40);
		mHumMax = prefs.getInt(HUM_HIGH, 70);

		Log.i(TAG,
				"Settings Received" + Double.toString(mTempMin)
						+ Double.toString(mTempMax) + Double.toString(mHumMin)
						+ Double.toString(mHumMax));
	}

	private static final String TEMP_LOW = "lowTemp";
	private static final String TEMP_HIGH = "HighTemp";
	private static final String HUM_LOW = "lowHum";
	private static final String HUM_HIGH = "highHum";

	/**
	 * Store the lastest settings in shared preferences
	 */
	private void storeSettings() {
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putInt(TEMP_LOW, (int) mTempMin);
		editor.putInt(TEMP_HIGH, (int) mTempMax);
		editor.putInt(HUM_LOW, (int) mHumMin);
		editor.putInt(HUM_HIGH, (int) mHumMax);
		editor.commit();
	}
}