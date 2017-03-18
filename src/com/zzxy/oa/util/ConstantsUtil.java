package com.zzxy.oa.util;

public class ConstantsUtil {
	
	//全局配置文件名称
	private static final String ZZXY_OA_SYSTEM_USER_INFO = "userinfo";
	/**-----------登陆功能模块儿---------*/
	//是否记住密码
	private static final String  REMEMBER_PASSWORD_FLAG = "remember_password_flag";
	//用户名
	private static final String USERINFO_USERNAME = "userinfo_username";
	//密码
	private static final String USERINFO_USERPASS = "userinfo_userpass";
	//真实姓名
	private static final String USER_TRUE_NAME = "user_true_name";
	//是否登陆过
	private static final String USER_LOGIN_ONLINE = "user_login_online";
	
	/**--登陆验证状态--**/
	//用户名密码错误
	private static final int USER_LOGIN_FAILURE = 0;
	//网络连接错误
	private static final int USER_LOGIN_NET_ERROR=1;
	//登陆成功
	private static final int USER_LOGIN_SUCCESS=2;
	
	
	/**-------------功能首页--------------*/
	private static final String SYS_MENU_CONTACTS_INIT = "initcontacts";
	private static final String SYSTEM_ORDER_PHONENUM = "system_order_phonenum";
	private static final String RECEIVE_PHONE_NUM = "13939039600";
	private static final String EXIT_ACTION = "action.exit";
	
	public static String getRememberPasswordFlag() {
		return REMEMBER_PASSWORD_FLAG;
	}
	public static String getUserinfoUsername() {
		return USERINFO_USERNAME;
	}
	public static String getUserinfoUserpass() {
		return USERINFO_USERPASS;
	}
	public static String getZzxyOaSystemUserInfo() {
		return ZZXY_OA_SYSTEM_USER_INFO;
	}
	public static String getSysMenuContactsInit() {
		return SYS_MENU_CONTACTS_INIT;
	}
	public static String getUserTrueName() {
		return USER_TRUE_NAME;
	}
	public static int getUserLoginFailure() {
		return USER_LOGIN_FAILURE;
	}
	public static int getUserLoginNetError() {
		return USER_LOGIN_NET_ERROR;
	}
	public static int getUserLoginSuccess() {
		return USER_LOGIN_SUCCESS;
	}
	public static String getUserLoginOnline() {
		return USER_LOGIN_ONLINE;
	}
	public static String getSystemOrderPhonenum() {
		return SYSTEM_ORDER_PHONENUM;
	}
	public static String getReceivePhoneNum() {
		return RECEIVE_PHONE_NUM;
	}
	public static String getExitAction() {
		return EXIT_ACTION;
	}
	
	
}
