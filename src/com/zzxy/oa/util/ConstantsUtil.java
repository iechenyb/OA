package com.zzxy.oa.util;

public class ConstantsUtil {
	
	//ȫ�������ļ�����
	private static final String ZZXY_OA_SYSTEM_USER_INFO = "userinfo";
	/**-----------��½����ģ���---------*/
	//�Ƿ��ס����
	private static final String  REMEMBER_PASSWORD_FLAG = "remember_password_flag";
	//�û���
	private static final String USERINFO_USERNAME = "userinfo_username";
	//����
	private static final String USERINFO_USERPASS = "userinfo_userpass";
	//��ʵ����
	private static final String USER_TRUE_NAME = "user_true_name";
	//�Ƿ��½��
	private static final String USER_LOGIN_ONLINE = "user_login_online";
	
	/**--��½��֤״̬--**/
	//�û����������
	private static final int USER_LOGIN_FAILURE = 0;
	//�������Ӵ���
	private static final int USER_LOGIN_NET_ERROR=1;
	//��½�ɹ�
	private static final int USER_LOGIN_SUCCESS=2;
	
	
	/**-------------������ҳ--------------*/
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
