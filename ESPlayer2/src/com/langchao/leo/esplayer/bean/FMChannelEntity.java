package com.langchao.leo.esplayer.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 频道实体
 * @author 碧空
 * 
 */
public class FMChannelEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String user_id;
	private String user_name;
	private String hash_code;
	private String channel_id;
	private String channel_name;
	private List<Song> list;
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}



	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}



	public String getHash_code() {
		return hash_code;
	}



	public void setHash_code(String hash_code) {
		this.hash_code = hash_code;
	}



	public String getChannel_id() {
		return channel_id;
	}



	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}



	public String getChannel_name() {
		return channel_name;
	}



	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}



	public List<Song> getList() {
		return list;
	}



	public void setList(List<Song> list) {
		this.list = list;
	}



	@Override
	public String toString() {
		return "FMChannelEntity [user_id=" + user_id + ", user_name="
				+ user_name + ", hash_code=" + hash_code + ", channel_id="
				+ channel_id + ", channel_name=" + channel_name + ", list="
				+ list + "]";
	}



	public class Song{
		private String id;
		private String type;
		private String method;
		private String flow_mark;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getFlow_mark() {
			return flow_mark;
		}
		public void setFlow_mark(String flow_mark) {
			this.flow_mark = flow_mark;
		}
		@Override
		public String toString() {
			return "Song [id=" + id + ", type=" + type + ", method=" + method
					+ ", flow_mark=" + flow_mark + "]";
		}
	}
}
