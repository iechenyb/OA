package com.zzxy.oa.acitivity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zzxy.oa.util.DBManager;
import com.zzxy.oa.util.HttpConnectUtil;
import com.zzxy.oa.vo.OnLineSearch;

/**
 * 在线查询
 * @author liweijun 2012-10-09 编写
 */
public class OnLineSearchActivity extends Activity {

	private ListView displayListView;  //数据展示View
	private ProgressBar pb; //进度条
	private TextView loadingTv; //加载提示
	private TextView deptView; //部门查询View
	private final int CALL_PHONENUM = 0; // 拨打电话
	private final int SEND_MESSAGE = 1; // 发送短信
	private final int COPY_PHONENUM = 2; // 保存号码
	private final int USER_OPERATE_LONG_CLICK_ITEM = 3; //弹出菜单
	private String department = null; //部门名称
	private String name = null; //姓名
	private Spinner sp; //部门选择器
	private EditText username_view; //姓名
	private DBManager db; //数据库对象
	private String[] depts = null; //部门数组
	private int dept_index = 0; //部门索引
	private final String default_title = "所有部门"; //默认显示名称
	private List<OnLineSearch> osList = null; //查询数据集合
	private final int SUCCESS_SEARCH = 0; //加载成功
	private final int FAILURE_SEARCH = 1; //加载失败
	private final int ERROR_SEARCH = 2; //加载异常
	private int selectID = 0; //通讯录索引

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_search);
		//检索数据View
		deptView = (TextView) findViewById(R.id.contacts_search_button_online);
		//数据展示View
		displayListView = (ListView) findViewById(R.id.contactslistViewOnline);
		//进度条 
		pb = (ProgressBar)findViewById(R.id.onlinedatasloadingBar);
		//进度条提示信息
		loadingTv = (TextView)findViewById(R.id.onlinedatasloadingText);
		//长时间单击通讯录事件
		displayListView.setOnItemLongClickListener(contactsLongClickListener);
		//数据检索点击事件
		deptView.setOnClickListener(searchClickListener);
		//初始化部门数据
		initDepartMents();
		//弹出条件查询框
		showSearchDialog();
	}
	
	/**
	 * @author liweijun 2012-10-09 编写
	 * 长按通讯录事件
	 */
	private OnItemLongClickListener contactsLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long arg3) {
		    selectID = position;
		    showDialog(USER_OPERATE_LONG_CLICK_ITEM);
			return false;
		}
	};
	
	/**
	 * @author liweijun 2012-10-09 编写
	 * 数据检索点击事件
	 */
	private View.OnClickListener searchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showSearchDialog();
		}
	};
	
	/**
	 * @author liweijun 2012-10-09 编写
	 * 展示数据
	 */
	private void displayDatas() {
		SearchThread thread = new SearchThread();
		thread.start();
		displayListView.setVisibility(View.GONE);
		pb.setVisibility(View.VISIBLE);
		loadingTv.setVisibility(View.VISIBLE);
	}
	/**
	 * @author liweijun 2012-10-09 编写
	 * 隐藏进度条
	 */
	private void hiddenProgressBar(){
		pb.setVisibility(View.GONE);
		loadingTv.setVisibility(View.GONE);
	}
	/**
	 * @author liweijun 2012-10-09 编写
	 * 组装ListView
	 */
	private void diplayView(){
		displayListView.setVisibility(View.VISIBLE);
		ContactssListAdapter contactsAdapter = new ContactssListAdapter(
				osList.size(), this, osList);
		displayListView.setAdapter(contactsAdapter);
		contactsAdapter.notifyDataSetChanged();
		displayListView.invalidate();
	}
	
    /**
     * @author liweijun 2012-10-09 编写
     * 数据加载线程
     */
	public class SearchThread extends Thread {
		@Override
		public void run() {
			osList = HttpConnectUtil.findOsList(department, name);
			if (osList == null) {
				handler.sendEmptyMessage(ERROR_SEARCH);
			} else if (osList.isEmpty()) {
				handler.sendEmptyMessage(FAILURE_SEARCH);
			} else {
				handler.sendEmptyMessage(SUCCESS_SEARCH);
			}
		}
	}
	/**
	 * @author liweijun 2012-10-09 编写
	 * 进程处理
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ERROR_SEARCH:
				hiddenProgressBar();
				Toast.makeText(OnLineSearchActivity.this, "网络连接失败!", 1000).show();
				break;
			case SUCCESS_SEARCH:
				hiddenProgressBar();
				diplayView();
				break;
			case FAILURE_SEARCH:
				hiddenProgressBar();
				Toast.makeText(OnLineSearchActivity.this, "服务器没有数据返回!", 1000).show();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * @author liweijun 2012-10-09 编写
	 * 初始化部门信息
	 */
	private void initDepartMents(){
		try {
			db = new DBManager(this);
			List<String> deptList = db.findDeptList();
			db.closeDB();
			Log.i("查询部门信息", deptList.toString());
			depts = new String[(deptList.size()+1)];
			depts[0] = default_title;
			for (int i = 0; i < deptList.size(); i++) {
				depts[i+1] = deptList.get(i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @author liweijun 2012-10-09 编写
	 *  弹出查询选择框
	 */
	private void showSearchDialog() {
		ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, depts);   
		deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.alert_dialog_search_entry, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				OnLineSearchActivity.this);
		builder.setTitle("数据检索");
		builder.setView(view);
		username_view = (EditText) view.findViewById(R.id.online_username_edit);
		sp = (Spinner) view.findViewById(R.id.online_department_search_spinner);
		sp.setAdapter(deptAdapter);
		sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				dept_index = arg2;
				 Log.i("部门选择", depts[dept_index]);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			} 
			
		});
		builder.setPositiveButton("查询", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				name = username_view.getText().toString();
				department = depts[dept_index];
				if (default_title.equals(department)) {
					department = null;
				}
				displayDatas();
				dialog.dismiss();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
	
	/**
	 * @author liweijun 2012-10-09 编写
	 *  内部类
	 */
	private class ContactssListAdapter extends BaseAdapter {
		private Context context;
		private int count = 0;
		private List<OnLineSearch> oslist;

		public ContactssListAdapter(int count, Context context,
				List<OnLineSearch> oslist) {
			this.count = count;
			this.context = context;
			this.oslist = oslist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			if (!oslist.isEmpty()) {
				LayoutInflater inflater = LayoutInflater.from(context);
				LinearLayout layout = (LinearLayout) inflater.inflate(
						R.layout.contacts_list_item, null);
				OnLineSearch os = oslist.get(position);
				TextView textview_name = (TextView) layout
						.findViewById(R.id.xyoa_contacts_name_TextView);
				TextView textview_detail = (TextView) layout
						.findViewById(R.id.xyoa_contacts_detail_TextView);
				textview_name.setText(os.getUsername() + "["
						+ os.getDepartMent() + "]");
				String tempContent = "";
				if (os.getContent() != null) {
					if (!"".equals(os.getContent())) {
						tempContent += "考勤:" + os.getContent();
					}
				}
				textview_detail.setText("手机:" + os.getPhoneNum() + " "
						+ tempContent);
				return layout;
			}
			return null;
		}
	}
	
	private void showOperates(int operateID){
		switch (operateID) {
		//拨打号码
		case CALL_PHONENUM:
			callPhoneNum();
			break;
		//发送短信
		case SEND_MESSAGE:
		    sendMessages();
			break;
		//复制号码
		case COPY_PHONENUM:
			copyPhoneNum();
		break;
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 说明：拨打电话
	 */
	private void callPhoneNum(){
		OnLineSearch os = osList.get(selectID);
		if (os != null) {
			Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+os.getPhoneNum()));
			startActivity(intent);
		}
	}
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 发送短信
	 */
	private void sendMessages(){
		OnLineSearch os = osList.get(selectID);
		if (os != null) {
			Uri smsToUri = Uri.parse("smsto:"+os.getPhoneNum());
			Intent intent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri);
			intent.putExtra("sms_body", "");
			startActivity( intent);
		}
	}
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 复制到粘贴板
	 */
	private void copyPhoneNum(){
		OnLineSearch os = osList.get(selectID);
		if (os != null) {
			ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(os.getPhoneNum());
			Toast.makeText(this, "已复制到粘贴板!", 1000).show();
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 *系统对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case USER_OPERATE_LONG_CLICK_ITEM:
			return new AlertDialog.Builder(OnLineSearchActivity.this)
            .setTitle(R.string.quick_operate)
            .setItems(R.array.quick_operate_items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	showOperates(which);
                }
            }).create();
		default:
			break;
		}
		return null;
	}
	
}
