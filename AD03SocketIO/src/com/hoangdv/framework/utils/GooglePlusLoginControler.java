package com.hoangdv.framework.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.hoangdv.framework.MainActivity;
import com.hoangdv.framework.app.AppController;

public class GooglePlusLoginControler implements ConnectionCallbacks,
		OnConnectionFailedListener {
	private Activity mActivity;
	public static final int RC_SIGN_IN = 400;
	private static final String TAG = "GooglePlusLogin";
	private static int PROFILE_PIC_SIZE = 100;
	private GoogleApiClient mGoogleApiClient;

	private Dialog dialog;

	private ConnectionResult connectionResult;

	public GooglePlusLoginControler(Activity activity) {
		this.mActivity = activity;
		mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		mGoogleApiClient.connect();
	}

	// login event
	public void login() {
		dialog = ProgressDialog.show(mActivity, "", "Đang xác thực...", true,
				false);
		try {
			// mGoogleApiClient.connect();
			connectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
		} catch (IntentSender.SendIntentException exception) {
			mGoogleApiClient.connect();
			if (dialog != null)
				dialog.dismiss();
		}
	}

	public void logout() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.clearDefaultAccountAndReconnect();
		}
	}

	// call this method in your activity's onActivityResult
	public void onActivityResult() {
		if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		if (!arg0.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(arg0.getErrorCode(),
					mActivity, 0);
		}
		connectionResult = arg0;
		if (dialog != null)
			dialog.dismiss();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		getUserFromServer(getProfileInformation());
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
		if (dialog != null)
			dialog.dismiss();
	}

	public JSONObject getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person person = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String fullname = person.getDisplayName();
				String iduser = person.getId();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String imgPath = person.getImage().getUrl();
				imgPath = imgPath.substring(0, imgPath.length() - 2)
						+ PROFILE_PIC_SIZE;
				JSONObject userProfile = new JSONObject();
				userProfile.put("id", iduser);
				userProfile.put("email", email);
				userProfile.put(ConfigApp.FULLNAME_KEY, fullname);
				userProfile.put("image", imgPath);
				return userProfile;
			}
			return null;
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public void getUserFromServer(JSONObject object) {
		if (object == null) {
			Toast.makeText(mActivity, "Lỗi xác thực tài khoản!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// dialog = ProgressDialog.show(mActivity, "", "Đang xác thực...",
			// true, false);
			Map<String, String> params = new HashMap<String, String>();
			params.put("uid", object.getString("id"));
			params.put("name", object.getString(ConfigApp.FULLNAME_KEY));
			params.put("provider", "google");
			params.put("email", object.getString("email"));
			params.put("image", object.getString("image"));
			CustomRequest request = new CustomRequest(Method.POST,
					ConfigApp.URL_LOGIN_OPENID, params,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							// TODO Auto-generated method stub
							try {
								if (dialog != null)
									dialog.dismiss();
								if (response.has("error")) {
									if (response.getBoolean("error")) {
										Toast.makeText(mActivity,
												response.getString("msg"),
												Toast.LENGTH_SHORT).show();
									}
								} else if (response.has("id")) {
									// ParseUser currentUser = ParseUser
									// .getCurrentUser();
									// currentUser.put("profile", response);
									// currentUser.saveInBackground();
									ConfigApp.CURRENT_USER.put(
											ConfigApp.PROFILE_KEY, response);
									Intent intent = new Intent(mActivity,
											MainActivity.class);
									mActivity.startActivity(intent);
									mActivity.finish();
								}
							} catch (Exception ex) {
								ex.printStackTrace();
								if (dialog != null)
									dialog.dismiss();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							if (dialog != null)
								dialog.dismiss();
							error.printStackTrace();
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
		} catch (Exception ex) {
			Toast.makeText(mActivity, ex.toString(), Toast.LENGTH_SHORT).show();
			ex.printStackTrace();
		}
	}

	/**
	 * Revoking access from google
	 * */
	public void revokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							mGoogleApiClient.connect();
						}

					});
		}
	}

}
