package com.webber.simpleemail.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFormatUtil {
	/**
	 * 验证邮箱格式
	 * @param email
	 * @return
	 */
		public static boolean emailFormat(String email) {
			boolean tag = true;
			String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern pattern = Pattern.compile(pattern1);
			Matcher mat = pattern.matcher(email);
			if (!mat.find()) {
				tag = false;
			}
			return tag;
		}
}
