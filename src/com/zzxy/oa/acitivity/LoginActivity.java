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
 * 用户登陆
 * 
 * @author liweijun 2012-09-22 编写
 */
public class LoginActivity extends Activity {
	private EditText username; // 用户名
	private EditText userpass; // 密码
	private Button loginButton; // 登陆按钮
	private CheckBox check, outLineCheck; // 记住密码，离线登陆
	private TextView outLineLoginText; // 离线登陆
	private final int USER_LOGIN_FAILURE = 0; // 校验失败
	private final int USER_LOGIN_NET_ERROR = 1; // 网络异常
	private final int USER_LOGIN_SUCCESS = 2; // 登陆成功
	private UserInfo user; // 用户对象

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
		checkUserInfo(); // 获取用户账号配置信息
		checkOutLineState(); // 获取离线登陆信息
		loginButton.setOnClickListener(loginListener); // 登陆事件
	}

	/**
	 * @author liweijun 2012-09-22 编写 检查用户配置信息
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
	 * @author liweijun 2012-09-22 编写 检查用户是否选择了离线登陆
	 */
	private void checkOutLineState() {
		if (isLoginOnlined()) {
			outLineCheck.setVisibility(View.VISIBLE);
			outLineLoginText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 登陆监听事件
	 * 
	 * @author liweijun 2012-09-29 编写 <br>
	 */
	private View.OnClickListener loginListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if ("".equals(username.getText().toString())) {
				username.setError("请输入用户名");
				return;
			} else if ("".equals(userpass.getText().toString())) {
				userpass.setError("请输入密码");
				return;
			}
			showDialog(0);
			LoginThread loginThread = new LoginThread();
			loginThread.start();
		}
	};

	/**
	 * 登陆线程
	 * 
	 * @author liweijun 2012-09-29 编写 <br>
	 */
	public class LoginThread extends Thread {
		@Override
		public void run() {
			String user_name = username.getText().toString();
			String user_pass = userpass.getText().toString();
			if (onlineLogin()) {
				user = HttpConnectUtil.login(user_name, user_pass);
				Log.i("在线登陆", "已执行-----------");
			} else {
				user = loginOutLine(user_name, user_pass);
				Log.i("离线登陆", "已执行-----------");
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
	 * @author liweijun 2012-09-29 编写 <br>
	 *         登陆结果显示
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
				Toast.makeText(LoginActivity.this, "登陆成功", 1000).show();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				dismissDialog(0);
				finish();
				break;
			case USER_LOGIN_FAILURE:
				showErrorTips("错误提示", "用户名或者密码错误");
				break;
			case USER_LOGIN_NET_ERROR:
				showErrorTips("错误提示", "网络异常，请重试！");
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @author liweijun 2012-09-29 编写 <br>
	 *         检查用户是否是在线登陆
	 */
	private boolean onlineLogin() {
		if (outLineCheck.isChecked()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @author liweijun 2012-09-29 编写 <br>
	 *         用户离线登陆处理方法
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return 登陆结果
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
	 * @author liweijun 2012-09-29 编写 <br>
	 *         保存用户信息
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
			dialog.setMessage("正在登陆，请稍后...");
			dialog.setCancelable(true);
			return dialog;
		}
		return null;
	}

	/**
	 * @author liweijun 2012-09-19 登陆提示
	 * @param title
	 *            标题
	 * @param message
	 *            内容
	 */
	private void showErrorTips(String title, String message) {
		dismissDialog(0);
		AlertDialog.Builder builder = new Builder(LoginActivity.this);
		builder.setTitle(title).setMessage(message)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
			builder.setTitle("退出程序")
					.setMessage("确定退出程序吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									finish();
								}
							})
					.setNegativeButton("取消",
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
	 * @author liweijun 用户在线登陆后更改标志
	 */
	private void setUserOnlineLoginState() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit().putBoolean(ConstantsUtil.getUserLoginOnline(), true).commit();
	}

	/**
	 * @author liweijun 2012-09-19 判断用户是否是联网登陆过
	 * @return
	 */
	private boolean isLoginOnlined() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getBoolean(ConstantsUtil.getUserLoginOnline(), false);
	}
}