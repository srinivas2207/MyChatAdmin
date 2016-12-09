package com.shree.mychatadmin.util;

public class ApplicationConstants {

	public static final String ADMIN_MAILID = "admin.mychat@gmail.com";

	
	public static final String BASE_URL = "https://mychat-shree2287.rhcloud.com/api";
    public static final String PROFILE_IMAGE_UPLOAD_URL = "http://mychatfileserver-shree2287.rhcloud.com/file/upload";

//	public static final String BASE_URL = "http://10.67.112.134:8080/MyChatApp/api";
	public static final String ADD_USER_URL = BASE_URL+"/server/addUser";
	public static final String EDIT_USER_URL = BASE_URL+"/server/editUser";
	public static final String DELETE_USER_URL = BASE_URL+"/server/deleteUser";
	public static final String BLOCK_USER_URL = BASE_URL+"/server/blockUser";
	public static final String UNBLOCK_USER_URL = BASE_URL+"/server/unBlockUser";
	public static final String ACCEPT_REQUEST = BASE_URL+"/server/acceptRequest";
	public static final String REJECT_REQUEST = BASE_URL+"/server/rejectRequest";

	public static final String GET_USERS_URL = BASE_URL+"/server/getUsers";

	public static final String GET_USERTYPES_URL = BASE_URL+"/server/getUserTypes";
	public static final String ADD_USERTYPE_URL = BASE_URL+"/server/addUserType";
	public static final String DELETE_USERTYPE_URL = BASE_URL+"/server/deleteUserType";

	public static final String REST_URL = "URL";
	public static final String REST_SUCCESS = "isSuccess";
	public static final String REST_RESPONSE_TYPE = "responseType";

	public static int NEW_USER = 1;
	public static int ACTIVE_USER = 2;
	public static int BLOCKED_USER = 3;
	public static int BLOCKED_USER_2 = 4;
	public static int REQUEST_USER = 5;

	public static final String REST_RESPONSE_RECEIVER = "com.mychatadmin.REST_RESPONSE";
}
