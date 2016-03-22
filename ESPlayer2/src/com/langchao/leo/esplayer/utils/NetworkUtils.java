package com.langchao.leo.esplayer.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * @author 碧空
 *
 */
public class NetworkUtils {

	public static final String NET_TYPE_WIFI = "WIFI";
	public static final String NET_TYPE_MOBILE = "MOBILE";
	public static final String NET_TYPE_NO_NETWORK = "no_network";

	private Context mContext = null;

	public NetworkUtils(Context pContext) {
		this.mContext = pContext;
	}

	public static final String IP_DEFAULT = "0.0.0.0";

	/**
	 * 网络连接是否可用
	 * 
	 * @param pContext
	 * @return
	 */
	public static boolean isConnectInternet(final Context pContext) {
		final ConnectivityManager conManager = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}

		return false;
	}
	
	/**
	 * 是否连接到Wi-Fi
	 * 
	 * @param pContext
	 * @return
	 */
	public static boolean isConnectWifi(final Context pContext) {
		ConnectivityManager mConnectivity = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		// 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
		int netType = -1;
		if (info != null) {
			netType = info.getType();
		}
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 获取网络类型名称
	 * 
	 * @param pNetType
	 * @return
	 */
	public static String getNetTypeName(final int pNetType) {
		switch (pNetType) {
		case 0:
			return "unknown";
		case 1:
			return "GPRS";
		case 2:
			return "EDGE";
		case 3:
			return "UMTS";
		case 4:
			return "CDMA: Either IS95A or IS95B";
		case 5:
			return "EVDO revision 0";
		case 6:
			return "EVDO revision A";
		case 7:
			return "1xRTT";
		case 8:
			return "HSDPA";
		case 9:
			return "HSUPA";
		case 10:
			return "HSPA";
		case 11:
			return "iDen";
		case 12:
			return "EVDO revision B";
		case 13:
			return "LTE";
		case 14:
			return "eHRPD";
		case 15:
			return "HSPA+";
		default:
			return "unknown";
		}
	}

	/**
	 * 获取当前IP地址
	 * 
	 * @return
	 */
	public static String getIPAddress() {
		try {
			final Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
					.getNetworkInterfaces();

			while (networkInterfaceEnumeration.hasMoreElements()) {
				final NetworkInterface networkInterface = networkInterfaceEnumeration
						.nextElement();

				final Enumeration<InetAddress> inetAddressEnumeration = networkInterface
						.getInetAddresses();

				while (inetAddressEnumeration.hasMoreElements()) {
					final InetAddress inetAddress = inetAddressEnumeration
							.nextElement();

					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}

			return NetworkUtils.IP_DEFAULT;
		} catch (final SocketException e) {
			return NetworkUtils.IP_DEFAULT;
		}
	}

	/**
	 * 判断手机是否采用手机默认联网方式
	 */
	public static boolean isMOBILEConnectde(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * Gps是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 获取连接类型名称
	 * 
	 * @return
	 */
	public String getConnTypeName() {
		ConnectivityManager connectivityManager = 
				(ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return NET_TYPE_NO_NETWORK;
		} else {
			return networkInfo.getTypeName();
		}
	}
	
}
