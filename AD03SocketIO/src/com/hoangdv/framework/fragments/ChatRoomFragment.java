package com.hoangdv.framework.fragments;

import io.vov.vitamio.utils.Log;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.hoangdv.framework.R;
import com.hoangdv.framework.ReplyActivity;
import com.hoangdv.framework.adapters.MessageAdapter;
import com.hoangdv.framework.app.AppController;
import com.hoangdv.framework.models.MessageItem;
import com.hoangdv.framework.utils.ConfigApp;
import com.hoangdv.framework.utils.CustomRequest;

@SuppressLint("NewApi")
public class ChatRoomFragment extends Fragment {
	private View rootView;

	// = 0 - Thao luan, =1 - Chu phong
	public static int room_type = 0;
	private Vector<MessageItem> messageItems_global = new Vector<MessageItem>();
	private Vector<MessageItem> messageItems_owner = new Vector<MessageItem>();
	private ListView listViewChatLog_global;
	private ListView listViewChatLog_owner;

	private MessageAdapter adapter_global = null;
	private MessageAdapter adapter_owner = null;

	// textedit msg
	private EditText inputMsgGlobal, inputMsgOwner;
	private ImageButton buttonSendGlobal, buttonSendOwner;

	// Socket
	private Socket socket;
	{
		try {
			socket = IO.socket(ConfigApp.IP_SERVER_CHAT);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ChatRoomFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_chatroom, container,
				false);

		initTab();
		getInitWidgetControl();
		// getMsgInJsonGlobal();
		getRoomInfo();
		listenerEvent();

		//
		initSocketIoClient();
		return rootView;
	}

	private void getInitWidgetControl() {
		listViewChatLog_global = (ListView) rootView
				.findViewById(R.id.listView_chat_global);
		listViewChatLog_owner = (ListView) rootView
				.findViewById(R.id.listView_chat_owner);
		adapter_global = new MessageAdapter(getActivity(), messageItems_global);
		adapter_owner = new MessageAdapter(getActivity(), messageItems_owner);
		listViewChatLog_global.setAdapter(adapter_global);
		listViewChatLog_owner.setAdapter(adapter_owner);

		inputMsgGlobal = (EditText) rootView
				.findViewById(R.id.editText_msg_global);
		inputMsgOwner = (EditText) rootView
				.findViewById(R.id.editText_msg_owner);

		buttonSendGlobal = (ImageButton) rootView
				.findViewById(R.id.imageButton_send_global);
		buttonSendOwner = (ImageButton) rootView
				.findViewById(R.id.imageButton_send_owner);
	}

	private void listenerEvent() {
		buttonSendGlobal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptSendGlobal();
			}
		});
		buttonSendOwner.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		listViewChatLog_global
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), ReplyActivity.class);
						MessageItem itemT = messageItems_global.elementAt(arg2);
						Bundle bundle = new Bundle();
						bundle.putSerializable("msg", itemT);
						intent.putExtra("bundle", bundle);
						startActivityForResult(intent, 113);
						return false;
					}
				});

		listViewChatLog_owner
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						return false;
					}
				});
	}

	private void getRoomInfo() {
		try {
			JSONObject userProfile = ConfigApp.CURRENT_USER
					.getJSONObject("profile");
			CustomRequest request = new CustomRequest(
					ConfigApp.URL_GET_LINKVIDEO_MSG + "/"
							+ ConfigApp.CURRENT_ROOM_ID + "/"
							+ userProfile.getString("id"), null,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							// TODO Auto-generated method stub
							Log.d("CustomUTF8", response.toString());
							ConfigApp.CURRENT_ROOM_DEFAULT = response;

							getMsgInJsonGlobal();
							getMsgInJsonOwner();
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.d("CustomUTF8_ERROR", error.toString());
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
			Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void getMsgInJsonGlobal() {
		Log.d("Chatroom: GetMsgJson", ConfigApp.CURRENT_ROOM_DEFAULT.toString());
		try {
			JSONArray array = ConfigApp.CURRENT_ROOM_DEFAULT.getJSONObject(
					"msg").getJSONArray("global");

			Log.d("-----------", array.toString());

			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				MessageItem item = new MessageItem();
				item.setId(json.getString("idm"));
				item.setUsername(json.getString("user"));
				item.setMessage(json.getString("msg"));
				item.setTime(json.getString("time"));
				item.setType(1);
				Log.d("ChatRoomFrag_GetMsgINJSON", item.getUsername() + ":"
						+ item.getMessage());

				if (json.has("answer")) {
					item.setReply(true);
					JSONArray repArray = json.getJSONArray("answer");
					for (int j = 0; j < repArray.length(); j++) {
						MessageItem itemR = new MessageItem();
						JSONObject objectRep = repArray.getJSONObject(j);
						itemR.setUsername(objectRep.getString("user"));
						itemR.setMessage(objectRep.getString("msg"));
						itemR.setTime(objectRep.getString("time"));
						item.addReply(itemR);
					}
				}

				// messageItems_global.add(item);
				messageItems_global.insertElementAt(item, 0);

			}
			if (messageItems_global.size() > 0) {
				adapter_global.notifyDataSetChanged();
			}
			// Collections.reverse(messageItems_global);
			if (messageItems_global.size() > 0)
				adapter_global.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getMsgInJsonOwner() {
		try {
			JSONArray array = ConfigApp.CURRENT_ROOM_DEFAULT.getJSONObject(
					"msg").getJSONArray("owner");
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				MessageItem item = new MessageItem();
				item.setId(json.getString("idm"));
				item.setUsername(json.getString("user"));
				item.setMessage(json.getString("msg"));
				item.setTime(json.getString("time"));
				item.setType(1);

				if (json.has("answer")) {
					item.setReply(true);
					JSONArray repArray = json.getJSONArray("answer");
					for (int j = 0; j < repArray.length(); j++) {
						MessageItem itemR = new MessageItem();
						JSONObject objectRep = repArray.getJSONObject(j);
						itemR.setUsername(objectRep.getString("user"));
						itemR.setMessage(objectRep.getString("msg"));
						itemR.setTime(objectRep.getString("time"));
						item.addReply(itemR);
					}
				}

				messageItems_owner.add(item);
			}
			Collections.reverse(messageItems_owner);
			if (messageItems_owner.size() > 0)
				adapter_owner.notifyDataSetChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initTab() {
		final TabHost tabHost = (TabHost) (rootView.findViewById(R.id.tabhost));
		tabHost.setup();
		TabHost.TabSpec tabSpec;
		// Setup tab 1
		tabSpec = tabHost.newTabSpec("tab01");
		tabSpec.setContent(R.id.tab1);
		tabSpec.setIndicator("Thảo luận");
		tabHost.addTab(tabSpec);
		// Setup tab 2
		tabSpec = tabHost.newTabSpec("tab02");
		tabSpec.setContent(R.id.tab2);
		tabSpec.setIndicator("Chủ phòng");
		tabHost.addTab(tabSpec);

		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabHost.getCurrentTab() == 0) {
					room_type = 0;
				} else {
					room_type = 1;
				}
			}
		});

		TabWidget widget = tabHost.getTabWidget();
		for (int i = 0; i < widget.getChildCount(); i++) {
			View v = widget.getChildAt(i);
			TextView tv = (TextView) v.findViewById(android.R.id.title);
			tv.setTextColor(Color.parseColor("#FFFFFF"));
		}
	}

	// - - Initsocket chat
	private void initSocketIoClient() {
		try {
			final JSONObject userProfile = ConfigApp.CURRENT_USER
					.getJSONObject("profile");
			Log.d("initSocketIoClient: ", userProfile.toString());

			// socket = IO.socket(ConfigApp.IP_SERVER_CHAT);

			socket.on(Socket.EVENT_CONNECT, onConnect).on(Socket.EVENT_ERROR,
					onError);

			// SetHeader

			socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {

				@Override
				public void call(Object... arg0) {
					// TODO Auto-generated method stub
					Transport transport = (Transport) arg0[0];
					transport.on(Transport.EVENT_REQUEST_HEADERS,
							new Emitter.Listener() {

								@Override
								public void call(Object... arg0) {
									// TODO Auto-generated method stub
									@SuppressWarnings("unchecked")
									Map<String, String> header = (Map<String, String>) arg0[0];
									try {
										header.put("cookie", "PHPSESSID="
												+ userProfile.getString("ssid"));
										// header.put("Authorization",
										// ConfigApp.API_KEY);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).on(Transport.EVENT_RESPONSE_HEADERS,
							new Emitter.Listener() {
								@Override
								public void call(Object... args) {
									@SuppressWarnings("unchecked")
									final Map<String, String> headers = (Map<String, String>) args[0];

									// get cookies from server.
								}
							});

				}
			});
			socket.connect();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Emitter.Listener onConnect = new Emitter.Listener() {

		@Override
		public void call(final Object... arg0) {
			// TODO Auto-generated method stub
			Log.d("Onconnection", "connect" + arg0.toString() + "-");
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(),
							"Connect: " + arg0.toString() + "-",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	private Emitter.Listener onError = new Emitter.Listener() {

		@Override
		public void call(final Object... arg0) {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(),
							"Error: " + arg0[0].toString(), Toast.LENGTH_SHORT)
							.show();
				}
			});

		}
	};

	//
	private void attemptSendGlobal() {
		if (!socket.connected())
			return;
		String msg = inputMsgGlobal.getText().toString().trim();
		if (TextUtils.isEmpty(msg)) {
			inputMsgGlobal.setError("*");
			return;
		}
		try {
			final JSONObject userProfile = ConfigApp.CURRENT_USER
					.getJSONObject("profile");
			String userName = userProfile.getString("username");
			java.text.DateFormat dateFormat = new SimpleDateFormat(
					"HH:mm dd/MM/yyyy");
			String time = dateFormat.format(Calendar.getInstance().getTime());
			MessageItem item = new MessageItem();
			item.setUsername(userName);
			item.setMessage(msg);
			item.setTime(time);
			item.setId(userProfile.getString("id"));
			messageItems_global.add(item);
			adapter_global.notifyDataSetChanged();
			inputMsgGlobal.setText("");
			listViewChatLog_global.smoothScrollToPosition(adapter_global
					.getCount() - 1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		socket.disconnect();
		socket.off(Socket.EVENT_CONNECT, onConnect);
		socket.off(Socket.EVENT_ERROR, onError);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
}
