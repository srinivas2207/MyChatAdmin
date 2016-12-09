package com.shree.mychatadmin.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.shree.mychatadmin.MyChatAdminApplication;
import com.shree.mychatadmin.fragments.UserListFragment;
import com.shree.mychatadmin.util.ApplicationConstants;
import com.shree.mychatadmin.util.DatabaseUtil;
import com.shree.mychatadmin.util.HttpRequestConstants;
import com.shree.mychatadmin.util.JsonConstancts;
import com.shree.mychatadmin.util.UserDetails;
import com.shree.mychatadmin.util.UserType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

public class NetworkOperations {
	private static NetworkOperations networkOperations;

	public static NetworkOperations getInstance() {
		if (networkOperations == null) {
			networkOperations = new NetworkOperations();
		}
		return networkOperations;
	}

	public boolean checkNetworkConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) MyChatAdminApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isSuccessResponse(JSONObject data) {
		try {
			int response = data.getInt(JsonConstancts.REQUEST_STATUS);
			if (response == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private int getRequstStatus(JSONObject data) {
		int response = 201;
		try {
			response = data.getInt(JsonConstancts.REQUEST_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public Runnable addUser(final String fullName, final String phoneNumber, final String emailId, final String userType) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.FULL_NAME, fullName);
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);
				params.put(HttpRequestConstants.EMAIL, emailId);
				params.put(HttpRequestConstants.USER_TYPE, userType);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.ADD_USER_URL, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							if (user == null) {
								user = new UserDetails();
							}
							user.setUserName(fullName);
							user.setEmaialId(emailId);
							user.setPhoneNumber(phoneNumber);
							user.setUserType(userType);
							user.setUserStatus(ApplicationConstants.NEW_USER);
							DatabaseUtil.getInstance().storeUserDetails(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.ADD_USER_URL, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable editUser(final String fullName, final String phoneNumber, final String emailId, final String userType) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.FULL_NAME, fullName);
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);
				params.put(HttpRequestConstants.EMAIL, emailId);
				params.put(HttpRequestConstants.USER_TYPE, userType);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.EDIT_USER_URL, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							if (user == null) {
								user = new UserDetails();
							}
							user.setUserName(fullName);
							user.setEmaialId(emailId);
							user.setPhoneNumber(phoneNumber);
							user.setUserType(userType);
							DatabaseUtil.getInstance().storeUserDetails(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.EDIT_USER_URL, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable deleteUser(final String phoneNumber) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.DELETE_USER_URL, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							DatabaseUtil.getInstance().deleteUser(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.DELETE_USER_URL, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable blockUser(final String phoneNumber) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.BLOCK_USER_URL, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							user.setUserStatus(ApplicationConstants.BLOCKED_USER);
							DatabaseUtil.getInstance().storeUserDetails(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.BLOCK_USER_URL, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable unBlockUser(final String phoneNumber) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.UNBLOCK_USER_URL, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							user.setUserStatus(ApplicationConstants.BLOCKED_USER_2);
							DatabaseUtil.getInstance().storeUserDetails(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.UNBLOCK_USER_URL, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable acceptRequest(final String phoneNumber, final String userType) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);
				params.put(HttpRequestConstants.USER_TYPE, userType);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.ACCEPT_REQUEST, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							user.setUserType(userType);
							user.setUserStatus(ApplicationConstants.NEW_USER);
							DatabaseUtil.getInstance().storeUserDetails(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.ACCEPT_REQUEST, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable rejectRequest(final String phoneNumber) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.PHONE_NUMBER, phoneNumber);

				int succssCode = 0;
				String result = doPost(ApplicationConstants.REJECT_REQUEST, params);
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						succssCode = getRequstStatus(data);
						if (succssCode == 200) {
							UserDetails user = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
							DatabaseUtil.getInstance().deleteUser(user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse( ApplicationConstants.REJECT_REQUEST, succssCode);
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}
	public Runnable getUsers(final int type) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String userType = "";
				if (type == ApplicationConstants.NEW_USER) {
					userType = "ADDED";
				} else if (type == ApplicationConstants.ACTIVE_USER) {
					userType = "ACTIVE";
				} else if (type == ApplicationConstants.BLOCKED_USER) {
					userType = "BLOCKED";
				} else if (type == ApplicationConstants.REQUEST_USER) {
					userType = "REQUEST";
				}
				Map<String, String> params = new HashMap<String, String>();
				params.put(HttpRequestConstants.USER_TYPE, userType);
				params.put(HttpRequestConstants.GCM_ID, DatabaseUtil.getInstance().getGcmToken());

				String result = doPost(ApplicationConstants.GET_USERS_URL,
						params);

				ArrayList<UserDetails> appUsers = null;
				int resposneType = 0;

				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						boolean success_state = isSuccessResponse(data);
						resposneType = getRequstStatus(data);

						if (success_state) {
							JSONArray userArray = data
									.getJSONArray("data");
							DatabaseUtil.getInstance().deleteUsers(type);
							appUsers = new ArrayList<>();
							for (int i = 0; i < userArray.length(); i++) {
								JSONObject user = userArray.getJSONObject(i);

								UserDetails userDetails = new UserDetails();
								userDetails.setUserName(user
										.getString(JsonConstancts.FULL_NAME));
								userDetails.setEmaialId(user
										.getString(JsonConstancts.EMAIL));
								userDetails.setPhoneNumber(user
										.getString(JsonConstancts.PHONE_NUMBER));
								userDetails.setUserStatus(type);
								userDetails.setUserType(user
										.getString(JsonConstancts.USER_TYPE));
								boolean isUnBlockPending = false;
								if (user.has(JsonConstancts.IS_UNBLOCK_PENDING)) {
									isUnBlockPending = user.getBoolean(JsonConstancts.IS_UNBLOCK_PENDING);
								}

								if (isUnBlockPending) {
									userDetails.setUserStatus(ApplicationConstants.BLOCKED_USER_2);
								}
								DatabaseUtil.getInstance().storeUserDetails(userDetails);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse(ApplicationConstants.GET_USERS_URL + type, resposneType);
			}
		};

		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable loadUserTypeData() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();

				String result = doPost(ApplicationConstants.GET_USERTYPES_URL,
						params);

				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						boolean success_state = isSuccessResponse(data);

						if (success_state) {
							storeAvailableUserTypes(data);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};

		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable deleteUserType(final int userType) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", userType+"");

				String result = doPost(ApplicationConstants.DELETE_USERTYPE_URL,params);
				int resposneType = 0;
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						boolean success_state = isSuccessResponse(data);
						resposneType = getRequstStatus(data);
						if (success_state) {
							storeAvailableUserTypes(data);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse(ApplicationConstants.DELETE_USERTYPE_URL, resposneType );
			}
		};

		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public Runnable addUserType(final String userType) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("userType", userType+"");

				String result = doPost(ApplicationConstants.ADD_USERTYPE_URL,params);
				int resposneType = 0;
				if (result != null && result.trim().length() > 0) {
					try {
						JSONObject data = new JSONObject(result);
						boolean success_state = isSuccessResponse(data);
						resposneType = getRequstStatus(data);
						if (success_state) {
							storeAvailableUserTypes(data);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				broacastRestResponse(ApplicationConstants.ADD_USERTYPE_URL, resposneType );
			}
		};
		MyChatAdminApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	private void storeAvailableUserTypes(JSONObject data) throws JSONException{
		List<UserType> availableUserTypes = new ArrayList<UserType>();
		JSONArray userTypeArray = data.getJSONArray("userTypes");
		for (int i=0; i<userTypeArray.length(); i++) {
			JSONObject userTypeObj = userTypeArray.getJSONObject(i);
			UserType userType = new UserType();
			userType.setId(userTypeObj.getInt("id"));
			userType.setType(userTypeObj.getString("val"));
			availableUserTypes.add(userType);
		}
		DatabaseUtil.getInstance().setAvailableUserTypes(availableUserTypes);
	}

	public long convertGMTtoLocal(String gmtTime) {
		long timeInMs = 0;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date localDate = dateFormatter.parse(gmtTime);
			timeInMs = localDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeInMs;
	}

	public long getCurrentTime() {
		return new Date().getTime();
	}

	public String doPost(String reqUrl, Map<String, String> postParams) {
		InputStream is = null;
		String result = null;
		try {
			URL url;
			try {
				url = new URL(reqUrl);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Invalid url: " + reqUrl);
			}
			StringBuilder bodyBuilder = new StringBuilder();

			Iterator<Entry<String, String>> iterator = postParams.entrySet()
					.iterator();
			// constructs the POST body using the parameters
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}
			String body = bodyBuilder.toString();
			byte[] bytes = body.getBytes();
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(15000);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setFixedLengthStreamingMode(bytes.length);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				// post the request
				OutputStream out = conn.getOutputStream();
				out.write(bytes);
				out.close();

				if (conn.getResponseCode() == 200) {
					is = conn.getInputStream();
					// Convert the InputStream into a string
					result = readStream(is);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					is.close();
				}
				if (conn != null) {
					conn.disconnect();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Reads an InputStream and converts it to a String.
	 */
	private String readStream(InputStream stream) {
		BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
		String line;
		StringBuffer response = new StringBuffer();
		try {
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String result = response.toString();
		result = result.replaceAll("[\\t\\n\\r]", " ");
		return result.trim();
	}

	private void broacastRestResponse(String url, int responseType) {
		Intent intent = new Intent(ApplicationConstants.REST_RESPONSE_RECEIVER);
		intent.putExtra(ApplicationConstants.REST_URL, url);
		intent.putExtra(ApplicationConstants.REST_SUCCESS, responseType == 200);
		intent.putExtra(ApplicationConstants.REST_RESPONSE_TYPE, responseType);
		MyChatAdminApplication.getInstance().sendBroadcast(intent);
	}

}