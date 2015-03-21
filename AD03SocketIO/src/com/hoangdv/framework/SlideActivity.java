package com.hoangdv.framework;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hoangdv.framework.adapters.NavDrawerListAdapter;
import com.hoangdv.framework.app.AppController;
import com.hoangdv.framework.fragments.ChatRoomFragment;
import com.hoangdv.framework.fragments.DownloadFilesFragment;
import com.hoangdv.framework.fragments.VbookFragment;
import com.hoangdv.framework.models.NavDrawerItem;
import com.hoangdv.framework.utils.ConfigApp;
import com.hoangdv.framework.utils.CustomRequest;

@SuppressWarnings("deprecation")
public class SlideActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	// private String[] navMenuTitles;
	public static Vector<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	// Video
	static VideoView videoview;
	static MediaController mediacontroller;
	static String VideoURL = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	static ProgressDialog dialog = null;

	//
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slider);

		/*
		 * Get thong tin phong
		 */

		try {
			// HH
			// dialog = ProgressDialog.show(this, "", "Loading...", true,
			// false);

			Log.e("ROOM", ConfigApp.CURRENT_ROOM_ID);

			// Luu rooom id
			// ConfigApp.CURRENT_ROOM_ID = room.getUserID();

			JSONObject userProfile = ConfigApp.CURRENT_USER
					.getJSONObject("profile");

			JsonObjectRequest request = new JsonObjectRequest(Method.GET,
					ConfigApp.URL_GET_LINKVIDEO_MSG + "/"
							+ ConfigApp.CURRENT_ROOM_ID + "/"
							+ userProfile.getString("id"), null,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							// TODO Auto-generated method stub
							if (dialog != null)
								dialog.dismiss();
							Log.d("Slider", response.toString());

							// ConfigApp.CURRENT_ROOM_DEFAULT = response;

							try {
								playVideoMain(
										response.getString("link_player"),
										SlideActivity.this);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							if (dialog != null)
								dialog.dismiss();
						}
					}) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					// TODO Auto-generated method stub
					Map<String, String> map = new HashMap<String, String>();
					map.put("Authorization", ConfigApp.API_KEY);
					map.put("Content-Type", "application/json");
					map.put("charset", "utf-8");
					return map;
				}
			};



			
			AppController.getInstance().addToRequestQueue(request);
			videoview = (VideoView) findViewById(R.id.videoView_default);
			// playVideoMain(VideoURL, SlideActivity.this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//

		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new Vector<NavDrawerItem>();

		try {
			JSONObject userProfile = ConfigApp.CURRENT_USER
					.getJSONObject("profile");

			ImageLoader imageLoader = AppController.getInstance()
					.getImageLoader();

			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View headerView = inflater.inflate(R.layout.profile_layout,
					mDrawerList, false);
			ImageView imageView = (ImageView) headerView
					.findViewById(R.id.imageView_avata);
			TextView tv_name = (TextView) headerView
					.findViewById(R.id.textView_name);
			tv_name.setText(Html.fromHtml(
					"<u>" + userProfile.getString("fullname") + "</u>")
					.toString());
			TextView tv_username = (TextView) headerView
					.findViewById(R.id.textView_username);
			tv_username.setText(userProfile.getString("username"));

			imageLoader.get(userProfile.getString("image"), ImageLoader
					.getImageListener(imageView, R.drawable.avata,
							R.drawable.avata));

			imageView.setImageResource(R.drawable.avata);
			headerView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(SlideActivity.this, "UserName",
							Toast.LENGTH_SHORT).show();
				}
			});
			mDrawerList.addHeaderView(headerView, null, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// end profile

		// adding nav drawer items to array
		// Home
		// navMenuTitles = new String[] { "Title 1", "Title 2" };
		navDrawerItems.add(new NavDrawerItem("Chat", R.drawable.ic_chat, true,
				"10"));
		navDrawerItems.add(new NavDrawerItem("Download Files",
				R.drawable.ic_download, true, "22"));
		navDrawerItems.add(new NavDrawerItem("Vbooks", R.drawable.ic_video,
				true, "50+"));
		navDrawerItems.add(new NavDrawerItem("Chat Room",
				android.R.drawable.ic_media_previous));
		navDrawerItems.add(new NavDrawerItem("Logout",
				android.R.drawable.ic_lock_power_off));
		// Photos

		// Recycle the typed array
		// navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(1);
			mDrawerList.setItemChecked(1, true);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@SuppressLint("NewApi")
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 1:
			ConfigApp.goOut = false;
			fragment = new ChatRoomFragment();
			break;
		case 2:
			ConfigApp.goOut = true;
			fragment = new DownloadFilesFragment();
			break;
		case 3:
			ConfigApp.goOut = true;
			fragment = new VbookFragment();
			break;
		case 4:
			ConfigApp.goOut = true;
			finish();
			break;
		case 5: // Logout
			try {
				ConfigApp.CURRENT_USER.put(ConfigApp.PROFILE_KEY, null);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ConfigApp.goOut = true;
			setResult(111);
			finish();
			break;
		case 6:
			// fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		// HH25/Feb/2015
		// while (dialog.isShowing()) {
		//
		// }

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navDrawerItems.get(position - 1).getTitle());
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public static void playVideoMain(String urlVideo, Context context) {
		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = new ProgressDialog(context);
		dialog.setMessage("Buffering...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		// dialog.show();
		try {
			// Start the MediaController
			mediacontroller = new MediaController(context);
			mediacontroller.setPadding(0, 0, 0, 0);
			// Get the URL from String VideoURL
			Uri video = Uri.parse(urlVideo);
			videoview.setMediaController(mediacontroller);
			// videoview.setVideoURI(video);
			videoview.setVideoPath(urlVideo);

		} catch (Exception e) {
			if (dialog != null) {
				dialog.dismiss();
			}
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		videoview.requestFocus();

		videoview.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				mediacontroller.setAnchorView(videoview);
				mp.setLooping(true);
				if (dialog != null)
					dialog.dismiss();
				// videoview.start();
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ConfigApp.goOut = true;
	}
}
