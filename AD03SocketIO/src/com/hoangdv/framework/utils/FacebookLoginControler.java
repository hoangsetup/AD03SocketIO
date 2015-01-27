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
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.hoangdv.framework.MainActivity;
import com.hoangdv.framework.app.AppController;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class FacebookLoginControler {
	private Activity mActivity;
	private Dialog dialog;

	public FacebookLoginControler(Activity activity) {
		this.mActivity = activity;
		
		try{
			ParseUser.logOut();
		}catch(Exception ex){
			
		}
	}

	private boolean isFbLogin() {
		Session session = Session.getActiveSession();
		return (session != null && session.isOpened());
	}

	public void login() {
		dialog = ProgressDialog.show(mActivity, "", "Đang xác thực...", true,
				false);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			getFacebookUserProfile();
			return;
		}

		List<String> permissions = Arrays.asList("public_profile", "email",
				"user_checkins", "user_checkins");

		ParseFacebookUtils.logIn(permissions, mActivity, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				dialog.dismiss();
				if (user == null) {
					Log.d("TAG",
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d("TAG",
							"User signed up and logged in through Facebook!");
					getFacebookUserProfile();
				} else {
					Log.d("TAG", "User logged in through Facebook!");
					getFacebookUserProfile();
				}
			}
		});
	}

	public void getFacebookUserProfile() {
		ParseUser currentUser = ParseUser.getCurrentUser();

		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			com.facebook.Request request = com.facebook.Request.newMeRequest(
					ParseFacebookUtils.getSession(),
					new com.facebook.Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								com.facebook.Response response) {
							JSONObject userProfile = new JSONObject();
							try {
								// Populate the JSON object
								if (user == null) {
									if (dialog != null)
										dialog.dismiss();
									Toast.makeText(mActivity,
											"Lỗi xác thực tài khoản!",
											Toast.LENGTH_SHORT).show();
									return;
								}
								userProfile.put("id", user.getId());
								userProfile.put("fullname", user.getName());
								if (user.getProperty("gender") != null) {
									userProfile.put("gender",
											user.getProperty("gender"));
								}
								if (user.getProperty("email") != null) {
									userProfile.put("email",
											user.getProperty("email"));
								}
								String imgPath = "https://graph.facebook.com/"
										+ user.getId() + "/picture?type=large";
								userProfile.put("image", imgPath);
								getUserFromServer(userProfile);
							} catch (JSONException e) {
								Log.d("GetUSER",
										"Error parsing returned user data. "
												+ e);
							}
						}

					});
			request.executeAsync();
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
									
									if (dialog != null)
										dialog.dismiss();
									
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
