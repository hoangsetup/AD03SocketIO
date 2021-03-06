/*
 * 
 */
package com.hoangdv.framework.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hoangdv.framework.utils.LruBitmapCache;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class AppController extends Application {
	public static final String TAG = AppController.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	public static AppController mInstance;

	@SuppressWarnings("static-access")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.mInstance = this;
		//
		Parse.initialize(this, "1505303586383700",
				"kGKY13TRTU8y8AvAGsVj00QXMPI=");
		ParseFacebookUtils.initialize("630355827068581");
		
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return this.mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag((TextUtils.isEmpty(tag) ? TAG : tag));
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
