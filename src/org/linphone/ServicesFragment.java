package org.linphone;

/*
 ContactsFragment.java
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
import java.io.InputStream;
import java.util.List;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sivvar.LocalBundleData;
import com.sivvar.R;
import com.sivvar.objects.ServiceDTO;
import com.sivvar.ui.grid.ImageAdapter;
import com.sivvar.webservices.WebserviceCallingFailedException;
import com.sivvar.webservices.WebserviceUtil;

/**
 * @author Sylvain Berfini
 */
@SuppressLint("DefaultLocale")
public class ServicesFragment extends Fragment {
	
	private boolean registered;
	private static ServicesFragment instance;
	private LinphoneCoreListenerBase mListener;
	private Button industriesButton;
	private LinearLayout favouriteLayout, industriesLayout;
	private ImageView gobackButton;
	private GridView industriesServiceWrapper;
	private TextView noIndustryServiceTextView, noFavouriteServiceTextView, noIndustryTextView, serviceTitleTextView;
	private int page = 1; // 1: industries view; 2: logo industries view
	private LayoutInflater infl;

	static final boolean isInstanciated() {
		return instance != null;
	}

	public static final ServicesFragment instance() {
		return instance;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		infl = inflater;
		View view = inflater.inflate(R.layout.services_list, container, false);
		LinphoneActivity.instance().hideStatusBar();
		
		registered = LinphoneManager.getLc().getDefaultProxyConfig() != null && LinphoneManager.getLc().getDefaultProxyConfig().isRegistered();
		
		noIndustryTextView = (TextView) view
				.findViewById(R.id.noIndustry);
		noIndustryServiceTextView = (TextView) view
				.findViewById(R.id.noIndustryService);
		noFavouriteServiceTextView = (TextView) view
				.findViewById(R.id.noFavouriteService);
		industriesServiceWrapper = (GridView) view
				.findViewById(R.id.industry_service_wrapper);
		favouriteLayout = (LinearLayout) view
				.findViewById(R.id.layout_favourite_wrap);
		industriesButton = (Button) view.findViewById(R.id.industries_button);
		
		industriesLayout = (LinearLayout) view
				.findViewById(R.id.layout_service_wrap);
		gobackButton = (ImageView) view.findViewById(R.id.goback) ;
		gobackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		serviceTitleTextView = (TextView)view.findViewById(R.id.service_title);
		industriesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showIndustriesView(inflater);
			}
		});

		GridView favouriteWrapper = (GridView) view
				.findViewById(R.id.favourite_wrapper);
		try {
			if (LocalBundleData.favouriteServiceList == null) {
				LocalBundleData.favouriteServiceList = WebserviceUtil
						.apiGetFavouriteServices();
			}
			if (LocalBundleData.favouriteServiceList.isEmpty()) {
				noFavouriteServiceTextView.setVisibility(View.VISIBLE);
			} else {
				noFavouriteServiceTextView.setVisibility(View.GONE);
			}
			
			favouriteWrapper.setAdapter(new ImageAdapter(LinphoneActivity.instance(), LocalBundleData.favouriteServiceList, inflater));
			favouriteWrapper.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					ServiceDTO serviceDTO = LocalBundleData.favouriteServiceList.get(position);
					boolean active = ServiceDTO.SERVICE_STATUS_ACTIVE.equalsIgnoreCase(serviceDTO.getServiceStatus()); 
					if (active && registered) {
						String address = serviceDTO.getServiceDialCode();
						String serviceName = serviceDTO.getServiceName();
						callService(address, serviceName);
					}
				}
			});
		} catch (WebserviceCallingFailedException e) {
			e.printStackTrace();
		}
		
		LinphoneManager.getLc().addListener(mListener = new LinphoneCoreListenerBase(){

			@Override
			public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
				// nothing
			}
			
			@Override
			public void globalState(LinphoneCore lc,LinphoneCore.GlobalState state, String message) {
				// nothing
			}

			@Override
			public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
				if (state == RegistrationState.RegistrationOk && LinphoneManager.getLc().getDefaultProxyConfig() != null && LinphoneManager.getLc().getDefaultProxyConfig().isRegistered()) {
					registered = true;
				} else  {
					registered = false;
				}
			}
		});

		return view;
	}

	private void showIndustriesView(final LayoutInflater inflater) {
		page = 1;
		favouriteLayout.setVisibility(View.GONE);
		industriesLayout.setVisibility(View.VISIBLE);
		noIndustryServiceTextView.setVisibility(View.GONE);
		serviceTitleTextView.setText(getResources().getString(R.string.services_industries_title));
		try {
			if (LocalBundleData.industryList == null) {
				LocalBundleData.industryList = WebserviceUtil
						.apiGetIndustryServices();
			}
			
			if (LocalBundleData.industryList.isEmpty()) {
				noIndustryTextView.setVisibility(View.VISIBLE);
			} else {
				noIndustryTextView.setVisibility(View.GONE);
			}
			
			industriesServiceWrapper.setAdapter(new ImageAdapter(LinphoneActivity.instance(), 
					LocalBundleData.industryList, inflater, true));
			industriesServiceWrapper.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					
					page = 2;
					try {
						final String industryName = LocalBundleData.industryList.get(position).getIndustryName();
						serviceTitleTextView.setText(industryName);
						if (LocalBundleData.serviceList.get(industryName) == null) {
							final List<ServiceDTO> listItem = WebserviceUtil
									.apiGetIndustrySpecific(industryName);
							LocalBundleData.serviceList.put(industryName, listItem);
						}
						if (LocalBundleData.serviceList.get(industryName).isEmpty()) {
							noIndustryServiceTextView.setVisibility(View.VISIBLE);
						} else {
							noIndustryServiceTextView.setVisibility(View.GONE);
						}
						
						industriesServiceWrapper.setAdapter(new ImageAdapter(LinphoneActivity.instance(), 
								LocalBundleData.serviceList.get(industryName), inflater));
						industriesServiceWrapper.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
								
								ServiceDTO serviceDTO = LocalBundleData.serviceList.get(industryName).get(position);
								boolean active = ServiceDTO.SERVICE_STATUS_ACTIVE.equalsIgnoreCase(serviceDTO.getServiceStatus()); 
								if (active && registered) {
									String address = serviceDTO.getServiceDialCode();
									String serviceName = serviceDTO.getServiceName();
									callService(address, serviceName);
								}
							}
						});
					} catch (WebserviceCallingFailedException e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void callService(String address, String displayName) {
		try {
			if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
				if (address.length() > 0) { 
					LinphoneManager.getInstance().newOutgoingCall(address, displayName);
				}
			}
		} catch (LinphoneCoreException e) {
			LinphoneManager.getInstance().terminateCall();
			onWrongDestinationAddress(address);
		};
	}
	
	private void onBackPressed() {
		if (page == 1) {
			industriesLayout.setVisibility(View.GONE);
			favouriteLayout.setVisibility(View.VISIBLE);
		} else if (page == 2) {
			showIndustriesView(infl);
		}
	}
	
	protected void onWrongDestinationAddress(String address) {
		Toast.makeText(LinphoneActivity.instance()
				,String.format(getResources().getString(R.string.warning_wrong_destination_address),address)
				,Toast.LENGTH_LONG).show();
	}
	
	public Bitmap resizeBitmap(int targetW, int targetH, String photoPath) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		return BitmapFactory.decodeFile(photoPath, bmOptions);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				Bitmap resized = Bitmap.createScaledBitmap(mIcon11, 90, 90,
						true);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
	
	@Override
	public synchronized void onDestroy() {
		super.onDestroy();
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}
	}
	
}
