package com.app.weatherpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

// Async Task to access the web
public class JsonReadTask extends AsyncTask<String, Void, String> {
	public Activity activity;

	private String jsonResult;
	private static final String TAG = "Json";

	TextView mDisplay;

	public JsonReadTask(Activity _activity) {
		this.activity = _activity;
	}

	@Override
	protected String doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(params[0]);
		try {
			HttpResponse response = httpclient.execute(httppost);
			jsonResult = inputStreamToString(response.getEntity().getContent())
					.toString();
		}

		catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}

	@Override
	protected void onPostExecute(String result) {
		// show the text
		drawConditions(jsonResult);
		Log.i(TAG, jsonResult);
	}

	private void drawConditions(String conditions) {
		Log.i("drawConditions", conditions);

		try {
			JSONObject jsonResponse = new JSONObject(conditions);
			
			String temp = jsonResponse.optString("temp");
			Button txtTemp = (Button) this.activity.findViewById(R.id.displayTemp);
			txtTemp.setText(temp + "°");
			
			String humidity = jsonResponse.optString("humidity");
			Button txtHumidity = (Button) this.activity.findViewById(R.id.displayHumidity);
			txtHumidity.setText(humidity + "%");
		} catch (JSONException e) {
			//
		}
	}

} // end async task
