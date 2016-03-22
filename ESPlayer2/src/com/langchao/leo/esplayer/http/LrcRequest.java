package com.langchao.leo.esplayer.http;

import java.util.List;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.langchao.leo.esplayer.contstants.NetConstants;
import com.langchao.leo.esplayer.ui.widget.lrc.DefaultLrcParser;
import com.langchao.leo.esplayer.ui.widget.lrc.LrcRow;

/**
 * 获取歌词
 * @author 碧空
 *
 */
public class LrcRequest extends Request<List<LrcRow>> {

	private final Response.Listener<List<LrcRow>> listener;
	
	/**
	 * 解析锁我们同时只解析一首歌的歌词
	 */
	private static final Object sParserLock = new Object();
	
	public LrcRequest(String url, 
			Response.Listener<List<LrcRow>> listener, 
			ErrorListener errorListener) {
		this(Method.GET, NetConstants.BASE_LRC_URL + url, listener, errorListener);
	}
	
	public LrcRequest(int method, String url, 
			Response.Listener<List<LrcRow>> listener, 
			ErrorListener errorListener) {
		super(method, url, errorListener);
		this.listener = listener;
	}

	@Override
	protected void deliverResponse(List<LrcRow> response) {
		if (listener != null)
			listener.onResponse(response);
	}

	@Override
	protected Response<List<LrcRow>> parseNetworkResponse(
			NetworkResponse response) {
		synchronized (sParserLock) {
			try {
				return doParse(response);
			} catch (Exception e) {
				return Response.error(new ParseError(e));
			}
		}
	}

	/**
	 * 真正的获取歌词方法
	 * @param response
	 * @return
	 */
    private Response<List<LrcRow>> doParse(NetworkResponse response) {
    	byte[] data = response.data;
    	List<LrcRow> mLrcRows = null;
    	
    	// 判断是否自动下载，写入文件
    	
    	String lrcString = new String(data);
    	
//    	String lrcString = "";
//		try {
//			lrcString = new String(data, HttpHeaderParser.parseCharset(response.headers);
//		} catch (UnsupportedEncodingException e) {
//			return Response.error(new ParseError(e));
//		}
    	mLrcRows = DefaultLrcParser.getIstance().getLrcRows(lrcString);
    	
    	if (mLrcRows == null) {
            return Response.error(new ParseError(response));
        } else {
            return Response.success(mLrcRows, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

	
}
