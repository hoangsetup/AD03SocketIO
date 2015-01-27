package com.hoangdv.framework.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.hoangdv.framework.MainActivity;
import com.hoangdv.framework.app.AppController;

public class FacebookLoginControlerNative {
	private Activity mActivity;
	private Dialog dialog;
	public static Session.StatusCallback statusCallback;

	public FacebookLoginControlerNative(Activity activity) {
		this.mActivity = activity;
		statusCallback = new SessionStatusCallback();
	}

	public void login() {
		dialog = ProgressDialog.show(mActivity, "", "Đang xác thực...", true,
				false);
		List<String> permissions = Arrays.asList("public_profile", "email",
				"user_checkins", "user_checkins");
		Session session = Session.getActiveSession();

		//
		if (session == null) {
			session = new Session(mActivity);
		}
		Session.setActiveSession(session);
		session.addCallback(statusCallback);
		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			session.openForRead(new Session.OpenRequest(mActivity).setCallback(
					statusCallback).setPermissions(permissions));
		}
		//
		if (!session.isOpened()) {
			session.openForRead(new Session.OpenRequest(mActivity).setCallback(
					statusCallback).setPermissions(permissions));
		} else {
			Session.openActiveSession(mActivity, true, statusCallback);
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			processSessionStatus(session, state, exception);
		}
	}

	@SuppressWarnings("deprecation")
	public void processSessionStatus(Session session, SessionState state,
			Exception exception) {

		if (session != null && session.isOpened()) {

			if (session.getPermissions().contains("email")) {
				Request.executeMeRequestAsync(session,
						new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser user,
									Response response) {
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}
								if (user != null) {
									Map<String, Object> responseMap = new HashMap<String, Object>();
									GraphObject graphObject = response
											.getGraphObject();
									responseMap = graphObject.asMap();
									Log.i("FbLogin", "Response Map KeySet - "
											+ responseMap.keySet());
									// TODO : Get Email
									// responseMap.get("email");
									JSONObject userProfile = new JSONObject();
									try {
										userProfile.put("id", user.getId());
										userProfile.put("fullname",
												user.getName());
										if (user.getProperty("gender") != null) {
											userProfile.put("gender",
													user.getProperty("gender"));
										}
										if (user.getProperty("email") != null) {
											userProfile.put("email",
													user.getProperty("email"));
										}
										String imgPath = "https://graph.facebook.com/"
												+ user.getId()
												+ "/picture?type=large";
										userProfile.put("image", imgPath);

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									//
									if (responseMap.get("email") != null) {
										getUserFromServer(userProfile);
									} else {
										Session session = Session
												.getActiveSession();
										if (session != null) {
											session.closeAndClearTokenInformation();
										}
									}
								}
							}
						});
			} else {
				List<String> permissions = Arrays.asList("public_profile",
						"email", "user_checkins", "user_checkins");
				session.requestNewReadPermissions(new Session.NewPermissionsRequest(
						mActivity, permissions));
			}
		}
	}

	public void getUserFromServer(JSONObject object) {
		if (object == null) {
			Toast.makeText(mActivity, "Lỗi xác thực tài khoản!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		try {
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
									ConfigApp.CURRENT_USER.put(
											ConfigApp.PROFILE_KEY, response);
									Intent intent = new Intent(mActivity,
											MainActivity.class);
									mActivity.startActivity(intent);
									mActivity.finish();
								}
							} catch (Exception ex) {
								ex.printStackTrace();
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

	public void logout() {
		if (Session.getActiveSession() != null) {
			Session.getActiveSession().closeAndClearTokenInformation();
			Session.setActiveSession(null);
		}
	}
}
