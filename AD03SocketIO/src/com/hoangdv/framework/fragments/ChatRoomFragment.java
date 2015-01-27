package com.hoangdv.framework.fragments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoangdv.framework.R;
import com.hoangdv.framework.utils.ConfigApp;

@SuppressLint("NewApi")
public class ChatRoomFragment extends Fragment {
	// TextView tv_msg_rec, tv_msg_sen;
	LinearLayout layout_logchat;
	ScrollView scrollView_logchat;
	ImageButton imageButton_send;
	EditText editText_msg;

	TextView tv_msg_sen, tv_msg_rec;
	// int ConfigApp.msg_count = -1;

	public static ChatClientThread chatClientThread = null;

	public ChatRoomFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_chatroom, container,
				false);
		View tempView = inflater.inflate(R.layout.msg_layout_temp, null);

		layout_logchat = (LinearLayout) rootView
				.findViewById(R.id.layout_logchat);
		scrollView_logchat = (ScrollView) rootView
				.findViewById(R.id.scrollView_logchat);
		imageButton_send = (ImageButton) rootView
				.findViewById(R.id.imageButton_send);
		editText_msg = (EditText) rootView.findViewById(R.id.editText_msg);
		scrollView_logchat.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrollView_logchat.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		imageButton_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String msg = editText_msg.getText().toString();
				if (!TextUtils.isEmpty(msg)) {
					chatClientThread.sendMsg(msg);
					editText_msg.setText("");
					sendMessageToView(msg);
				} else {
					editText_msg.setError("SPAM!");
				}
			}
		});

		tv_msg_sen = (TextView) tempView.findViewById(R.id.textView_sen);
		tv_msg_rec = (TextView) tempView.findViewById(R.id.TextView_rec);
		try {
			ConfigApp.goOut = false;
			// ParseUser user = ParseUser.getCurrentUser();
			JSONObject jsonObject = ConfigApp.CURRENT_USER
					.getJSONObject(ConfigApp.PROFILE_KEY);
			if (chatClientThread == null) {
				chatClientThread = new ChatClientThread(
						jsonObject.getString(ConfigApp.USERNAME_KEY),
						ConfigApp.IP_SERVER_CHAT, ConfigApp.PORT_SERVER_CHAR);
				// chatClientThread.start();
			} else {
				chatClientThread.interrupt();
				chatClientThread = null;
				chatClientThread = new ChatClientThread(ConfigApp.USERNAME_KEY,
						ConfigApp.IP_SERVER_CHAT, ConfigApp.PORT_SERVER_CHAR);
				// chatClientThread.start();
			}

		} catch (Exception ex) {
			Toast.makeText(getActivity(),
					"Khong the ket noi may chu!\n" + ex.toString(),
					Toast.LENGTH_SHORT).show();
		}

		// layout_logchat.removeAllViews();
		for (int i = 0; i < ConfigApp.msg_count; i++) {
			ViewGroup parent = (ViewGroup) ConfigApp.arr_textViews[i]
					.getParent();
			parent.removeView(ConfigApp.arr_textViews[i]);
			layout_logchat.addView(ConfigApp.arr_textViews[i]);
		}
		return rootView;
	}

	public void sendMessageToView(String msg) {
		ConfigApp.msg_count++;
		ConfigApp.arr_textViews[ConfigApp.msg_count] = new TextView(
				getActivity());
		ConfigApp.arr_textViews[ConfigApp.msg_count].setLayoutParams(tv_msg_sen
				.getLayoutParams());
		ConfigApp.arr_textViews[ConfigApp.msg_count].setText(msg);
		ConfigApp.arr_textViews[ConfigApp.msg_count].setBackground(tv_msg_sen
				.getBackground());
		ConfigApp.arr_textViews[ConfigApp.msg_count].setTextColor(Color.WHITE);
		ConfigApp.arr_textViews[ConfigApp.msg_count].setSingleLine(false);
		ConfigApp.arr_textViews[ConfigApp.msg_count]
				.setGravity(Gravity.CENTER_VERTICAL);
		int minHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 32, getResources()
						.getDisplayMetrics());
		ConfigApp.arr_textViews[ConfigApp.msg_count]
				.setMinimumHeight(minHeight);

		layout_logchat.addView(ConfigApp.arr_textViews[ConfigApp.msg_count]);

		scrollView_logchat.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrollView_logchat.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

	}

	public void receiveMessageToView(String msg) {
		ConfigApp.msg_count++;
		ConfigApp.arr_textViews[ConfigApp.msg_count] = new TextView(
				getActivity());
		ConfigApp.arr_textViews[ConfigApp.msg_count].setLayoutParams(tv_msg_rec
				.getLayoutParams());
		ConfigApp.arr_textViews[ConfigApp.msg_count].setText(msg);
		ConfigApp.arr_textViews[ConfigApp.msg_count].setBackground(tv_msg_rec
				.getBackground());
		ConfigApp.arr_textViews[ConfigApp.msg_count].setTextColor(Color.WHITE);
		ConfigApp.arr_textViews[ConfigApp.msg_count].setSingleLine(false);
		ConfigApp.arr_textViews[ConfigApp.msg_count]
				.setGravity(Gravity.CENTER_VERTICAL);
		int minHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 32, getResources()
						.getDisplayMetrics());
		ConfigApp.arr_textViews[ConfigApp.msg_count]
				.setMinimumHeight(minHeight);

		if (!ConfigApp.goOut) {
			layout_logchat
					.addView(ConfigApp.arr_textViews[ConfigApp.msg_count]);
		}

		scrollView_logchat.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrollView_logchat.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	// CHatServer
	public class ChatClientThread extends Thread {

		String name;
		String dstAddress;
		int dstPort;

		String msgToSend = "";

		// boolean goOut = false;

		ChatClientThread(String name, String address, int port) {
			this.name = name;
			dstAddress = address;
			dstPort = port;
		}

		@Override
		public void run() {
			Socket socket = null;
			DataOutputStream dataOutputStream = null;
			DataInputStream dataInputStream = null;

			try {
				socket = new Socket(dstAddress, dstPort);
				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream.writeUTF(name);
				dataOutputStream.flush();

				while (!ConfigApp.goOut) {
					if (dataInputStream.available() > 0) {
						// msgLog += dataInputStream.readUTF();
						final String msg_rec = dataInputStream.readUTF();
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								receiveMessageToView(msg_rec);
							}
						});
					}

					if (!msgToSend.equals("")) {
						dataOutputStream.writeUTF(msgToSend);
						dataOutputStream.flush();
						Log.e("Task", "dâttatatat");
						msgToSend = "";
					}
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
				final String eString = e.toString();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), eString,
								Toast.LENGTH_LONG).show();
					}

				});
			} catch (IOException e) {
				e.printStackTrace();
				final String eString = e.toString();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), eString,
								Toast.LENGTH_LONG).show();
					}

				});
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dataInputStream != null) {
					try {
						dataInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// getActivity().runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// // loginPanel.setVisibility(View.VISIBLE);
				// // chatPanel.setVisibility(View.GONE);
				// }
				//
				// });
			}

		}

		private void sendMsg(String msg) {
			msgToSend = msg;

		}

		public void disconnect() {
			ConfigApp.goOut = true;
		}
	}
}
