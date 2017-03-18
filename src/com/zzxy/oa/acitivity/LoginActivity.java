package com.zzxy.oa.acitivity;

import com.zzxy.oa.util.ConstantsUtil;
import com.zzxy.oa.util.HttpConnectUtil;
import com.zzxy.oa.vo.UserInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �û���½
 * 
 * @author liweijun 2012-09-22 ��д
 */
public class LoginActivity extends Activity {
	private EditText username; // �û���
	private EditText userpass; // ����
	private Button loginButton; // ��½��ť
	private CheckBox check, outLineCheck; // ��ס���룬���ߵ�½
	private TextView outLineLoginText; // ���ߵ�½
	private final int USER_LOGIN_FAILURE = 0; // У��ʧ��
	private final int USER_LOGIN_NET_ERROR = 1; // �����쳣
	private final int USER_LOGIN_SUCCESS = 2; // ��½�ɹ�
	private UserInfo user; // �û�����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.username_edit);
		userpass = (EditText) findViewById(R.id.password_edit);
		loginButton = (Button) findViewById(R.id.login_button);
		check = (CheckBox) findViewById(R.id.password_rem_checkbox);
		outLineCheck = (CheckBox) findViewById(R.id.login_check_outline);
		outLineLoginText = (TextView) findViewById(R.id.login_check_outline_text);
		checkUserInfo(); // ��ȡ�û��˺�������Ϣ
		checkOutLineState(); // ��ȡ���ߵ�½��Ϣ
		loginButton.setOnClickListener(loginListener); // ��½�¼�
	}

	/**
	 * @author liweijun 2012-09-22 ��д ����û�������Ϣ
	 */
	private void checkUserInfo() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		boolean checked = sp.getBoolean(
				ConstantsUtil.getRememberPasswordFlag(), false);
		if (checked) {
			String user_name = sp.getString(
					ConstantsUtil.getUserinfoUsername(), "");
			String user_pass = sp.getString(
					ConstantsUtil.getUserinfoUserpass(), "");
			this.username.setText(user_name);
			this.userpass.setText(user_pass);
			this.check.setChecked(true);
		} else {
			this.username.setText("");
			this.userpass.setText("");
			this.check.setChecked(false);
		}
	}

	/**
	 * @author liweijun 2012-09-22 ��д ����û��Ƿ�ѡ�������ߵ�½
	 */
	private void checkOutLineState() {
		if (isLoginOnlined()) {
			outLineCheck.setVisibility(View.VISIBLE);
			outLineLoginText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ��½�����¼�
	 * 
	 * @author liweijun 2012-09-29 ��д <br>
	 */
	private View.OnClickListener loginListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if ("".equals(username.getText().toString())) {
				username.setError("�������û���");
				return;
			} else if ("".equals(userpass.getText().toString())) {
				userpass.setError("����������");
				return;
			}
			showDialog(0);
			LoginThread loginThread = new LoginThread();
			loginThread.start();
		}
	};

	/**
	 * ��½�߳�
	 * 
	 * @author liweijun 2012-09-29 ��д <br>
	 */
	public class LoginThread extends Thread {
		@Override
		public void run() {
			String user_name = username.getText().toString();
			String user_pass = userpass.getText().toString();
			if (onlineLogin()) {
				user = HttpConnectUtil.login(user_name, user_pass);
				Log.i("���ߵ�½", "��ִ��-----------");
			} else {
				user = loginOutLine(user_name, user_pass);
				Log.i("���ߵ�½", "��ִ��-----------");
			}
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			if (user.getLoginstate() == ConstantsUtil.getUserLoginFailure()) {
				b.putInt("result", ConstantsUtil.getUserLoginFailure());
			} else if (user.getLoginstate() == ConstantsUtil
					.getUserLoginNetError()) {
				b.putInt("result", ConstantsUtil.getUserLoginNetError());
			} else if (user.getLoginstate() == ConstantsUtil
					.getUserLoginSuccess()) {
				b.putInt("result", ConstantsUtil.getUserLoginSuccess());
			}
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

	/**
	 * @author liweijun 2012-09-29 ��д <br>
	 *         ��½�����ʾ
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int result_flag = msg.getData().getInt("result");
			switch (result_flag) {
			case USER_LOGIN_SUCCESS:
				if (onlineLogin()) {
					saveUserInfo();
					setUserOnlineLoginState();
				}
				Toast.makeText(LoginActivity.this, "��½�ɹ�", 1000).show();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				dismissDialog(0);
				finish();
				break;
			case USER_LOGIN_FAILURE:
				showErrorTips("������ʾ", "�û��������������");
				break;
			case USER_LOGIN_NET_ERROR:
				showErrorTips("������ʾ", "�����쳣�������ԣ�");
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @author liweijun 2012-09-29 ��д <br>
	 *         ����û��Ƿ������ߵ�½
	 */
	private boolean onlineLogin() {
		if (outLineCheck.isChecked()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @author liweijun 2012-09-29 ��д <br>
	 *         �û����ߵ�½������
	 * @param username
	 *            �û���
	 * @param password
	 *            ����
	 * @return ��½���
	 */
	private UserInfo loginOutLine(String username, String password) {
		UserInfo userinfo = null;
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		String tempUserName = sp.getString(ConstantsUtil.getUserinfoUsername(),
				"");
		String tempUserPass = sp.getString(ConstantsUtil.getUserinfoUserpass(),
				"");
		if (tempUserName.equals(username) && tempUserPass.equals(password)) {
			userinfo = new UserInfo(null, null, USER_LOGIN_SUCCESS);
		} else {
			userinfo = new UserInfo(null, null, USER_LOGIN_FAILURE);
		}
		return userinfo;
	}

	/**
	 * @author liweijun 2012-09-29 ��д <br>
	 *         �����û���Ϣ
	 */
	private void saveUserInfo() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit()
				.putString(ConstantsUtil.getUserTrueName(), user.getName())
				.putString(ConstantsUtil.getSystemOrderPhonenum(),
						ConstantsUtil.getReceivePhoneNum())
				.putString(ConstantsUtil.getUserinfoUsername(),
						username.getText().toString())
				.putString(ConstantsUtil.getUserinfoUserpass(),
						userpass.getText().toString()).commit();
		if (this.check.isChecked()) {
			sp.edit().putBoolean(ConstantsUtil.getRememberPasswordFlag(), true)
					.commit();
		} else {
			sp.edit()
					.putBoolean(ConstantsUtil.getRememberPasswordFlag(), false)
					.commit();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setIndeterminate(true);
			dialog.setMessage("���ڵ�½�����Ժ�...");
			dialog.setCancelable(true);
			return dialog;
		}
		return null;
	}

	/**
	 * @author liweijun 2012-09-19 ��½��ʾ
	 * @param title
	 *            ����
	 * @param message
	 *            ����
	 */
	private void showErrorTips(String title, String message) {
		dismissDialog(0);
		AlertDialog.Builder builder = new Builder(LoginActivity.this);
		builder.setTitle(title).setMessage(message)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new Builder(LoginActivity.this);
			builder.setTitle("�˳�����")
					.setMessage("ȷ���˳�������?")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									finish();
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							}).show();
			return true;
		}
		return false;
	}

	/**
	 * @author liweijun �û����ߵ�½����ı�־
	 */
	private void setUserOnlineLoginState() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit().putBoolean(ConstantsUtil.getUserLoginOnline(), true).commit();
	}

	/**
	 * @author liweijun 2012-09-19 �ж��û��Ƿ���������½��
	 * @return
	 */
	private boolean isLoginOnlined() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getBoolean(ConstantsUtil.getUserLoginOnline(), false);
	}
}