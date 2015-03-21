package com.hoangdv.framework.models;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class MessageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String username;
	private String message;
	private boolean reply;
	private int type;
	private List<MessageItem> msgReply;
	private String time;

	public MessageItem() {
		this.reply = false;
		msgReply = new Vector<MessageItem>();
	}

	public MessageItem(String id, String username, String message,
			boolean reply, int type, List<MessageItem> msgReply, String time) {
		super();
		this.id = id;
		this.username = username;
		this.message = message;
		this.reply = reply;
		this.type = type;
		this.msgReply = msgReply;
		this.time = time;
	}
	
	public void addReply(MessageItem item){
		this.msgReply.add(item);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isReply() {
		return reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public List<MessageItem> getMsgReply() {
		return msgReply;
	}

	public void setMsgReply(List<MessageItem> msgReply) {
		this.msgReply = msgReply;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
