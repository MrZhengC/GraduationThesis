package com.langchao.leo.esplayer.bean;

/**
 * 本地频道信息
 * @author 碧空
 *
 */
public class FMChannelBean {

	/**
	 * 频道ID
	 */
	private String channel_id = "";
	
	/**
	 * 频道名称
	 */
	private String channel_name = "";
	
	/**
	 * 次序
	 */
	private int channel_order = 0;
	
	/**
	 * 频道分类ID
	 */
	private String cate_id = "";
	
	/**
	 * 频道分类名称
	 */
	private String cate = "";
	
	/**
	 * 频道分类次序
	 */
	private int cate_order = 0;
	
	/**
	 * 暂无使用地方
	 */
	private int pv_order = 0;
	
	/**
	 * 频道封面URL
	 */
	private String coverUrl = "";

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

	public int getChannel_order() {
		return channel_order;
	}

	public void setChannel_order(int channel_order) {
		this.channel_order = channel_order;
	}

	public String getCate_id() {
		return cate_id;
	}

	public void setCate_id(String cate_id) {
		this.cate_id = cate_id;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public int getCate_order() {
		return cate_order;
	}

	public void setCate_order(int cate_order) {
		this.cate_order = cate_order;
	}

	public int getPv_order() {
		return pv_order;
	}

	public void setPv_order(int pv_order) {
		this.pv_order = pv_order;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	@Override
	public String toString() {
		return "FMChannelBean [channel_id=" + channel_id + ", channel_name="
				+ channel_name + ", channel_order=" + channel_order
				+ ", cate_id=" + cate_id + ", cate=" + cate + ", cate_order="
				+ cate_order + ", pv_order=" + pv_order + ", coverUrl="
				+ coverUrl + "]";
	}
	
}
