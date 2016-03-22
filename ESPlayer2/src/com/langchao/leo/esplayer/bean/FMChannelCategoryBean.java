package com.langchao.leo.esplayer.bean;

import java.util.List;

/**
 * 频道分类
 * @author 碧空
 *
 */
public class FMChannelCategoryBean {

	/**
	 * 分类ID
	 */
	private String cateId = "";
	
	/**
	 * 分类名称
	 */
	private String cateName = "";
	
	/**
	 * 频道列表
	 */
	private List<FMChannelBean> channelList = null;

	public String getCateId() {
		return cateId;
	}

	public void setCateId(String cateId) {
		this.cateId = cateId;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public List<FMChannelBean> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<FMChannelBean> channelList) {
		this.channelList = channelList;
	}

	@Override
	public String toString() {
		return "FMChannelCategoryBean [cateId=" + cateId + ", cateName="
				+ cateName + ", channelList=" + channelList + "]";
	} 
	
}
