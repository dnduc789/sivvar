package org.linphone.setup;
/*
WizardFragment.java
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sivvar.AppUtil;
import com.sivvar.LocalBundleData;
import com.sivvar.PhoneNumUtil;
import com.sivvar.R;
import com.sivvar.objects.InitializeUserResponseDTO;
import com.sivvar.ui.phoneEditText.Country;
import com.sivvar.ui.phoneEditText.CountryAdapter;
import com.sivvar.ui.phoneEditText.CustomPhoneNumberFormattingTextWatcher;
import com.sivvar.ui.phoneEditText.OnPhoneChangedListener;
import com.sivvar.ui.phoneEditText.PhoneUtils;
import com.sivvar.webservices.WebserviceCallingFailedException;
import com.sivvar.webservices.WebserviceUtil;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
/**
 * @author Sylvain Berfini
 */
public class WizardFragment extends Fragment {
	
	protected static final TreeSet<String> CANADA_CODES = new TreeSet<String>();

    static {
        CANADA_CODES.add("204");
        CANADA_CODES.add("236");
        CANADA_CODES.add("249");
        CANADA_CODES.add("250");
        CANADA_CODES.add("289");
        CANADA_CODES.add("306");
        CANADA_CODES.add("343");
        CANADA_CODES.add("365");
        CANADA_CODES.add("387");
        CANADA_CODES.add("403");
        CANADA_CODES.add("416");
        CANADA_CODES.add("418");
        CANADA_CODES.add("431");
        CANADA_CODES.add("437");
        CANADA_CODES.add("438");
        CANADA_CODES.add("450");
        CANADA_CODES.add("506");
        CANADA_CODES.add("514");
        CANADA_CODES.add("519");
        CANADA_CODES.add("548");
        CANADA_CODES.add("579");
        CANADA_CODES.add("581");
        CANADA_CODES.add("587");
        CANADA_CODES.add("604");
        CANADA_CODES.add("613");
        CANADA_CODES.add("639");
        CANADA_CODES.add("647");
        CANADA_CODES.add("672");
        CANADA_CODES.add("705");
        CANADA_CODES.add("709");
        CANADA_CODES.add("742");
        CANADA_CODES.add("778");
        CANADA_CODES.add("780");
        CANADA_CODES.add("782");
        CANADA_CODES.add("807");
        CANADA_CODES.add("819");
        CANADA_CODES.add("825");
        CANADA_CODES.add("867");
        CANADA_CODES.add("873");
        CANADA_CODES.add("902");
        CANADA_CODES.add("905");
    }
    
    protected SparseArray<ArrayList<Country>> mCountriesMap = new SparseArray<ArrayList<Country>>();

    protected PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    protected Spinner mSpinner;

    protected String mLastEnteredPhone;
    protected CountryAdapter mAdapter;

	private Handler mHandler = new Handler();
	private EditText username;
	
	private ImageView usernameOkIV;
	private boolean usernameOk = false;
	private ImageView createAccount;
	private TextView errorMessage;
	private TextView createAccountText;
	private char[] acceptedChars = new char[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '_', '-' };
	
	protected AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Country c = (Country) mSpinner.getItemAtPosition(position);
            if (mLastEnteredPhone != null && mLastEnteredPhone.startsWith(c.getCountryCodeStr())) {
                return;
            }
            username.getText().clear();
            username.getText().insert(username.getText().length() > 0 ? 1 : 0, String.valueOf(c.getCountryCode()));
            username.setSelection(username.length());
            mLastEnteredPhone = null;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    
    protected OnPhoneChangedListener mOnPhoneChangedListener = new OnPhoneChangedListener() {
        @Override
        public void onPhoneChanged(String phone) {
            try {
                mLastEnteredPhone = phone;
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(phone, null);
                ArrayList<Country> list = mCountriesMap.get(p.getCountryCode());
                Country country = null;
                if (list != null) {
                    if (p.getCountryCode() == 1) {
                        String num = String.valueOf(p.getNationalNumber());
                        if (num.length() >= 3) {
                            String code = num.substring(0, 3);
                            if (CANADA_CODES.contains(code)) {
                                for (Country c : list) {
                                    // Canada has priority 1, US has priority 0
                                    if (c.getPriority() == 1) {
                                        country = c;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (country == null) {
                        for (Country c : list) {
                            if (c.getPriority() == 0) {
                                country = c;
                                break;
                            }
                        }
                    }
                }
                if (country != null) {
                    final int position = country.getNum();
                    mSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mSpinner.setSelection(position);
                        }
                    });
                }
            } catch (NumberParseException ignore) {
            }

        }
    };
    
    protected void initUI(View rootView) {
        mSpinner = (Spinner) rootView.findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mAdapter = new CountryAdapter(getActivity());

        mSpinner.setAdapter(mAdapter);

        username.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher(mOnPhoneChangedListener));
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (dstart > 0 && !Character.isDigit(c)) {
                        return "";
                    }
                }
                return null;
            }
        };

        username.setFilters(new InputFilter[]{filter});

        username.setImeOptions(EditorInfo.IME_ACTION_SEND);
        username.setImeActionLabel(getString(R.string.label_send), EditorInfo.IME_ACTION_SEND);
        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
//                    send();
                    return true;
                }
                return false;
            }
        });

    }
	
    protected void initCodes(Context context) {
        new AsyncPhoneInitTask(context).execute();
    }

    protected class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<Country>> {

        private int mSpinnerPosition = -1;
        private Context mContext;

        public AsyncPhoneInitTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<Country> doInBackground(Void... params) {
            ArrayList<Country> data = new ArrayList<Country>(233);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    Country c = new Country(mContext, line, i);
                    data.add(c);
                    ArrayList<Country> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<Country>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            if (!TextUtils.isEmpty(username.getText())) {
                return data;
            }
            String countryRegion = PhoneUtils.getCountryRegionFromPhone(mContext);
            int code = mPhoneNumberUtil.getCountryCodeForRegion(countryRegion);
            ArrayList<Country> list = mCountriesMap.get(code);
            if (list != null) {
                for (Country c : list) {
                    if (c.getPriority() == 0) {
                        mSpinnerPosition = c.getNum();
                        break;
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Country> data) {
            mAdapter.addAll(data);
            if (mSpinnerPosition > 0) {
                mSpinner.setSelection(mSpinnerPosition);
            }
        }
    }


    protected String validate() {
        String region = null;
        String phone = null;
        if (mLastEnteredPhone != null) {
            try {
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(mLastEnteredPhone, null);
                StringBuilder sb = new StringBuilder(16);
                sb.append('+').append(p.getCountryCode()).append(p.getNationalNumber());
                phone = sb.toString();
                region = mPhoneNumberUtil.getRegionCodeForNumber(p);
            } catch (NumberParseException ignore) {
            }
        }
        if (region != null) {
            return phone;
        } else {
            return null;
        }
    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setup_wizard, container, false);
		
		username = (EditText) view.findViewById(R.id.setup_username);
    	usernameOkIV = (ImageView) view.findViewById(R.id.setup_username_ok);
    	addXMLRPCUsernameHandler(username, usernameOkIV);
    	InputFilter filter = new InputFilter(){
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    for (int index = start; index < end; index++) {                                         
                        if (!new String(acceptedChars).contains(String.valueOf(source.charAt(index)))) { 
                            return ""; 
                        }               
                    }
                }
                return null;
            }
        };
    	username.setFilters(new InputFilter[] { filter });

    	
    	errorMessage = (TextView) view.findViewById(R.id.setup_error);
    	
    	createAccount = (ImageView) view.findViewById(R.id.setup_create);
    	createAccount.setEnabled(false);
    	createAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createAccountClickedHandle();
			}
    	});
    	createAccountText = (TextView) view.findViewById(R.id.setup_create_text);
    	
    	
    	initUI(view);
		initCodes(getActivity());
    	
		return view;
	}
	
	public void createAccountClickedHandle() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				createAccount(username.getText().toString().toLowerCase(Locale.getDefault()));
			}
		});
		builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		if(!username.getText().toString().equals(username.getText().toString().toLowerCase(Locale.getDefault()))){
			builder.setMessage(getString(R.string.setup_confirm_username).replace("%s", username.getText().toString().toLowerCase(Locale.getDefault())));
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			createAccount(username.getText().toString().toLowerCase(Locale.getDefault()));
		}
	}
	
	private boolean isUsernameCorrect(String username) {
		return PhoneNumUtil.isPhoneNumber(username.replaceAll("[^\\d.]", ""));
	}
	
	private void isUsernameRegistred(String username, final ImageView icon) {
		final Runnable runNotReachable = new Runnable() {
			public void run() {
				errorMessage.setText(R.string.wizard_server_unavailable);
				usernameOk = false;
				icon.setImageResource(R.drawable.wizard_notok);
				createAccount.setEnabled(usernameOk);
			}
		};
		
		try {
			XMLRPCClient client = new XMLRPCClient(new URL(getString(R.string.wizard_url)));
			
			XMLRPCCallback listener = new XMLRPCCallback() {
				Runnable runNotOk = new Runnable() {
    				public void run() {
    					errorMessage.setText(R.string.username_validate_error);
    					usernameOk = false;
						icon.setImageResource(R.drawable.wizard_notok);
						createAccount.setEnabled(usernameOk);
					}
	    		};
	    		
	    		Runnable runOk = new Runnable() {
    				public void run() {
    					errorMessage.setText("");
    					icon.setImageResource(R.drawable.wizard_ok);
						usernameOk = true;
						createAccount.setEnabled(usernameOk);
					}
	    		};
				
			    public void onResponse(long id, Object result) {
			    	int answer = (Integer) result;
			    	if (answer != 0) {
			    		mHandler.post(runNotOk);
					}
					else {
						mHandler.post(runOk);
					}
			    }
			    
			    public void onError(long id, XMLRPCException error) {
			    	mHandler.post(runNotReachable);
			    }
			   
			    public void onServerError(long id, XMLRPCServerException error) {
			    	mHandler.post(runNotReachable);
			    }
			};

		    client.callAsync(listener, "check_account", username);
		} 
		catch(Exception ex) {
			mHandler.post(runNotReachable);
		}
	}
	
	private boolean isEmailCorrect(String email) {
    	Pattern emailPattern = Patterns.EMAIL_ADDRESS;
    	return emailPattern.matcher(email).matches();
	}
	
	private class ServiceWaitThread extends Thread {
		
		public String msisdn;
		public String fullPhoneNumber;
		
		public void run() {
			String imei = AppUtil.retrieveIMEI(getActivity());
			InitializeUserResponseDTO data = null;
			try {
				data = WebserviceUtil.apiInitializeUser(imei, msisdn);
				
			} catch (WebserviceCallingFailedException e) {
				try {
					// call webservice again if failed
					Thread.sleep(300);
					data = WebserviceUtil.apiInitializeUser(imei, msisdn);
				} catch (Exception e1) {
					SetupActivity.instance().runOnUiThread(new Runnable() {
					    public void run() {
					    	Toast.makeText(getActivity(), getString(R.string.webserver_unavailable), Toast.LENGTH_LONG).show();
					    }
					});
					return;
				}
				
			}
//			data = new InitializeUserResponseDTO();
//			data.setCode(WebserviceUtil.SUCCESS_CODE_EXISTING_RETURNING_USER_WITH_VALID_REGISTRATION_CODE);
//			data.setMessage("gsdpongpsdg");
//			data.setData("222");
			if (data != null) {
				if (data.getSuccess()) {
					final InitializeUserResponseDTO dataTemp = data;
					SetupActivity.instance().runOnUiThread(new Runnable() {
					    public void run() {
					    	if (dataTemp.getCode().contains(WebserviceUtil.SUCCESS_CODE_EXISTING_RETURNING_USER_WITH_VALID_REGISTRATION_CODE)) {
								LocalBundleData.setGoAheadOverSubscribe(true);
								errorMessage.setTextColor(getResources().getColor(R.color.notify_message));
							}
							SetupActivity.instance().displayWizardConfirm(msisdn, fullPhoneNumber);
					    }
					});
				} else {
					String message = "";
					if (data.getCode().contains(WebserviceUtil.ERROR_CODE_ERROR_CODES_FROM_SMS_GATEWAY)
							|| data.getCode().contains(WebserviceUtil.ERROR_CODE_FAILED_WHILE_SENDING_SMS)
							|| data.getCode().contains(WebserviceUtil.ERROR_CODE_INVALID_OR_INCORRECT_PHONE_NUMBER)) {
						LocalBundleData.setLoginFailedNumber(LocalBundleData.getLoginFailedNumber() + 1);
						message = data.getMessage();
					} else if (data.getCode().contains(WebserviceUtil.ERROR_CODE_DATABASE_ERROR)) {
						LocalBundleData.setLoginFailedNumber(LocalBundleData.getLoginFailedNumber() + AppUtil.MAX_FAILED_CALLING);
						message = data.getMessage();
					}
					final String messageTemp = message;
					SetupActivity.instance().runOnUiThread(new Runnable() {
					    public void run() {
				    		errorMessage.setTextColor(getResources().getColor(R.color.error_message));
					    	errorMessage.setText(messageTemp);
					    	
					    	if (LocalBundleData.getLoginFailedNumber() >= AppUtil.MAX_FAILED_CALLING) {
					    		Toast.makeText(getActivity(), 
					    				getString(R.string.webserver_temporarily_down), Toast.LENGTH_LONG).show();
					    	}
					    }
					});
					
				}
			}
		}
	}
	
	private void createAccount(final String username) {
		String usernameCorrect = username.replaceAll("[^\\d.]", "");
		String msisdn = usernameCorrect;
		if (msisdn == null || msisdn.isEmpty()) {
			errorMessage.setText(R.string.username_validate_error);
			usernameOk = false;
			usernameOkIV.setImageResource(R.drawable.wizard_notok);
			createAccount.setEnabled(usernameOk);
		} else {
			if (getResources().getBoolean(R.bool.use_initialize_webservice)) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				ServiceWaitThread thread = new ServiceWaitThread();
				thread.msisdn = msisdn;
				thread.fullPhoneNumber = username;
				thread.start();
			} else {
				SetupActivity.instance().displayWizardConfirm(msisdn, username);
			}
		}
	}
	
	private void addXMLRPCUsernameHandler(final EditText field, final ImageView icon) {
		field.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int count, int after) {
				usernameOk = false;
				String username = field.getText().toString().toLowerCase(Locale.getDefault());
				if (isUsernameCorrect(username) && !LocalBundleData.getAccountVerified()) {
//					isUsernameRegistred(username, icon);
					createAccount.setEnabled(true);
					createAccountText.setTextColor(Color.BLACK);
					icon.setImageResource(R.drawable.wizard_ok);
					errorMessage.setText("");
					
				} else {
					createAccount.setEnabled(false);
					createAccountText.setTextColor(getResources().getColor(R.color.text_disabled));
					icon.setImageResource(R.drawable.wizard_notok);
					errorMessage.setText(R.string.username_validate_error);
				}
			}
		});
	}
	
	@Override
	public void onResume() {
		LocalBundleData.setLoginFailedNumber(0);
		super.onResume();
	}
	
}
	
