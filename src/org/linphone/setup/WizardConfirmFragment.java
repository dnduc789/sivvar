package org.linphone.setup;
/*
WizardConfirmFragment.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
import java.util.Locale;

import org.linphone.LinphoneService;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sivvar.AppUtil;
import com.sivvar.LocalBundleData;
import com.sivvar.R;
import com.sivvar.objects.InitializeUserResponseDTO;
import com.sivvar.objects.RegisterVerifyUserResponseDTO;
import com.sivvar.webservices.WebserviceCallingFailedException;
import com.sivvar.webservices.WebserviceUtil;
/**
 * @author Sylvain Berfini
 */
public class WizardConfirmFragment extends Fragment {
	private String msisdn;
	private EditText editTextEnterCode; 
	private TextView errorMessage;
	private ImageView checkAccount;
	private TextView checkAccountText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setup_wizard_confirm, container, false);
		editTextEnterCode = (EditText)view.findViewById(R.id.setup_code_confirm);
		
		String phoneNumberFull = getArguments().getString("phoneNumberFull");
		msisdn = getArguments().getString("MSISDN");
		
		
		errorMessage = (TextView) view.findViewById(R.id.setup_error);
		checkAccount = (ImageView) view.findViewById(R.id.setup_check);
		checkAccountText = (TextView) view.findViewById(R.id.setup_check_text);
		TextView notifyMessage = (TextView) view.findViewById(R.id.notify_message);
		notifyMessage.setText(String.format(notifyMessage.getText().toString(), phoneNumberFull));
		
		addXMLRPCRegCodeHandler(editTextEnterCode);
		
		checkAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				verifyRegCode();
			}
		});
		
		return view;
	}	
	
	private void verifyRegCode() {
		String code = editTextEnterCode.getText().toString();
		if (code == null || code.isEmpty()) {
			errorMessage.setText(R.string.regcode_validate_error);
			checkAccountText.setTextColor(getResources().getColor(R.color.text_disabled));
			checkAccount.setEnabled(false);
		} else {
			isValidCode(code, msisdn);
		}
	}
	
	public void setRegistrationCodeGUI(String code) {
		editTextEnterCode.setText(code);
	}
	
	private class VerifyUserThread extends Thread {
		
		public String msisdn;
		public String regSmsCode;
		
		public void run() {
			String imei = AppUtil.retrieveIMEI(getActivity());
			RegisterVerifyUserResponseDTO data = null;
			if (getResources().getBoolean(R.bool.use_register_verify_user_webservice)) {
				if (!LocalBundleData.getGoAheadOverSubscribe()) {
					try {
						data = WebserviceUtil.apiRegisterVerifyUser(imei, msisdn, regSmsCode);
					} catch (WebserviceCallingFailedException e) {
						try {
							// call webservice again if failed
							Thread.sleep(500);
							data = WebserviceUtil.apiRegisterVerifyUser(imei, msisdn, regSmsCode);
						} catch (Exception e1) {
							SetupActivity.instance().runOnUiThread(new Runnable() {
							    public void run() {
							    	Toast.makeText(getActivity(), getString(R.string.webserver_unavailable), Toast.LENGTH_LONG).show();
							    }
							});
							return;
						}
					}
					
				} else {
					LocalBundleData.setGoAheadOverSubscribe(false);
					data = new RegisterVerifyUserResponseDTO();
					data.setVerified(true);
				}
			} else {
				data = new RegisterVerifyUserResponseDTO();
				data.setVerified(true);
			}
			
			if (data != null) {
				if (data.getVerified()) {
					final Context context = SetupActivity.instance() == null ? LinphoneService.instance().getApplicationContext() : SetupActivity.instance();
					String domain = context.getString(R.string.default_domain);
					if (!getResources().getBoolean(R.bool.use_register_verify_user_webservice)) {
						msisdn = "thienau_testapp";
						regSmsCode= "thien8776941";
						domain = "sip.linphone.org";
					}
					SetupActivity.instance().saveCreatedAccount(msisdn, regSmsCode, domain);
					SetupActivity.instance().runOnUiThread(new Runnable() {
					    public void run() {
					    	SetupActivity.instance().isAccountVerified();
					    }
					});
					LocalBundleData.setAccountVerified(true);
					LocalBundleData.setMSISDN(msisdn);
					LocalBundleData.setIMEI(imei);
					LocalBundleData.setSMSRegistrationCode(regSmsCode);
				} else {
					LocalBundleData.setLoginFailedNumber(LocalBundleData.getLoginFailedNumber() + 1);
					if (data.getCode().contains(WebserviceUtil.ERROR_CODE_SUBSCRIBER_NOT_CREATED_DUE_TO_DATABASE_ERROR)
							|| data.getCode().contains(WebserviceUtil.ERROR_CODE_DATABASE_ERROR)) {
						LocalBundleData.setLoginFailedNumber(LocalBundleData.getLoginFailedNumber() + AppUtil.MAX_FAILED_CALLING);
					}
					final RegisterVerifyUserResponseDTO temp = data;
					SetupActivity.instance().runOnUiThread(new Runnable() {
					    public void run() {
					    	errorMessage.setText(temp.getMessage());
					    	if (LocalBundleData.getLoginFailedNumber() >= AppUtil.MAX_FAILED_CALLING) {
					    		Toast.makeText(getActivity(), 
					    				getString(R.string.webserver_temporarily_down), Toast.LENGTH_LONG).show();
					    	}
					    }
					});
				}
			} else {
				SetupActivity.instance().runOnUiThread(new Runnable() {
				    public void run() {
				    	Toast.makeText(getActivity(), getString(R.string.setup_account_not_validated), Toast.LENGTH_LONG).show();
				    }
				});
			}
		}
	}
	
	private void isValidCode(String regSmsCode, final String username) {
//		LocalBundleData.setUserCode(regSmsCode);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		VerifyUserThread thread = new VerifyUserThread();
		thread.msisdn = username;
		thread.regSmsCode = regSmsCode;
		thread.start();
	}
	
	private void addXMLRPCRegCodeHandler(final EditText field) {
		field.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int count, int after) {
				String username = field.getText().toString().toLowerCase(Locale.getDefault());
				if (username != null && !username.isEmpty() && username.length() >= 8) {
					checkAccount.setEnabled(true);
					checkAccountText.setTextColor(Color.BLACK);
					errorMessage.setText("");
				} else {
					checkAccount.setEnabled(false);
					checkAccountText.setTextColor(getResources().getColor(R.color.text_disabled));
					errorMessage.setText(R.string.regcode_validate_error);
				}
			}
		});
	}

	public void clickVerifyButton() {
		verifyRegCode();
	}
	
	@Override
	public void onResume() {
		LocalBundleData.setLoginFailedNumber(0);
		super.onResume();
	}
	
}
