package com.zzxy.oa.acitivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzxy.oa.util.ConstantsUtil;
import com.zzxy.oa.util.DBManager;
import com.zzxy.oa.util.HttpConnectUtil;
import com.zzxy.oa.vo.ContactsInfo;
import com.zzxy.oa.vo.DepartMent;

/**
 * 系统主菜单
 * 
 * @author chenyuanbao 2012-10-09 编写
 */
public class MainActivity extends Activity {
	private static Map<Integer, String> ORDER_MAP_QJ = null; // 请假
	private static Map<Integer, String> ORDER_MAP_CC = null; // 出差
	private static Map<Integer, String> ORDER_MAP_WC = null; // 因私外出
	private static Map<Integer, String> ORDER_MAP_BS = null; // 市内办事
	private static Map<Integer, String> ORDER_MAP_TX = null; // 调休
	private long exitTime = 0; // 初始化退出时间
	private ProgressDialog mProgressDialog; // 进度条
	private String SYSTEM_DATA_UPDATE_MESSAGE = null;
	private final int KSQJ_QJ = 0; // 快速请假--请假
	private final int KSQJ_CC = 1; // 快速请假--出差
	private final int KSQJ_BS = 2; // 快速请假--市内办事
	private final int KSQJ_WC = 3; // 快速请假--因私外出
	private final int KSQJ_TX = 4; // 快速请假--调休
	private final int KSQJ_QJLX = 5; // 快速请假-请假类型
	private final int INIT_CONTACTS = 6; // 初始化系统，从服务器下载数据
	private final int SYSTEM_SETTING  = 7; //系统设置
	private final int SYSTEM_SETTING_UPDATE_DIALOG = 8;
	private final int SYSTEM_SETTING_PHONENUM = 0; //设置接收号码
	private final int SYSTEM_SETTING_UPDATE = 1; //更新通讯录
	private DBManager db; // 数据库工具类对象
	private final int INIT_SUCCESS = 0; // 初始化标志--成功
	private final int INIT_FAILURE = 1; // 初始化标志--失败
	private TextView UserNameTv; // 显示当前登录用户的姓名
	static {
		ORDER_MAP_QJ = new HashMap<Integer, String>();
		ORDER_MAP_CC = new HashMap<Integer, String>();
		ORDER_MAP_WC = new HashMap<Integer, String>();
		ORDER_MAP_BS = new HashMap<Integer, String>();
		ORDER_MAP_TX = new HashMap<Integer, String>();
		// 请假
		ORDER_MAP_QJ.put(0, "qj0,上");
		ORDER_MAP_QJ.put(1, "qj0,下");
		ORDER_MAP_QJ.put(2, "qj0,1");
		ORDER_MAP_QJ.put(3, "qj,上");
		ORDER_MAP_QJ.put(4, "qj,下");
		ORDER_MAP_QJ.put(5, "qj,1");
		// 调休
		ORDER_MAP_TX.put(0, "tx0,上");
		ORDER_MAP_TX.put(1, "tx0,下");
		ORDER_MAP_TX.put(2, "tx0,1");
		ORDER_MAP_TX.put(3, "tx,上");
		ORDER_MAP_TX.put(4, "tx,下");
		ORDER_MAP_TX.put(5, "tx,1");
		// 出差
		ORDER_MAP_CC.put(0, "cc0,上");
		ORDER_MAP_CC.put(1, "cc0,下");
		ORDER_MAP_CC.put(2, "cc0,1");
		ORDER_MAP_CC.put(3, "cc,上");
		ORDER_MAP_CC.put(4, "cc,下");
		ORDER_MAP_CC.put(5, "cc,1");
		// 市内办事
		ORDER_MAP_BS.put(0, "bs0,上");
		ORDER_MAP_BS.put(1, "bs0,下");
		ORDER_MAP_BS.put(2, "bs0,1");
		ORDER_MAP_BS.put(3, "bs,上");
		ORDER_MAP_BS.put(4, "bs,下");
		ORDER_MAP_BS.put(5, "bs,1");
		// 因私外出
		ORDER_MAP_WC.put(0, "wc0,上");
		ORDER_MAP_WC.put(1, "wc0,下");
		ORDER_MAP_WC.put(2, "wc0,1");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UserNameTv = (TextView) findViewById(R.id.tv_user_name);
		UserNameTv.setText(getUserInfo(ConstantsUtil.getUserTrueName()));
		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new ImageAdapter(this));
		checkInitContacts(); // 初始化页面
	}

	/**
	 * 检查系统是否需要初始化
	 * 
	 * @author chenyuanbao 2012-09-29 编写
	 */
	private void checkInitContacts() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		boolean contacts_init_check = sp.getBoolean(
				ConstantsUtil.getSysMenuContactsInit(), false);
		if (!contacts_init_check) {
			SYSTEM_DATA_UPDATE_MESSAGE = "第一次使用，系统正在初始化，请稍后...";
			showDialog(INIT_CONTACTS);
			InitContactsThread initThread = new InitContactsThread();
			initThread.start();
		}
	}

	/**
	 * @author chenyuanbao 2012-09-29 编写 设置系统初始化标志
	 */
	private void setInitFlag() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit().putBoolean(ConstantsUtil.getSysMenuContactsInit(), true)
				.commit();
	}

	/**
	 * 获取用户配置信息
	 * 
	 * @author chenyuanbao 2012-09-24 编写
	 * @param tag
	 *            标志
	 * @return 用户信息
	 */
	private String getUserInfo(String tag) {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getString(tag, "");
	}

	/**
	 * 初始化线程
	 * 
	 * @author chenyuanbao 2012-09-29 编写
	 */
	private class InitContactsThread extends Thread {
		@Override
		public void run() {
			try {
				initContacts();
				initContactsHandler.sendEmptyMessage(INIT_SUCCESS);
			} catch (Exception e) {
				Log.i("INIT_ERROR", e.getMessage());
				initContactsHandler.sendEmptyMessage(INIT_FAILURE);
			}

		}
	}

	/**
	 * @author chenyuanbao 2012-09-29 编写 数据处理线程
	 */
	private Handler initContactsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_SUCCESS:
				removeDialog(INIT_CONTACTS);
				Toast.makeText(MainActivity.this, "数据已更新！", 1000).show();
				break;
			case INIT_FAILURE:
				removeDialog(INIT_CONTACTS);
				Toast.makeText(MainActivity.this, "数据更新失败,请重试！", 1000).show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @author chenyuanbao 2012-09-29 编写 图像菜单内部类
	 */
	public class ImageAdapter extends BaseAdapter {
		// 上下文
		private Context context;
		private int result;
		private AlertDialog.Builder builder = null;

		public ImageAdapter(Context context) {
			super();
			this.context = context;
		}

		@Override
		public int getCount() {
			return 9;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.grid_item, null);
			ImageView imageView = new ImageView(context);
			TextView mTextView = (TextView) convertView
					.findViewById(R.id.textview);
			switch (position) {
			// 常用指令
			case 0:
				mTextView.setText(R.string.menu_allorder);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.allorder));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(KSQJ_QJLX);
					}
				});
				break;
			// 添加指令
			case 1:
				mTextView.setText(R.string.menu_sendorder);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.sendorder));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, SendOrderActivity.class);
						startActivity(intent);
					}
				});
				break;
			// 考勤查询
			case 2:
				mTextView.setText(R.string.kaoqin);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.kaoqin));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, AttendanceActivity.class);
						startActivity(intent);
					}
				});
				break;
			// 通讯录查询
			case 3:
				mTextView.setText(R.string.tongxunlu);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.phone));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, ContactsActivity.class);
						startActivity(intent);
					}
				});
				break;
			// 在线查询
			case 4:
				mTextView.setText(R.string.online_search);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.online_search));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, OnLineSearchActivity.class);
						startActivity(intent);
					}
				});
				break;
			// 帮助
			case 5:
				mTextView.setText(R.string.menu_help);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.help));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, HelpActivity.class);
						startActivity(intent);
					}
				});
				break;
			// 设置
			case 6:
				mTextView.setText(R.string.menu_setting);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.setting));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//showSettingDialg();
						showDialog(SYSTEM_SETTING);
					}
				});
				break;
			// 退出
			case 7:
				mTextView.setText(R.string.login_exit);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.exit));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						exit();
					}
				});
				break;
			// 重新登录
			case 8:
				mTextView.setText(R.string.login_login);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.login));
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						reLogin();
					}
				});
				break;
			}
			return convertView;
		}

		/**
		 * @author chenyuanbao 2012-10-08 编写 <br>
		 *         退出程序
		 */
		private void exit() {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("提示")
					.setMessage("确定退出吗?")
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
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		}

		/**
		 * @author chenyuanbao 重新登陆
		 */
		private void reLogin() {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("提示")
					.setMessage("确定注销吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									startActivity(new Intent(MainActivity.this,
											LoginActivity.class));
									finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		}

	}

	// 发送信息
	private void sendMessage(String messages) {
		String phoneNum = getPhoneNum();
		Log.i("messages-------->", messages);
		if (messages != null && !"".equals(messages)) {
			Log.i("快速发短信接收手机号为：", phoneNum);
			Log.i("快速发短信获取的短信内容为:", messages);
			Uri smsToUri = Uri.parse("smsto:" + phoneNum);
			Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			intent.putExtra("sms_body", messages);
			startActivity(intent);
		}
	}

	/**
	 * @author chenyuanbao 用户按返回键退出程序事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	/**
	 * 设置短信接收号码
	 */
	public void showSettingDialg() {
		final AlertDialog.Builder builder = new Builder(MainActivity.this);
		final EditText inputServer = new EditText(this);
		inputServer.setText(getPhoneNum());
		inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
		inputServer
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
		builder.setTitle("设置短信接收号码")
				.setView(inputServer)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String phnum = inputServer.getText().toString();
						if ("".equals(phnum) || phnum.length() != 11) {
							Toast.makeText(MainActivity.this, "请输入正确的接收号码!",
									1000).show();
							showSettingDialg();
							return;
						}
						restorePhoneNum(ConstantsUtil.getSystemOrderPhonenum(),
								phnum);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	/**
	 * @author chenyuanbao 2012-10-08 编写 <br>
	 *         说明：获取配置文件中的接收手机号
	 * @return
	 */
	private String getPhoneNum() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getString(ConstantsUtil.getSystemOrderPhonenum(), "");
	}

	/**
	 * @author chenyuanbao 2012-10-08 编写<br>
	 *         说明：重置短信指令接收号码
	 * @param tag
	 * @param value
	 */
	private void restorePhoneNum(String tag, String value) {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit().putString(tag, value).commit();
		Toast.makeText(this, "设置成功！", Toast.LENGTH_LONG).show();
	}

	/**
	 * @author chenyuanbao 2012-09-27 编写写入数据到通讯录 <br>
	 */
	private void initContacts() {
		List<ContactsInfo> contactsList = HttpConnectUtil.getContactsInfoList();
		List<DepartMent> deptList = HttpConnectUtil.getDepts();
		db = new DBManager(this);
		db.deleteAll();
		db.addContacts(contactsList);
		db.addDepts(deptList);
		db.closeDB();
		setInitFlag();
	}
	
	/**
	 * @author chenyuanbao 2012-10-10 编写 <br>
	 *    系统设置菜单处理
	 * @param id 菜单索引
	 */
	private void UserSetting(int id){
		switch (id) {
		case SYSTEM_SETTING_PHONENUM: //设置接收号码
			showSettingDialg();
			break;
		case SYSTEM_SETTING_UPDATE: //更新数据
			showDialog(SYSTEM_SETTING_UPDATE_DIALOG);
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INIT_CONTACTS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(SYSTEM_DATA_UPDATE_MESSAGE);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return mProgressDialog;
		case KSQJ_QJLX: // 请假类型
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("请假类型")
					.setItems(R.array.qjtypeItem,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									showDialog(which);
								}
							}).create();
		case KSQJ_QJ: // 请假
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("请假")
					.setItems(R.array.ksqjItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_QJ.get(which));
								}
							}).create();
		case KSQJ_CC: // 出差
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("出差")
					.setItems(R.array.ksccItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_CC.get(which));
								}
							}).create();
		case KSQJ_BS: // 市内办事
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("市内办事")
					.setItems(R.array.ksbsItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_BS.get(which));
								}
							}).create();
		case KSQJ_WC: //因私外出
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("因私外出")
					.setItems(R.array.kswcItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_WC.get(which));
								}
							}).create();
		case KSQJ_TX: // 调休
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("调休")
					.setItems(R.array.kstxItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_TX.get(which));
								}
							}).create();
		case SYSTEM_SETTING:
			return new AlertDialog.Builder(MainActivity.this)
			.setTitle("系统设置")
			.setItems(R.array.system_setting_items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							UserSetting(which);
						}
					}).create();
		case SYSTEM_SETTING_UPDATE_DIALOG:
			return new AlertDialog.Builder(MainActivity.this)
			.setTitle("数据更新").setMessage("确实要更新本地数据吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SYSTEM_DATA_UPDATE_MESSAGE = "数据更新中，请勿退出程序...";
					showDialog(INIT_CONTACTS);
					InitContactsThread initThread = new InitContactsThread();
					initThread.start();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create();
		default:
			break;
		}
		return null;
	}

}
