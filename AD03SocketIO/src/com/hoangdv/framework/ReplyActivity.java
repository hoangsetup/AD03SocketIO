package com.hoangdv.framework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hoangdv.framework.models.MessageItem;

public class ReplyActivity extends Activity {
	private TextView username, time, message;
	private Button button_send, button_cancel;
	private EditText editText_msg_rely;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_replymsg);
		Bundle  bundle = this.getIntent().getBundleExtra("bundle");
		MessageItem item = (MessageItem) bundle.getSerializable("msg");
		
		showMsgToReply(item);
		getWidgetControl();
	}
	
	private void getWidgetControl(){
		button_cancel = (Button) findViewById(R.id.button_cancel);
		button_send = (Button) findViewById(R.id.button_send);
		
		username = (TextView) findViewById(R.id.username);
		time = (TextView) findViewById(R.id.textView_time);
		message = (TextView) findViewById(R.id.message);
		editText_msg_rely = (EditText) findViewById(R.id.editText_msg_reply);
	}
	
	private void showMsgToReply(MessageItem item){
		try{
			if(item == null)
				return;
			username.setText(item.getUsername());
			time.setText(item.getTime());
			message.setText(item.getMessage());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void registryEvent(){
		button_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
