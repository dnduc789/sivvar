package com.sivvar.webservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sivvar.objects.IndustryDTO;
import com.sivvar.objects.InitializeUserResponseDTO;
import com.sivvar.objects.RegisterVerifyUserResponseDTO;
import com.sivvar.objects.ResponseDTO;
import com.sivvar.objects.ServiceDTO;

public class WebserviceUtil {

	private static final String HTTP_POST = "HTTP_POST";
	private static final String HTTP_GET = "HTTP_GET";
	
	public static final String ERROR_CODE_INVALID_REQUEST = "E9000";
	public static final String ERROR_CODE_INVALID_IMEI = "E9001";
	public static final String ERROR_CODE_INVALID_OR_INCORRECT_PHONE_NUMBER = "E9002";
	public static final String ERROR_CODE_DATABASE_ERROR = "E9003";
	public static final String ERROR_CODE_FAILED_WHILE_SENDING_SMS = "E9004";
	public static final String ERROR_CODE_INVALID_OR_WRONG_REGISTRATION_CODE = "E9005";
	public static final String ERROR_CODE_SUBSCRIBER_NOT_CREATED_DUE_TO_DATABASE_ERROR = "E9006";
	public static final String SUCCESS_CODE_SUBSCRIBER_SUCCESSFULLY_CREATED = "S0000";
	public static final String SUCCESS_CODE_EXISTING_RETURNING_USER_WITH_VALID_REGISTRATION_CODE = "S0001";
	public static final String SUCCESS_CODE_USER_ALREADY_RECEIVED_REGISTRATION_CODE = "S0002";
	public static final String SUCCESS_CODE_SMS_SUCCESSFULLY_SENT = "OK";
	public static final String ERROR_CODE_ERROR_CODES_FROM_SMS_GATEWAY = "-29";

	private static final String WEBSERVICE_INITIALIZE_USER = "http://104.131.205.147:9448/V2/initialize_sivvar_android_user.php?imei=%s&msisdn=%s";
	private static final String WEBSERVICE_REGISTER_VERIFY_USER = "http://104.131.205.147:9448/V2/subscribe_sivvar_android_user.php?imei=%s&msisdn=%s&sms_reg_code=%s";
//	private static final String WEBSERVICE_INITIALIZE_USER = "http://104.131.205.147:9448/V2/initialize_sivvar_user.php?msisdn=%s";
	private static final String VOLLEY_WEBSERVICE_REGISTER_VERIFY_USER = "http://104.131.205.147:9448/V2/subscribe_sivvar_android_user.php";
	private static final String WEBSERVICE_GET_FAVOURITE_SERVICES = "http://104.131.205.147:9448/sivvar_services/sivvar_favourite_services.php?favourites=all";
	private static final String WEBSERVICE_GET_INDUSTRY_SERVICES = "http://104.131.205.147:9448/sivvar_services/sivvar_industry.php?industry=all";
	private static final String WEBSERVICE_GET_INDUSTRY_SPECIFIC = "http://104.131.205.147:9448/sivvar_services/sivvar_industry_services.php?industry_name=%s";
	private static final String WEBSERVICE_GET_SUB_SERVICES = "http://104.131.205.147:9448/sivvar_services/sivvar_sub_services.php?service_name=%s";
	
	private static final String[] WEBSERVICE_SUBSCRIBE_SUCCESS_CODE = { 
			SUCCESS_CODE_SUBSCRIBER_SUCCESSFULLY_CREATED };
	private static final String[] WEBSERVICE_SUBSCRIBE_ERROR_CODE = { 
			ERROR_CODE_DATABASE_ERROR,
			ERROR_CODE_INVALID_OR_WRONG_REGISTRATION_CODE,
			ERROR_CODE_SUBSCRIBER_NOT_CREATED_DUE_TO_DATABASE_ERROR };
	private static final String[] WEBSERVICE_INITIALIZATION_SUCCESS_CODE = { 
			SUCCESS_CODE_EXISTING_RETURNING_USER_WITH_VALID_REGISTRATION_CODE, 
			SUCCESS_CODE_USER_ALREADY_RECEIVED_REGISTRATION_CODE, 
			SUCCESS_CODE_SMS_SUCCESSFULLY_SENT, 
			ERROR_CODE_DATABASE_ERROR, 
			ERROR_CODE_FAILED_WHILE_SENDING_SMS };
	private static final String[] WEBSERVICE_INITIALIZATION_ERROR_CODE = { 
			ERROR_CODE_INVALID_REQUEST, 
			ERROR_CODE_INVALID_IMEI, 
			ERROR_CODE_INVALID_OR_INCORRECT_PHONE_NUMBER, 
			ERROR_CODE_DATABASE_ERROR, 
			ERROR_CODE_FAILED_WHILE_SENDING_SMS, 
			ERROR_CODE_ERROR_CODES_FROM_SMS_GATEWAY };
	
	public static DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static String api(String action, String URL, String... params)
			throws ClientProtocolException, IOException {
		String createdURL = String.format(URL, params);

		if (action.equals(HTTP_GET)) {
			return apiHttpGet(createdURL);
		} else if (action.equals(HTTP_POST)) {
			return apiHttpPost(createdURL);
		}
		return "";
	}
	
	public static String volleyAPI(String action, String URL, String... params)
			throws ClientProtocolException, IOException {
		String createdURL = String.format(URL, params);

		if (action.equals(HTTP_GET)) {
			return apiHttpGet(createdURL);
		} else if (action.equals(HTTP_POST)) {
			return apiHttpPost(createdURL);
		}
		return "";
	}

	public static String apiHttpPost(String url)
			throws ClientProtocolException, IOException {
		
		DefaultHttpClient httpclient = getNewHttpClient();
		
		String[] urlParts = url.split("\\?");
		if (urlParts.length == 0) {
			return "";
		}
		
		HttpPost httpost = new HttpPost(urlParts[0]);
		if (urlParts.length > 1 && !urlParts[1].isEmpty()) {
			
			String[] parameterParts = urlParts[1].split("&");
			if (parameterParts.length > 0) {
				List<NameValuePair> nvps = new ArrayList <NameValuePair>();
				for (int index = 0; index < parameterParts.length; index++) {
					String[] pair = parameterParts[index].split("=");
					if (pair.length == 2) {
						String name = pair[0];
						String value = pair[1];
						nvps.add(new BasicNameValuePair(name, value));
					}
				}
		        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
		}

        HttpResponse response = httpclient.execute(httpost);

        StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			String responseString = out.toString();
			out.close();
			httpclient.getConnectionManager().shutdown();
			return responseString;
		} else {
			// Closes the connection.
			response.getEntity().getContent().close();
			httpclient.getConnectionManager().shutdown();
			throw new IOException(statusLine.getReasonPhrase());
		}

	}

	public static String apiHttpGet(String url) throws ClientProtocolException,
			IOException {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient httpclient = getNewHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(url));
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			String responseString = out.toString();
			out.close();
			return responseString;
		} else {
			// Closes the connection.
			response.getEntity().getContent().close();
			httpclient.getConnectionManager().shutdown();
			throw new IOException(statusLine.getReasonPhrase());
		}
	}
	
	public static InitializeUserResponseDTO apiInitializeUser(String imei, String msisdn)
			throws WebserviceCallingFailedException {
		InitializeUserResponseDTO responseDTO = new InitializeUserResponseDTO();
		try {
			String response = api(HTTP_GET, WEBSERVICE_INITIALIZE_USER, imei, msisdn);
			JSONObject responseJSON = new JSONObject(response);
			getResponse(responseJSON, responseDTO);
			responseDTO.setSuccess(Arrays.asList(WEBSERVICE_INITIALIZATION_SUCCESS_CODE).contains(responseDTO.getCode()));
		} catch (ClientProtocolException e) {
			throw new WebserviceCallingFailedException();
		} catch (IOException e) {
			throw new WebserviceCallingFailedException();
		} catch (JSONException e) {
			throw new WebserviceCallingFailedException();
		}
		return responseDTO;
	}

	public static RegisterVerifyUserResponseDTO apiRegisterVerifyUser(String imei, String msisdn, String smsRegCode)
			throws WebserviceCallingFailedException {
		RegisterVerifyUserResponseDTO responseDTO = new RegisterVerifyUserResponseDTO();
		try {
			 String response = api(HTTP_GET, WEBSERVICE_REGISTER_VERIFY_USER, imei, msisdn, smsRegCode);
			 JSONObject responseJSON = new JSONObject(response);
			 getResponse(responseJSON, responseDTO);
			 responseDTO.setVerified(Arrays.asList(WEBSERVICE_SUBSCRIBE_SUCCESS_CODE).contains(responseDTO.getCode()));
		} catch (ClientProtocolException e) {
			throw new WebserviceCallingFailedException();
		} catch (IOException e) {
			throw new WebserviceCallingFailedException();
		} catch (JSONException e) {
			throw new WebserviceCallingFailedException();
		} catch (Exception e) {
			throw new WebserviceCallingFailedException();
		}
		return responseDTO;
	}
	
	public static RegisterVerifyUserResponseDTO volleyApiRegisterVerifyUser(final String imei, final String msisdn, final String smsRegCode, Context context)
			throws WebserviceCallingFailedException {
		RegisterVerifyUserResponseDTO responseDTO = new RegisterVerifyUserResponseDTO();
//		try {
			 
//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//			.permitAll().build();
		//	StrictMode.setThreadPolicy(policy);
			
			RequestQueue queue = Volley.newRequestQueue(context);
			
			Response.ErrorListener errorListener = new Response.ErrorListener() {
		        @Override
		        public void onErrorResponse(VolleyError error) {
		            if( error instanceof NetworkError) {
		            } else if( error instanceof ServerError) {
		            } else if( error instanceof AuthFailureError) {
		            } else if( error instanceof ParseError) {
		            } else if( error instanceof NoConnectionError) {
		            } else if( error instanceof TimeoutError) {                     
		            }
		            String w = "";
		            String e = w;
		        }
		    };
			
			
		    StringRequest sr = new StringRequest(Request.Method.GET, VOLLEY_WEBSERVICE_REGISTER_VERIFY_USER, new Response.Listener<String>() {
		        @Override
		        public void onResponse(String response) {
		//            ProgressLoadStartLogin.setVisibility(View.GONE);
		//            displaystatis_kontenDetail(response);
		//            btn_enable();
		        }
		    }, errorListener){
		        @Override
		        protected Map<String,String> getParams(){
		            Map<String,String> params = new HashMap<String, String>();
		            params.put("imei", imei);
		            params.put("msisdn",msisdn);
		            params.put("sms_reg_code", smsRegCode);
		            return params;
		        }
		
		        @Override
		        public Map<String, String> getHeaders() throws AuthFailureError {
		            Map<String,String> params = new HashMap<String, String>();
		            params.put("Content-Type","application/x-www-form-urlencoded");
		            return params;
		        }
		    };
		    queue.add(sr);
			
			
			
			
			
//			 JSONObject responseJSON = new JSONObject(response);
//			 getResponse(responseJSON, responseDTO);
//			 responseDTO.setVerified(response.equals("true"));
//		} catch (ClientProtocolException e) {
//			throw new WebserviceCallingFailedException();
//		} catch (IOException e) {
//			throw new WebserviceCallingFailedException();
//		} catch (JSONException e) {
//			throw new WebserviceCallingFailedException();
//		}
		return responseDTO;
	}

//	public static InitializeUserResponseDTO apiInitializeUser(String msisdn)
//			throws WebserviceCallingFailedException {
//		InitializeUserResponseDTO responseDTO = new InitializeUserResponseDTO();
//		try {
//			String response = api(HTTP_GET, WEBSERVICE_INITIALIZE_USER, msisdn);
////			String response = "{\"response_code\":\"E9002\",\"response_message\":\"YOUR PHONE NUMBER IS INCORRECT OR INVALID PHONE NUMBER\",\"response_data\":\"84988549089\"}";
//			JSONObject responseJSON = new JSONObject(response);
//			getResponse(responseJSON, responseDTO);
//		} catch (ClientProtocolException e) {
//			throw new WebserviceCallingFailedException();
//		} catch (IOException e) {
//			throw new WebserviceCallingFailedException();
//		} catch (JSONException e) {
//			// no thing.
//		}
//		return responseDTO;
//	}
//
//	public static RegisterVerifyUserResponseDTO apiRegisterVerifyUser(String msisdn, String smsRegCode)
//			throws WebserviceCallingFailedException {
//		RegisterVerifyUserResponseDTO responseDTO = new RegisterVerifyUserResponseDTO();
//		try {
//			 String response = api(HTTP_GET, WEBSERVICE_REGISTER_VERIFY_USER, msisdn, smsRegCode);
//			 JSONObject responseJSON = new JSONObject(response);
//			 getResponse(responseJSON, responseDTO);
//			 responseDTO.setVerified(response.equals("true"));
//		} catch (ClientProtocolException e) {
//			throw new WebserviceCallingFailedException();
//		} catch (IOException e) {
//			throw new WebserviceCallingFailedException();
//		} catch (JSONException e) {
//			throw new WebserviceCallingFailedException();
//		}
//		return responseDTO;
//	}
	
	public static List<ServiceDTO> apiGetFavouriteServices()
			throws WebserviceCallingFailedException {
		List<ServiceDTO> services = new ArrayList<ServiceDTO>(); 
		try {
			String response = api(HTTP_GET, WEBSERVICE_GET_FAVOURITE_SERVICES);
			JSONObject responseJSON = new JSONObject(response);
			JSONArray favouritesJSON = responseJSON.getJSONArray("sivvar_favourites");
			for (int index = 0; index < favouritesJSON.length(); index++) {
				JSONObject favouriteJSON = favouritesJSON.getJSONObject(index).getJSONObject("sivvar_favourite");
				ServiceDTO service = retrieveService(favouriteJSON, true);
				services.add(service);
			}
		} catch (ClientProtocolException e) {
			throw new WebserviceCallingFailedException();
		} catch (IOException e) {
			throw new WebserviceCallingFailedException();
		} catch (JSONException e) {
			throw new WebserviceCallingFailedException();
		}
		return services;
	}
	
	public static List<IndustryDTO> apiGetIndustryServices()
			throws WebserviceCallingFailedException {
		List<IndustryDTO> services = new ArrayList<IndustryDTO>(); 
		try {
			String response = api(HTTP_GET, WEBSERVICE_GET_INDUSTRY_SERVICES);
			JSONObject responseJSON = new JSONObject(response);
			JSONArray favouritesJSON = responseJSON.getJSONArray("sivvar_industries");
			for (int index = 0; index < favouritesJSON.length(); index++) {
				JSONObject favouriteJSON = favouritesJSON.getJSONObject(index).getJSONObject("sivvar_industry");
				IndustryDTO service = new IndustryDTO();
				service.setIndustryName(favouriteJSON.getString("industry_name"));
				service.setIndustryLogo(favouriteJSON.getString("industry_logo"));
				services.add(service);
			}
		} catch (ClientProtocolException e) {
			throw new WebserviceCallingFailedException();
		} catch (IOException e) {
			throw new WebserviceCallingFailedException();
		} catch (JSONException e) {
			throw new WebserviceCallingFailedException();
		}
		return services;
	}
	
	public static List<ServiceDTO> apiGetIndustrySpecific(String industryName)
			throws WebserviceCallingFailedException {
		List<ServiceDTO> services = new ArrayList<ServiceDTO>(); 
		try {
			String response = api(HTTP_GET, WEBSERVICE_GET_INDUSTRY_SPECIFIC, industryName);
			JSONObject responseJSON = new JSONObject(response);
			JSONArray industryServicesJSON = responseJSON.getJSONArray("sivvar_industry_services");
			for (int index = 0; index < industryServicesJSON.length(); index++) {
				JSONObject serviceJSON = industryServicesJSON.getJSONObject(index).getJSONObject("sivvar_industry_service");
				ServiceDTO service = retrieveService(serviceJSON, false);
				services.add(service);
			}
		} catch (ClientProtocolException e) {
			throw new WebserviceCallingFailedException();
		} catch (IOException e) {
			throw new WebserviceCallingFailedException();
		} catch (JSONException e) {
			throw new WebserviceCallingFailedException();
		}
		return services;
	}
	
	public static List<ServiceDTO> apiGetSubServices(String serviceName)
			throws WebserviceCallingFailedException {
		List<ServiceDTO> services = new ArrayList<ServiceDTO>(); 
		try {
			String response = api(HTTP_GET, WEBSERVICE_GET_SUB_SERVICES, serviceName);
			JSONObject responseJSON = new JSONObject(response);
			JSONArray subServicesJSON = responseJSON.getJSONArray("sivvar_sub_services");
			for (int index = 0; index < subServicesJSON.length(); index++) {
				JSONObject serviceJSON = subServicesJSON.getJSONObject(index).getJSONObject("sivvar_sub_service");
				ServiceDTO service = retrieveService(serviceJSON, false);
				services.add(service);
			}
		} catch (ClientProtocolException e) {
			throw new WebserviceCallingFailedException();
		} catch (IOException e) {
			throw new WebserviceCallingFailedException();
		} catch (JSONException e) {
			throw new WebserviceCallingFailedException();
		}
		return services;
	}
	
	private static ServiceDTO retrieveService(JSONObject serviceJSON, boolean favourite) throws JSONException {
		ServiceDTO service = new ServiceDTO();
		service.setServiceName(serviceJSON.getString("service_name"));
		service.setServiceLogo(serviceJSON.getString("service_logo"));
		service.setIndustryName(serviceJSON.getString("industry_name"));
		service.setHasSubService(serviceJSON.getString("has_sub_service"));
		service.setServiceDialCode(serviceJSON.getString("service_dial_code"));
		if (favourite) {
			service.setServiceStatus(serviceJSON.getString("favourite_status"));
		} else {
			service.setServiceStatus(serviceJSON.getString("service_status"));
		}
		return service;
	}
	
	public static ResponseDTO getResponse(JSONObject responseJSON, ResponseDTO responseDTO) throws JSONException {
		responseDTO.setCode(responseJSON.getString("response_code"));
		responseDTO.setMessage(responseJSON.getString("response_message"));
		responseDTO.setData(responseJSON.getString("response_data"));
		return responseDTO;
	}
	
}
