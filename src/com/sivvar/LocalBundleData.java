package com.sivvar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linphone.LinphoneService;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.sivvar.objects.IndustryDTO;
import com.sivvar.objects.ServiceDTO;


public class LocalBundleData {

	private static final String PREFS_NAME = "com_sivvar";
	private static final String LOCAL_DATA_IMEI = "com_sivvar_imei";
	private static final String LOCAL_DATA_MSISDN = "com_sivvar_msisdn";
	private static final String LOCAL_DATA_SMS_REGISTRATION_CODE = "com_sivvar_sms_reg_code";
	private static final String LOCAL_DATA_LOGIN_FAILED_NUMBER = "com_sivvar_login_failed_number";
	private static final String LOCAL_DATA_GO_AHEAD_OVER_SUBSCRIBE = "com_sivvar_go_ahead_over_subscribe";
	private static final String LOCAL_DATA_ACCOUNT_VERIFIED = "com_sivvar_account_verified";
	
	public static List<ServiceDTO> favouriteServiceList = null;
	public static Map<String, List<ServiceDTO>> serviceList = new HashMap<String, List<ServiceDTO>>();
	public static List<IndustryDTO> industryList = null;
	public static Map<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();
	public static Map<String, ServiceDTO> services = new HashMap<String, ServiceDTO>();
	
	public static String getIMEI() {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(LOCAL_DATA_IMEI, "");
	}
	
	public static void setIMEI(String imei) {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LOCAL_DATA_IMEI, imei);
		editor.commit();
	}
	
	public static String getMSISDN() {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(LOCAL_DATA_MSISDN, "");
	}
	
	public static void setMSISDN(String msisdn) {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LOCAL_DATA_MSISDN, msisdn);
		editor.commit();
	}
	
	public static String getSMSRegistrationCode() {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(LOCAL_DATA_SMS_REGISTRATION_CODE, "");
	}
	
	public static void setSMSRegistrationCode(String code) {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LOCAL_DATA_SMS_REGISTRATION_CODE, code);
		editor.commit();
	}
	
	public static int getLoginFailedNumber() {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt(LOCAL_DATA_LOGIN_FAILED_NUMBER, 0);
	}
	
	public static void setLoginFailedNumber(int number) {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(LOCAL_DATA_LOGIN_FAILED_NUMBER, number);
		editor.commit();
	}
	
	public static boolean getGoAheadOverSubscribe() {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(LOCAL_DATA_GO_AHEAD_OVER_SUBSCRIBE, false);
	}
	
	public static void setGoAheadOverSubscribe(boolean goHead) {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(LOCAL_DATA_GO_AHEAD_OVER_SUBSCRIBE, goHead);
		editor.commit();
	}
	
	public static boolean getAccountVerified() {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(LOCAL_DATA_ACCOUNT_VERIFIED, false);
	}
	
	public static void setAccountVerified(boolean verified) {
		SharedPreferences settings = LinphoneService.instance()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(LOCAL_DATA_ACCOUNT_VERIFIED, verified);
		editor.commit();
	}
	
}
