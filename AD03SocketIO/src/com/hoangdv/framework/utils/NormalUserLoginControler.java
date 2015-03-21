package com.hoangdv.framework.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.hoangdv.framework.MainActivity;
import com.hoangdv.framework.app.AppController;

public class NormalUserLoginControler {
	private Activity activity;
	private Dialog dialog;

	public NormalUserLoginControler(Activity act) {
		this.activity = act;
	}

	public void logon(String username, String password) {
		dialog = ProgressDialog.show(activity, "", "Đang xác thực...", true,
				false);
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		CustomRequest customRequest = new CustomRequest(Method.POST,
				ConfigApp.URL_LOGIN, params, new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						if (dialog != null)
							dialog.dismiss();
						try {
							if (response.has("error")) {
								if (response.getBoolean("error")) {
									Toast.makeText(activity,
											response.getString("msg"),
											Toast.LENGTH_SHORT).show();
								}
							} else if (response.has("id")) {

								Log.d("ProfileNomarl", response.toString());
								ConfigApp.CURRENT_USER.put("profile", response);

								Intent intent = new Intent(activity,
										MainActivity.class);
								activity.startActivity(intent);
								activity.finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					};
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d(NormalUserLoginControler.class.getName(),
								error.toString());
						Toast.makeText(activity, error.toString(),
								Toast.LENGTH_LONG).show();
						if (dialog != null)
							dialog.dismiss();
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("Authorization", ConfigApp.API_KEY);
				return map;
			}
		};
		AppController.getInstance().addToRequestQueue(customRequest);
	}
}
