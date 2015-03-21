package com.hoangdv.framework.adapters;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hoangdv.framework.R;
import com.hoangdv.framework.models.MessageItem;

public class MessageAdapter extends BaseAdapter {
	private Vector<MessageItem> items = new Vector<MessageItem>();
	private Context context;

	public MessageAdapter(Context arg1, Vector<MessageItem> messageItems) {
		this.context = arg1;
		this.items = messageItems;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return items.indexOf(items.get(arg0));
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// if (arg1 == null) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arg1 = inflater.inflate(com.hoangdv.framework.R.layout.item_message,
				null);
		// }
		MessageItem item = items.get(arg0);
		// LinearLayout layout = (LinearLayout) arg1
		// .findViewById(com.hoangdv.framework.R.id.linearLayout_chatItem);

		TextView txt_username = (TextView) arg1
				.findViewById(com.hoangdv.framework.R.id.username);
		TextView txt_message = (TextView) arg1
				.findViewById(com.hoangdv.framework.R.id.message);
		TextView txt_rep = (TextView) arg1
				.findViewById(com.hoangdv.framework.R.id.textView_reply);
		TextView txt_time = (TextView) arg1.findViewById(R.id.textView_time);
		// check has reply
		if (item.isReply()) {
			txt_rep.setVisibility(View.VISIBLE);
			StringBuilder builder = new StringBuilder();
			for (MessageItem i : item.getMsgReply()) {
				builder.append("'" + i.getUsername() + " - " + i.getTime()
						+ ": " + i.getMessage() + "'");
				if (item.getMsgReply().indexOf(i) != item.getMsgReply().size() - 1) {
					builder.append("\n");
				}
			}
			txt_rep.setText(builder.toString());
		} else {
			txt_rep.setVisibility(View.GONE);
		}

		// CHange color
		if (item.getType() == 0) {// sen

		} else {// rec

		}

		//
		txt_time.setText(item.getTime());
		txt_username.setText(item.getUsername());
		txt_message.setText(item.getMessage());

		return arg1;
	}
}
