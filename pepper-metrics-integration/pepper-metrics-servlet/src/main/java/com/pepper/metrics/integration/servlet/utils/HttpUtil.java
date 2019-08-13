package com.pepper.metrics.integration.servlet.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Created by zhangrongbin on 2018/9/28.
 */
public class HttpUtil {
	private static Pattern numberPattern = Pattern.compile("^/\\d+$");

	public static String getUrlWithoutNumber(String uri) {
		if (StringUtils.isEmpty(uri))
			return uri;
		int lastIndex = uri.lastIndexOf("/");
		if(lastIndex >= 0){
			String str = uri.substring(lastIndex);
			if(numberPattern.matcher(str).matches()){
				uri = uri.substring(0, lastIndex)+"/number";
			}
		}
		return uri;
	}

	public static void main(String[] args) {
		System.out.println(getPatternUrl("/666/api/666"));
		System.out.println(getPatternUrl("/666/777/666"));
		System.out.println(getPatternUrl("/api/777/news"));
		System.out.println(getPatternUrl("/"));
		System.out.println(getPatternUrl("/api/news/$post$appCurVersion=3.3.5.6&exposureTime=0&androidId=7bb2432b55d828f8&imei=868460031387753&userId=94375465&typeId=19&channel=F2qdx360&os=Android/"));
		System.out.println(getPatternUrl("/dexgp.php/"));
		System.out.println(getPatternUrl("/phpMyAdmin/scripts/setup.php/"));
	}
	private static final String[] IGNORE_POST_FIX = new String[]{".php", ".jpg", ".png", ".jpeng", ".css", ".js", ".jsp"};

	public static boolean profilerUrlCheck(String uri) {
		if (StringUtils.containsAny(uri, IGNORE_POST_FIX)) {
			return false;
		}
		return true;
	}

	public static String getPatternUrl(String uri) {
		if (StringUtils.isEmpty(uri))
			return uri;
		final String[] split = StringUtils.split(uri, '/');
		StringBuilder result = new StringBuilder("/");
		for (String s : split) {
			result.append(checkSegment(s) ? "xxx" : s).append("/");
		}
		return result.toString();
	}

	private static boolean checkSegment(String s) {
		return NumberUtils.isDigits(s) || !StringUtils.isAlphanumeric(s);
	}

	public static String getUrlEndpoint(String uri) {
		return StringUtils.substring(uri, 0, StringUtils.indexOf(uri, '/', 1));
	}

	public static String getClientRealIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("user-agent");
	}

	public static String getFullUrl(HttpServletRequest request) {
		if (StringUtils.isEmpty(request.getQueryString())) {
			return request.getRequestURI();
		} else {
			return new StringBuilder(request.getRequestURI()).append("?").append(request.getQueryString()).toString();
		}
	}
}
