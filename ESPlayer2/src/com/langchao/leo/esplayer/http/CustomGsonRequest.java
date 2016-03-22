package com.langchao.leo.esplayer.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
	
/**
 * 自定义Request使用Okhttp访问网络，使用GSon解析json
 * Created by leo
 * on 2015/11/26
 * Description: customVolleyRequest
 */
public class CustomGsonRequest<T> extends Request<T> {
    
	private final Class<T> clazz;
	
    private final Map<String, String> headers;
    
    private final Response.Listener<T> listener;
    
    private Map<String, String> params;


    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url    URL of the request to make
     * @param clazz  Relevant class object, for Gson's reflection
     * @param params Map of request params
     */
    public CustomGsonRequest(String url, Class<T> clazz, Map<String, String> params,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = null;
        this.params = params;
        this.listener = listener;
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public CustomGsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                         Map<String, String> params,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }

    /**
     * @param builder requestBuilder
     */
    public CustomGsonRequest(RequestBuilder<T> builder) {
        super(builder.method, builder.url, builder.errorListener);
        clazz = builder.clazz;
        headers = builder.headers;
        listener = builder.successListener;
        params = builder.params;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (clazz == null) {
                return (Response<T>) Response.success(
                			parsed,
                			HttpHeaderParser.parseCacheHeaders(response)
                		);
            } else {
                return (Response<T>) Response.success(
                		GsonParser.toObject(parsed, clazz),
                		//gson.fromJson(parsed, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * requestBiulder  使用方法参见httpClientRequest
     */
    public static class RequestBuilder<T> {
        private int method = Method.GET;
        private String url;
        private Class<T> clazz;
        private Response.Listener<T> successListener;
        private Response.ErrorListener errorListener;
        private Map<String, String> headers;
        private Map<String, String> params;

        public RequestBuilder<T> url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder<T> clazz(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public RequestBuilder<T> successListener(Response.Listener<T> successListener) {
            this.successListener = successListener;
            return this;
        }

        public RequestBuilder<T> errorListener(Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        public RequestBuilder<T> post() {
            this.method = Method.POST;
            return this;
        }

        public RequestBuilder<T> method(int method) {
            this.method = method;
            return this;
        }

        public RequestBuilder<T> addHeader(String key, String value) {
            if (headers == null)
                headers = new HashMap<String, String>();
            headers.put(key, value);
            return this;
        }

        public RequestBuilder<T> headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestBuilder<T> params(Map<String, String> params) {
            post();
            this.params = params;
            return this;
        }

        public RequestBuilder<T> addParams(String key, String value) {
            if (params == null) {
                params = new HashMap<String, String>();
                post();
            }
            params.put(key, value);
            return this;
        }

        public RequestBuilder<T> addMethodParams(String method) {
            if (params == null) {
                params = new HashMap<String, String>();
                post();
            }
            params.put("method", method);
            return this;
        }

        public CustomGsonRequest<T> build() {
            return new CustomGsonRequest<T>(this);
        }
    }
}