package com.sivvar;

import java.util.ArrayList;
import java.util.List;

import org.linphone.LinphoneActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.core.LinphoneProxyConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sivvar.objects.IndustryDTO;
import com.sivvar.objects.LogoDownloading;
import com.sivvar.objects.ServiceDTO;
import com.sivvar.ui.grid.AssignImageTask;
import com.sivvar.ui.grid.DownloadImageTask;
import com.sivvar.webservices.WebserviceCallingFailedException;
import com.sivvar.webservices.WebserviceUtil;

public class AppUtil {

	public final static int MAX_FAILED_CALLING = 3; 
	public final static String SIP_SERVER = "104.131.205.147:50070"; // remember changing DEFAULT_DOMAIN in non_localizable_custom.xml
	
	public static String retrieveIMEI(Context ctx) {
		TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	public static String getRegistrationCodeFrom(String message) {
		String regCode = "";
		String[] parts = message.trim().split(" ");
		if (parts.length > 0) {
			regCode = parts[parts.length - 1];
		}
		return regCode;
	}
	
	public static void logout() {
		String username = LocalBundleData.getMSISDN();
		signOut(username, LinphoneActivity.instance().getString(R.string.default_domain));
		LinphoneActivity.instance().exit();
	}
	
	private static void signOut(String sipUsername, String domain) {
		if (LinphoneManager.isInstanciated()) {
			String sipAddress = sipUsername + "@" + domain;
			List<Integer> accountIndexes = findAuthIndexOf(sipAddress);
			for (Integer accountIndex : accountIndexes) {
				deleteAccount(accountIndex);
			}
		}
	}
	
	public static void deleteAccount(int n) {
		final LinphoneProxyConfig proxyCfg = getProxyConfig(n);

		if (proxyCfg != null)
			LinphoneManager.getLc().removeProxyConfig(proxyCfg);
		if (LinphoneManager.getLc().getProxyConfigList().length == 0) {
			LinphoneManager.getLc().refreshRegisters();
		} else {
			resetDefaultProxyConfig();
			LinphoneManager.getLc().refreshRegisters();
		}
	}
	
	// Accounts settings
	private static LinphoneProxyConfig getProxyConfig(int n) {
		LinphoneProxyConfig[] prxCfgs = LinphoneManager.getLc()
				.getProxyConfigList();
		if (n < 0 || n >= prxCfgs.length)
			return null;
		return prxCfgs[n];
	}
	
	public static void resetDefaultProxyConfig() {
		int count = LinphoneManager.getLc().getProxyConfigList().length;
		for (int i = 0; i < count; i++) {
			if (isAccountEnabled(i)) {
				LinphoneManager.getLc()
						.setDefaultProxyConfig(getProxyConfig(i));
				break;
			}
		}

		if (LinphoneManager.getLc().getDefaultProxyConfig() == null) {
			LinphoneManager.getLc().setDefaultProxyConfig(getProxyConfig(0));
		}
	}
	
	public static boolean isAccountEnabled(int n) {
		return getProxyConfig(n).registerEnabled();
	}
	
	private static List<Integer> findAuthIndexOf(String sipAddress) {
		int nbAccounts = LinphonePreferences.instance().getAccountCount();
		List<Integer> indexes = new ArrayList<Integer>();
		for (int index = 0; index < nbAccounts; index++) {
			String accountUsername = LinphonePreferences.instance()
					.getAccountUsername(index);
			String accountDomain = LinphonePreferences.instance()
					.getAccountDomain(index);
			String identity = accountUsername + "@" + accountDomain;
			if (sipAddress.contains(identity)) {
				indexes.add(index);
			}
		}
		return indexes;
	}
	
	public static void loadServiceDataFake() {
		if (LocalBundleData.favouriteServiceList == null) {
			LocalBundleData.favouriteServiceList = new ArrayList<ServiceDTO>();
			for (int index = 0; index < 10; index++) {
				ServiceDTO serviceDTO = new ServiceDTO();
				serviceDTO.setServiceName("testName");
				serviceDTO.setServiceStatus(ServiceDTO.SERVICE_STATUS_ACTIVE);
				serviceDTO.setServiceDialCode("thienau");
				serviceDTO.setServiceLogo("http://files.softicons.com/download/toolbar-icons/fatcow-hosting-icons-by-fatcow/png/32/battery_low.png");
				
				LocalBundleData.favouriteServiceList.add(serviceDTO);
				AppUtil.saveService(serviceDTO);
				AppUtil.loadLogo(serviceDTO, null, null);
			}
		}
		if (LocalBundleData.industryList == null) {
			LocalBundleData.industryList = new ArrayList<IndustryDTO>();
			for (int index = 0; index < 3; index++) {
				IndustryDTO industry = new IndustryDTO();
				industry.setIndustryName("testIndustry");
				industry.setIndustryLogo("http://files.softicons.com/download/toolbar-icons/fatcow-hosting-icons-by-fatcow/png/32/battery_low.png");
				LocalBundleData.industryList.add(industry);
			}
		}
		
		for (IndustryDTO industry : LocalBundleData.industryList) {
			String industryName = industry.getIndustryName();
			if (LocalBundleData.serviceList.get(industryName) == null) {
				List<ServiceDTO> services = new ArrayList<ServiceDTO>();
				for (int index = 0; index < 10; index++) {
					ServiceDTO serviceDTO = new ServiceDTO();
					serviceDTO.setServiceName("testName");
					serviceDTO.setServiceStatus(ServiceDTO.SERVICE_STATUS_ACTIVE);
					serviceDTO.setServiceDialCode("thienau");
					serviceDTO.setServiceLogo("http://files.softicons.com/download/toolbar-icons/fatcow-hosting-icons-by-fatcow/png/32/battery_low.png");
					
					services.add(serviceDTO);
					AppUtil.saveService(serviceDTO);
					AppUtil.loadLogo(serviceDTO, null, null);
				}
				LocalBundleData.serviceList.put(industryName, services);
			}
		}
	}
	
	public static void loadServiceData() {
		try {
			if (LocalBundleData.favouriteServiceList == null) {
				LocalBundleData.favouriteServiceList = WebserviceUtil
						.apiGetFavouriteServices();
				for (ServiceDTO serviceDTO : LocalBundleData.favouriteServiceList) {
					AppUtil.saveService(serviceDTO);
					AppUtil.loadLogo(serviceDTO, null, null);
				}
			}
			if (LocalBundleData.industryList == null) {
				LocalBundleData.industryList = WebserviceUtil
						.apiGetIndustryServices();
			}
			
			for (IndustryDTO industry : LocalBundleData.industryList) {
				String industryName = industry.getIndustryName();
				if (LocalBundleData.serviceList.get(industryName) == null) {
					List<ServiceDTO> services =  WebserviceUtil.apiGetIndustrySpecific(industryName);
					for (ServiceDTO serviceDTO : services) {
						AppUtil.saveService(serviceDTO);
						AppUtil.loadLogo(serviceDTO, null, null);
					}
					LocalBundleData.serviceList.put(industryName, services);
				}
			}
		} catch (WebserviceCallingFailedException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getAvatar(String serviceDialCode) {
		Bitmap result = null;
		ServiceDTO service = LocalBundleData.services.get(serviceDialCode);
		if (service != null) {
			result = LocalBundleData.cachedImages.get(service.getLogo());
		}
		return result;
	}
	
	public static void saveService(ServiceDTO service) {
		LocalBundleData.services.put(service.getServiceDialCode(), service);
	}
	
	public static void loadLogo(LogoDownloading logoDownloading, ImageView itemImage, ProgressBar loadingProgress) {
		boolean grayOut = false;
		if (logoDownloading.getStatus() != null) {
			grayOut = !ServiceDTO.SERVICE_STATUS_ACTIVE.equalsIgnoreCase(logoDownloading.getStatus());
		}
		
		Bitmap logoBitmap = LocalBundleData.cachedImages.get(logoDownloading.getLogo());
		if (logoBitmap == null) {
			if (logoDownloading.isLogoLoading()) {
				new AssignImageTask(itemImage, loadingProgress, grayOut).execute(logoDownloading.getLogo());
			} else {
				logoDownloading.setLogoLoading(true);
				new DownloadImageTask(itemImage, loadingProgress, grayOut, logoDownloading).execute(logoDownloading.getLogo());
			}
		} else {
			if (grayOut) {
				logoBitmap = AppUtil.toGrayscale(logoBitmap);
			}
			if (itemImage != null) {
				itemImage.setImageBitmap(logoBitmap);
			}
			if (loadingProgress != null) {
				loadingProgress.setVisibility(View.GONE);
			}
		}
	}
	
	public static Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}
	
}
