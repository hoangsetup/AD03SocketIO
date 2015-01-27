package com.hoangdv.framework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hoangdv.framework.utils.FacebookLoginControler;
import com.hoangdv.framework.utils.GooglePlusLoginControler;
import com.hoangdv.framework.utils.NormalUserLoginControler;
import com.hoangdv.framework.utils.TwitterLoginControler;
import com.parse.ParseFacebookUtils;

@SuppressLint("NewApi")
public class LoginActivity extends Activity {
	private EditText edtxtUsername, edtxtPassword;
	private CheckBox chkKeeplogin;
	private Button btnLogin;
	private static String sUsername, sPassword;
	private ImageButton imgFB, imgGG, imgTT;

	// private Dialog progressDialog;

	private GooglePlusLoginControler plusLoginControler;
	private NormalUserLoginControler normalUserLoginControler;
	private FacebookLoginControler facebookLoginControler;
	private TwitterLoginControler twitterLoginControler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		this.getActionBar().hide();
		getWidgetControl();
		registryEvent();

		plusLoginControler = new GooglePlusLoginControler(this);
		normalUserLoginControler = new NormalUserLoginControler(this);
		facebookLoginControler = new FacebookLoginControler(this);
		twitterLoginControler = new TwitterLoginControler(this);
		twitterLoginControler.getUserFromServer();

	}

	public void getWidgetControl() {
		edtxtUsername = (EditText) findViewById(R.id.editText_user);
		edtxtPassword = (EditText) findViewById(R.id.editText_password);
		btnLogin = (Button) findViewById(R.id.button_login);
		chkKeeplogin = (CheckBox) findViewById(R.id.checkBox_keeplogin);
		imgFB = (ImageButton) findViewById(R.id.imageButton_signinfb);
		imgGG = (ImageButton) findViewById(R.id.imageButton_signingg);
		imgTT = (ImageButton) findViewById(R.id.imageButton_signintt);

	}

	public void registryEvent() {
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkLoginInfo()) {
					// loginNormalByAccount(sUsername, sPassword);
					normalUserLoginControler.logon(sUsername, sPassword);
				}
			}
		});
		imgFB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// signinWithFacebook();
				facebookLoginControler.login();
			}
		});

		imgGG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				plusLoginControler.login();
			}
		});

		imgTT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				twitterLoginControler.login();
			}
		});
	}

	public boolean checkLoginInfo() {
		sUsername = edtxtUsername.getText().toString();
		sPassword = edtxtPassword.getText().toString();
		if (TextUtils.isEmpty(sUsername) || TextUtils.isEmpty(sPassword)) {
			if (TextUtils.isEmpty(sUsername))
				edtxtUsername.setError("*");
			if (TextUtils.isEmpty(sPassword))
				edtxtPassword.setError("*");
			return false;
		}
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getSharedPreferences("data_login",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		// if (chkKeeplogin.isChecked()) {
		editor.putString("username", sUsername);
		editor.putString("password", sPassword);
		editor.putBoolean("checked", chkKeeplogin.isChecked());

		editor.commit();
		// Logout
		plusLoginControler.logout();

		// facebookLoginControler.logout();

		twitterLoginControler.logout();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			SharedPreferences preferences = getSharedPreferences("data_login",
					MODE_PRIVATE);
			chkKeeplogin.setChecked(preferences.getBoolean("checked", false));
			sUsername = preferences.getString("username", "");
			sPassword = preferences.getString("password", "");
			edtxtUsername.setText(sUsername);
			edtxtPassword.setText(sPassword);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == plusLoginControler.RC_SIGN_IN) {
			plusLoginControler.onActivityResult();
			Log.d("requestCode", requestCode + "");
			return;
		}

		Log.d("FbLogin", "Result Code is - " + resultCode + "");
		// Session.getActiveSession().onActivityResult(LoginActivity.this,
		// requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Session.getActiveSession().removeCallback(
		// facebookLoginControler.statusCallback);

	}
}
