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
 * ϵͳ���˵�
 * 
 * @author chenyuanbao 2012-10-09 ��д
 */
public class MainActivity extends Activity {
	private static Map<Integer, String> ORDER_MAP_QJ = null; // ���
	private static Map<Integer, String> ORDER_MAP_CC = null; // ����
	private static Map<Integer, String> ORDER_MAP_WC = null; // ��˽���
	private static Map<Integer, String> ORDER_MAP_BS = null; // ���ڰ���
	private static Map<Integer, String> ORDER_MAP_TX = null; // ����
	private long exitTime = 0; // ��ʼ���˳�ʱ��
	private ProgressDialog mProgressDialog; // ������
	private String SYSTEM_DATA_UPDATE_MESSAGE = null;
	private final int KSQJ_QJ = 0; // �������--���
	private final int KSQJ_CC = 1; // �������--����
	private final int KSQJ_BS = 2; // �������--���ڰ���
	private final int KSQJ_WC = 3; // �������--��˽���
	private final int KSQJ_TX = 4; // �������--����
	private final int KSQJ_QJLX = 5; // �������-�������
	private final int INIT_CONTACTS = 6; // ��ʼ��ϵͳ���ӷ�������������
	private final int SYSTEM_SETTING  = 7; //ϵͳ����
	private final int SYSTEM_SETTING_UPDATE_DIALOG = 8;
	private final int SYSTEM_SETTING_PHONENUM = 0; //���ý��պ���
	private final int SYSTEM_SETTING_UPDATE = 1; //����ͨѶ¼
	private DBManager db; // ���ݿ⹤�������
	private final int INIT_SUCCESS = 0; // ��ʼ����־--�ɹ�
	private final int INIT_FAILURE = 1; // ��ʼ����־--ʧ��
	private TextView UserNameTv; // ��ʾ��ǰ��¼�û�������
	static {
		ORDER_MAP_QJ = new HashMap<Integer, String>();
		ORDER_MAP_CC = new HashMap<Integer, String>();
		ORDER_MAP_WC = new HashMap<Integer, String>();
		ORDER_MAP_BS = new HashMap<Integer, String>();
		ORDER_MAP_TX = new HashMap<Integer, String>();
		// ���
		ORDER_MAP_QJ.put(0, "qj0,��");
		ORDER_MAP_QJ.put(1, "qj0,��");
		ORDER_MAP_QJ.put(2, "qj0,1");
		ORDER_MAP_QJ.put(3, "qj,��");
		ORDER_MAP_QJ.put(4, "qj,��");
		ORDER_MAP_QJ.put(5, "qj,1");
		// ����
		ORDER_MAP_TX.put(0, "tx0,��");
		ORDER_MAP_TX.put(1, "tx0,��");
		ORDER_MAP_TX.put(2, "tx0,1");
		ORDER_MAP_TX.put(3, "tx,��");
		ORDER_MAP_TX.put(4, "tx,��");
		ORDER_MAP_TX.put(5, "tx,1");
		// ����
		ORDER_MAP_CC.put(0, "cc0,��");
		ORDER_MAP_CC.put(1, "cc0,��");
		ORDER_MAP_CC.put(2, "cc0,1");
		ORDER_MAP_CC.put(3, "cc,��");
		ORDER_MAP_CC.put(4, "cc,��");
		ORDER_MAP_CC.put(5, "cc,1");
		// ���ڰ���
		ORDER_MAP_BS.put(0, "bs0,��");
		ORDER_MAP_BS.put(1, "bs0,��");
		ORDER_MAP_BS.put(2, "bs0,1");
		ORDER_MAP_BS.put(3, "bs,��");
		ORDER_MAP_BS.put(4, "bs,��");
		ORDER_MAP_BS.put(5, "bs,1");
		// ��˽���
		ORDER_MAP_WC.put(0, "wc0,��");
		ORDER_MAP_WC.put(1, "wc0,��");
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
		checkInitContacts(); // ��ʼ��ҳ��
	}

	/**
	 * ���ϵͳ�Ƿ���Ҫ��ʼ��
	 * 
	 * @author chenyuanbao 2012-09-29 ��д
	 */
	private void checkInitContacts() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		boolean contacts_init_check = sp.getBoolean(
				ConstantsUtil.getSysMenuContactsInit(), false);
		if (!contacts_init_check) {
			SYSTEM_DATA_UPDATE_MESSAGE = "��һ��ʹ�ã�ϵͳ���ڳ�ʼ�������Ժ�...";
			showDialog(INIT_CONTACTS);
			InitContactsThread initThread = new InitContactsThread();
			initThread.start();
		}
	}

	/**
	 * @author chenyuanbao 2012-09-29 ��д ����ϵͳ��ʼ����־
	 */
	private void setInitFlag() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit().putBoolean(ConstantsUtil.getSysMenuContactsInit(), true)
				.commit();
	}

	/**
	 * ��ȡ�û�������Ϣ
	 * 
	 * @author chenyuanbao 2012-09-24 ��д
	 * @param tag
	 *            ��־
	 * @return �û���Ϣ
	 */
	private String getUserInfo(String tag) {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getString(tag, "");
	}

	/**
	 * ��ʼ���߳�
	 * 
	 * @author chenyuanbao 2012-09-29 ��д
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
	 * @author chenyuanbao 2012-09-29 ��д ���ݴ����߳�
	 */
	private Handler initContactsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_SUCCESS:
				removeDialog(INIT_CONTACTS);
				Toast.makeText(MainActivity.this, "�����Ѹ��£�", 1000).show();
				break;
			case INIT_FAILURE:
				removeDialog(INIT_CONTACTS);
				Toast.makeText(MainActivity.this, "���ݸ���ʧ��,�����ԣ�", 1000).show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @author chenyuanbao 2012-09-29 ��д ͼ��˵��ڲ���
	 */
	public class ImageAdapter extends BaseAdapter {
		// ������
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
			// ����ָ��
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
			// ���ָ��
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
			// ���ڲ�ѯ
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
			// ͨѶ¼��ѯ
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
			// ���߲�ѯ
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
			// ����
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
			// ����
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
			// �˳�
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
			// ���µ�¼
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
		 * @author chenyuanbao 2012-10-08 ��д <br>
		 *         �˳�����
		 */
		private void exit() {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("��ʾ")
					.setMessage("ȷ���˳���?")
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
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		}

		/**
		 * @author chenyuanbao ���µ�½
		 */
		private void reLogin() {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("��ʾ")
					.setMessage("ȷ��ע����?")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									startActivity(new Intent(MainActivity.this,
											LoginActivity.class));
									finish();
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		}

	}

	// ������Ϣ
	private void sendMessage(String messages) {
		String phoneNum = getPhoneNum();
		Log.i("messages-------->", messages);
		if (messages != null && !"".equals(messages)) {
			Log.i("���ٷ����Ž����ֻ���Ϊ��", phoneNum);
			Log.i("���ٷ����Ż�ȡ�Ķ�������Ϊ:", messages);
			Uri smsToUri = Uri.parse("smsto:" + phoneNum);
			Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			intent.putExtra("sms_body", messages);
			startActivity(intent);
		}
	}

	/**
	 * @author chenyuanbao �û������ؼ��˳������¼�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	/**
	 * ���ö��Ž��պ���
	 */
	public void showSettingDialg() {
		final AlertDialog.Builder builder = new Builder(MainActivity.this);
		final EditText inputServer = new EditText(this);
		inputServer.setText(getPhoneNum());
		inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
		inputServer
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
		builder.setTitle("���ö��Ž��պ���")
				.setView(inputServer)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String phnum = inputServer.getText().toString();
						if ("".equals(phnum) || phnum.length() != 11) {
							Toast.makeText(MainActivity.this, "��������ȷ�Ľ��պ���!",
									1000).show();
							showSettingDialg();
							return;
						}
						restorePhoneNum(ConstantsUtil.getSystemOrderPhonenum(),
								phnum);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	/**
	 * @author chenyuanbao 2012-10-08 ��д <br>
	 *         ˵������ȡ�����ļ��еĽ����ֻ���
	 * @return
	 */
	private String getPhoneNum() {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getString(ConstantsUtil.getSystemOrderPhonenum(), "");
	}

	/**
	 * @author chenyuanbao 2012-10-08 ��д<br>
	 *         ˵�������ö���ָ����պ���
	 * @param tag
	 * @param value
	 */
	private void restorePhoneNum(String tag, String value) {
		SharedPreferences sp = getSharedPreferences(
				ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		sp.edit().putString(tag, value).commit();
		Toast.makeText(this, "���óɹ���", Toast.LENGTH_LONG).show();
	}

	/**
	 * @author chenyuanbao 2012-09-27 ��дд�����ݵ�ͨѶ¼ <br>
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
	 * @author chenyuanbao 2012-10-10 ��д <br>
	 *    ϵͳ���ò˵�����
	 * @param id �˵�����
	 */
	private void UserSetting(int id){
		switch (id) {
		case SYSTEM_SETTING_PHONENUM: //���ý��պ���
			showSettingDialg();
			break;
		case SYSTEM_SETTING_UPDATE: //��������
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
		case KSQJ_QJLX: // �������
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("�������")
					.setItems(R.array.qjtypeItem,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									showDialog(which);
								}
							}).create();
		case KSQJ_QJ: // ���
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("���")
					.setItems(R.array.ksqjItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_QJ.get(which));
								}
							}).create();
		case KSQJ_CC: // ����
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("����")
					.setItems(R.array.ksccItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_CC.get(which));
								}
							}).create();
		case KSQJ_BS: // ���ڰ���
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("���ڰ���")
					.setItems(R.array.ksbsItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_BS.get(which));
								}
							}).create();
		case KSQJ_WC: //��˽���
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("��˽���")
					.setItems(R.array.kswcItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_WC.get(which));
								}
							}).create();
		case KSQJ_TX: // ����
			return new AlertDialog.Builder(MainActivity.this)
					.setTitle("����")
					.setItems(R.array.kstxItems,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendMessage(ORDER_MAP_TX.get(which));
								}
							}).create();
		case SYSTEM_SETTING:
			return new AlertDialog.Builder(MainActivity.this)
			.setTitle("ϵͳ����")
			.setItems(R.array.system_setting_items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							UserSetting(which);
						}
					}).create();
		case SYSTEM_SETTING_UPDATE_DIALOG:
			return new AlertDialog.Builder(MainActivity.this)
			.setTitle("���ݸ���").setMessage("ȷʵҪ���±���������").setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SYSTEM_DATA_UPDATE_MESSAGE = "���ݸ����У������˳�����...";
					showDialog(INIT_CONTACTS);
					InitContactsThread initThread = new InitContactsThread();
					initThread.start();
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
