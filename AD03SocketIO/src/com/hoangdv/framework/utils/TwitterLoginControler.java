package com.hoangdv.framework.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.hoangdv.framework.MainActivity;
import com.hoangdv.framework.app.AppController;

public class TwitterLoginControler {
	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	private Activity mActivity;

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	private Dialog dialog;

	public TwitterLoginControler(Activity activity) {
		this.mActivity = activity;
		// Shared Preferences
		mSharedPreferences = mActivity.getSharedPreferences("MyAccessTwitter",
				0);
	}

	public void login() {
		dialog = ProgressDialog.show(mActivity, "", "Đang xác thực...", true,
				false);
		// Check if already logged in
		new taskLoginTwitter().execute();
	}

	private class taskLoginTwitter extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (!isTwitterLoggedInAlready()) {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(ConfigApp.TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(ConfigApp.TWITTER_CONSUMER_SECRET);
				Configuration configuration = builder.build();

				TwitterFactory factory = new TwitterFactory(configuration);
				twitter = factory.getInstance();

				try {
					requestToken = twitter
							.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(requestToken.getAuthenticationURL())));
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {
				// user already logged into twitter
				Log.d("LOG", "Already Logged into twitter");
			}
			return null;
		}
	}

	private class taskGetUserFromServer extends
			AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... vars) {
			// TODO Auto-generated method stub
			if (!isTwitterLoggedInAlready()) {
				Uri uri = mActivity.getIntent().getData();
				if (uri != null
						&& uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
					// oAuth verifier
					String verifier = uri
							.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
					try {
						// Get the access token
						AccessToken accessToken = twitter.getOAuthAccessToken(
								requestToken, verifier);

						// Shared Preferences
						Editor e = mSharedPreferences.edit();

						// After getting access token, access token secret
						// store them in application preferences
						e.putString(PREF_KEY_OAUTH_TOKEN,
								accessToken.getToken());
						e.putString(PREF_KEY_OAUTH_SECRET,
								accessToken.getTokenSecret());
						// Store login status - true
						e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
						e.commit(); // save changes

						Log.e("Twitter OAuth Token",
								"> " + accessToken.getToken());
						long userID = accessToken.getUserId();
						User user = twitter.showUser(userID);
						String username = user.getName();
						String imgPath = user.getOriginalProfileImageURL();
						JSONObject userProfile = new JSONObject();
						userProfile.put("id", userID + "");
						userProfile.put("fullname", username);
						userProfile.put("image", imgPath);
						userProfile.put("email", "email@twitter.com");

						return userProfile;
					} catch (Exception e) {
						// Check log for login errors
						Log.e("Twitter Login Error", "> " + e.toString());
						return null;
					}
				}
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == null) {
				if (dialog != null) {
					dialog.dismiss();
				}
			} else {
				Log.d("obj", result.toString());
				getUserProfileFromServer(result);
			}
		}
	}

	public void getUserProfileFromServer(JSONObject object) {

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("uid", object.getString("id"));
			params.put("name", object.getString(ConfigApp.FULLNAME_KEY));
			params.put("provider", "twitter");
			params.put("email", object.getString("email"));
			params.put("image", object.getString("image"));
			Log.d("obj2", object.getString("image"));
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
									// Log.d("resVolley", response.toString());
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
	 * This if conditions is tested once is redirected from twitter page. Parse
	 * the uri to get oAuth Verifier
	 * */

	public void getUserFromServer() {
		new taskGetUserFromServer().execute();
	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	/**
	 * Function to logout from twitter It will just clear the application shared
	 * preferences
	 * */
	public void logout() {
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}

}
