package com.langchao.leo.esplayer.interfaces;

/**
 * 
 * @author 碧空
 * 
 */
public interface OnOperationActionListener {
	
	/**
	 * 删除操作Action
	 */
	public static final int ACTION_OPERATION_DELETE = 0;
	
	
	
	/**
	 * 处理操作行为
	 * 
	 * @param action
	 */
	public void handleAction(int action);
}