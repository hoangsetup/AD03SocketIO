package com.hoangdv.framework;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hoangdv.framework.utils.Utilities;

public class SplashScreen extends Activity {
	public static int SPLASH_TIME_OUT = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Utilities utilities = new Utilities(this);
		if (!utilities.isConnectingToInternet()) {
			ProgressDialog.show(this, "Thông báo",
					"Thiết bị không kết nối Internet", false, true,
					new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface arg0) {
							// TODO Auto-generated method stub
							((TextView) findViewById(R.id.textView_internet))
									.setVisibility(View.VISIBLE);
						}
					}).show();
			return;
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// /get hash key facebook
				try {
					PackageInfo info = getPackageManager().getPackageInfo(
							"com.hoangdv.framework",
							PackageManager.GET_SIGNATURES);
					for (Signature signature : info.signatures) {
						MessageDigest md;
						md = MessageDigest.getInstance("SHA");
						md.update(signature.toByteArray());
						String something = new String(Base64.encode(
								md.digest(), 0));
						Log.e("hash key", something);
					}
				} catch (NameNotFoundException e1) {
					Log.e("name not found", e1.toString());
				} catch (NoSuchAlgorithmException e) {
					Log.e("no such an algorithm", e.toString());
				} catch (Exception e) {
					Log.e("exception", e.toString());
				}

				// /
				Intent intent = new Intent(SplashScreen.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}
