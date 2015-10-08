package com.sivvar.objects;

public class InitializeUserResponseDTO extends ResponseDTO {

	private String smsRegCode;
	private boolean success;
	
	public String getSmsRegCode() {
		return smsRegCode;
	}
	public void setSmsRegCode(String smsRegCode) {
		this.smsRegCode = smsRegCode;
	}
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
