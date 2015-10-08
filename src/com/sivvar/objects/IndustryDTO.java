package com.sivvar.objects;

public class IndustryDTO implements LogoDownloading {

	// self variables
	private boolean logoLoading;
	
	private String industryName;
	private String industryLogo;
	
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	public String getIndustryLogo() {
		return industryLogo;
	}
	public void setIndustryLogo(String industryLogo) {
		this.industryLogo = industryLogo;
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
		return getIndustryLogo();
	}
	@Override
	public String getStatus() {
		return null;
	}
	
}
