package com.sivvar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumUtil {
	public static boolean isPhoneNumber(String str) {
		Pattern pattern = Pattern.compile("[0-9]{6,14}$");
	    Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
