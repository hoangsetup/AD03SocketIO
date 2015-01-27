package com.hoangdv.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hoangdv.framework.adapters.NavDrawerListAdapter;
import com.hoangdv.framework.app.AppController;
import com.hoangdv.framework.models.NavDrawerItem;
import com.hoangdv.framework.utils.ConfigApp;
import com.hoangdv.framework.utils.Utilities;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	Utilities utilities;
	ListView listView;
	Button btn_more;
	NavDrawerListAdapter adapter = null;
	Vector<NavDrawerItem> items = new Vector<NavDrawerItem>();

	private static int page_class = 0;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		// hidden action bar
		this.getActionBar().hide();
		utilities = new Utilities(this);
		listView = (ListView) findViewById(R.id.listView_listroom);
		btn_more = (Button) findViewById(R.id.button_moreroom);
		btn_more.setVisibility(View.GONE);
		// TestJsonObject
		// ParseUser parseUser = ParseUser.getCurrentUser();
		// JSONObject userProfile =
		// parseUser.getJSONObject(ConfigApp.PROFILE_KEY);
		Log.d("profileMain", ConfigApp.CURRENT_USER.toString());
		// /TestJsonObject
		getDataRooms();

		adapter = new NavDrawerListAdapter(MainActivity.this, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// ParseUser parseUser = ParseUser.getCurrentUser();
				// JSONObject userProfile = parseUser
				// .getJSONObject(ConfigApp.PROFILE_KEY);
				// Log.d("profile", userProfile.toString());
				Intent intent = new Intent(MainActivity.this,
						SlideActivity.class);
				startActivityForResult(intent, 113);
			}
		});

		btn_more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getDataRooms();
			}
		});
	}

	public void getDataRooms() {
		// ParseUser parseUser = ParseUser.getCurrentUser();
		// JSONObject userProfile =
		// parseUser.getJSONObject(ConfigApp.PROFILE_KEY);

		try {
			JSONObject userProfile = ConfigApp.CURRENT_USER
					.getJSONObject(ConfigApp.PROFILE_KEY);
			JSONObject rooms = userProfile.getJSONObject(ConfigApp.ROOMS_KEY);
			int number_class = rooms.getInt(ConfigApp.NUMBER_CLASS_KEY);
			if (number_class <= 0)
				return;
			getRoom(userProfile.getString("id"), page_class);
			page_class++;

		} catch (Exception ex) {

		}
	}

	public void getRoom(String uid, int p) {
		dialog = ProgressDialog.show(this, "", "Đang tải...", true, false);
		String url = ConfigApp.URL_GETROOMDATA + "/" + uid + "/" + p;
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, url,
				null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject data) {
						try {
							if (data.has(ConfigApp.NO_MORE_KEY)) {
								boolean no_more = data
										.getBoolean(ConfigApp.NO_MORE_KEY);
								if (no_more)
									btn_more.setVisibility(View.GONE);
								// return;
								else
									btn_more.setVisibility(View.VISIBLE);
							} else if (data.has("data")) {
								btn_more.setVisibility(View.VISIBLE);
							}

							JSONArray jsonArrayRooms = data
									.getJSONArray(ConfigApp.DATA_ROOM_KEY);
							for (int i = 0; i < jsonArrayRooms.length(); i++) {
								JSONObject object = jsonArrayRooms
										.getJSONObject(i);
								NavDrawerItem item = new NavDrawerItem(object
										.getString(ConfigApp.NAME_CLASS_KEY),
										R.drawable.ic_chat, false, "");
								item.setIs_live(object
										.getBoolean(ConfigApp.IS_LIVE_KEY));
								item.setUserID(object
										.getString(ConfigApp.IDC_KEY));
								items.add(item);
							}
							adapter.notifyDataSetChanged();

							if (dialog.isShowing())
								dialog.dismiss();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (dialog.isShowing())
							dialog.dismiss();
						Toast.makeText(MainActivity.this, error.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("Authorization", ConfigApp.API_KEY);
				return map;
			}
		};
		AppController.getInstance().addToRequestQueue(request);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 111) {
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
