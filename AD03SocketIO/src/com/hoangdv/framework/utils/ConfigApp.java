package com.hoangdv.framework.utils;

import org.json.JSONObject;

import android.widget.TextView;

public class ConfigApp {
	public static String IP_SERVER_CHAT = "10.0.3.2";
	public static int PORT_SERVER_CHAR = 8080;

	// public static String USER_NAME = "admin";
	public static int ROOM_NUM = 10;
	// Layout dang duoc chon trong slider,
	// neu la 1 thi hienj msg
	// <> luu vao cache list Textview
	public static int TAB_NUM = 1;
	public static TextView[] arr_textViews = new TextView[2048];
	public static boolean goOut = false;
	public static int msg_count = -1;

	public static JSONObject CURRENT_USER = new JSONObject();
	//
	public static String API_KEY = "72559437c0ef26f4d7a276684ce0ebbd";
	// public static String SERVER_ADD = "http://108.61.184.234";
	public static String SERVER_ADD = "http://dev.lophocmoi.com";

	public static String URL_LOGIN = SERVER_ADD + "/service/v1/login";
	public static String URL_LOGIN_OPENID = SERVER_ADD + "/service/v1/openid";
	public static String URL_GETROOMDATA = SERVER_ADD + "/service/v1/get_rooms";

	// Key in API
	public static String PROFILE_KEY = "profile";
	public static String ROOMS_KEY = "rooms";
	public static String NUMBER_CLASS_KEY = "number_class";
	public static String DATA_ROOM_KEY = "data";
	public static String NAME_CLASS_KEY = "name_class";
	public static String IDC_KEY = "idc";
	public static String IS_LIVE_KEY = "is_live";
	public static String NO_MORE_KEY = "no_more";

	public static String ERROR_KEY = "error";
	public static String MSG_KEY = "msg";
	public static String USERNAME_KEY = "username";
	public static String FULLNAME_KEY = "fullname";

	// Twitter
	static String TWITTER_CONSUMER_KEY = "QG5yb1spHJFcw3dp3ovywKPC4";
	static String TWITTER_CONSUMER_SECRET = "oqhfjAqxOZ4dKuLnwmE8Tyzuz0N1dw5Vg5HjK1SZumJZkZizJ6";

}
