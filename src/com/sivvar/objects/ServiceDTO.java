package com.sivvar.objects;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class ServiceDTO implements LogoDownloading {

	public static final String SERVICE_STATUS_ACTIVE = "active";
	public static final String SERVICE_STATUS_INACTIVE = "inactive";
	public static final String SERVICE_STATUS_DISABLED = "disabled";
	public static final String SERVICE_STATUS_SUSPENDED = "suspended";
	
	// self variables
	private boolean logoLoading;
	
	private String serviceName;
	private String serviceLogo;
	private String industryName;
	private boolean hasSubService;
	private String serviceDialCode;
	private String serviceStatus;
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceLogo() {
		return serviceLogo;
	}
	public void setServiceLogo(String serviceLogo) {
		this.serviceLogo = serviceLogo;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	public boolean hasSubService() {
		return hasSubService;
	}
	public void setHasSubService(boolean hasSubService) {
		this.hasSubService = hasSubService;
	}
	public void setHasSubService(String hasSubService) {
		this.hasSubService = true;
		if (hasSubService.equals("NO")) {
			this.hasSubService = false;
		} else if (hasSubService.equals("YES")) {
			this.hasSubService = true;
		}
	}
	public String getServiceDialCode() {
		return serviceDialCode;
	}
	public void setServiceDialCode(String serviceDialCode) {
		this.serviceDialCode = serviceDialCode;
	}
	public String getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus.toLowerCase();
	}
	public boolean isHasSubService() {
		return hasSubService;
	}
	@Override
	public boolean isLogoLoading() {
		return logoLoading;
	}
	@Override
	public void setLogoLoading(boolean logoLoading) {
		this.logoLoading = logoLoading;
	}
	@Override
	public String getLogo() {
		return getServiceLogo();
	}
	@Override
	public String getStatus() {
		return getServiceStatus();
	}
	
	
}
