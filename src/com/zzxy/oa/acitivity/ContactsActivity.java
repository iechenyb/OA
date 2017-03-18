package com.zzxy.oa.acitivity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzxy.oa.util.DBManager;
import com.zzxy.oa.vo.ContactsInfo;

/**
 * @author liweijun 2012-09-29 编写
 *  部门通讯录
 */
public class ContactsActivity extends Activity {

	private EditText searchEditText; //搜索框
	private List<ContactsInfo> contactsList; //通讯录数据集
	private ListView displayListView; //数据展示View
	private TextView countTextView; //查询结果数目View
	private TextView deptView; //按部门查询View
	private ImageView deptButton; //重置部门按钮
	private int dept_index; //部门索引
	private String dept_name = null; //部门名称
	private final String defautTitle = "所有部门"; //默认显示
	private String[] depts = null; //部门数组
	private DBManager db; //数据库对象
	private int selectID = 0; //通讯录索引
	private final int CALL_PHONENUM = 0; //拨打电话
	private final int SEND_MESSAGE = 1; //发送短信
	private final int COPY_PHONENUM = 2; //保存号码
	private final int USER_OPERATE_SELECT_DEPT = 1; //弹出部门选择框
	private final int USER_OPERATE_LONG_CLICK_ITEM = 2; //通讯录长按事件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		searchEditText = (EditText) findViewById(R.id.edit_search);
		displayListView = (ListView) findViewById(R.id.contactslistView);
		countTextView = (TextView)findViewById(R.id.edit_search_count);
		deptView = (TextView)findViewById(R.id.contacts_department);
		deptButton = (ImageView)findViewById(R.id.clear_department);
		initDepartMents();
		// 输入款监听事件
		searchEditText.addTextChangedListener(textChangeListener);
		//选择部门事件
		deptView.setOnClickListener(deptClickListener);
		//查询所有部门
		deptButton.setOnClickListener(searchAllListener);
		//长时间单击通讯录事件
		displayListView.setOnItemLongClickListener(contactsLongClickListener);
		//展示数据
		displayDatas(dept_name);
	}
	
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 说明：点击部门查询事件
	 */
	public View.OnClickListener deptClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			showDialog(USER_OPERATE_SELECT_DEPT);
		}
	};
	
	/**
	 *  @author liweijun 2012-10-08 编写 <br>
	 *  查询全部监听事件
	 */
	private View.OnClickListener searchAllListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			searchAll();
		}
	};
	
	/**
	 *  @author liweijun 2012-10-08 编写 <br>
	 *   查询全部
	 */
	private void searchAll(){
		displayDatas(null);
		dept_name = null;
		deptView.setText(defautTitle);
		deptButton.setVisibility(View.GONE);
	}
	
	/**
	 *  @author liweijun 2012-10-08 编写 <br>
	 *   获取部门信息
	 */
	private void initDepartMents(){
		try {
			db = new DBManager(this);
			List<String> deptList = db.findDeptList();
			db.closeDB();
			depts = new String[(deptList.size())];
			for (int i = 0; i < deptList.size(); i++) {
				depts[i] = deptList.get(i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 *  @author liweijun 2012-10-08 编写 <br>
	 *   长按通讯录事件
	 */
	private OnItemLongClickListener contactsLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long arg3) {
		    selectID =position;
		    showDialog(USER_OPERATE_LONG_CLICK_ITEM); //弹出菜单
			return false;
		}
	};
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 输入监听事件
	 */
	private TextWatcher textChangeListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			Log.i("获取内容", s.toString());
			displayDatas(dept_name);
	    }

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 展示数据
	 */
	private void displayDatas(String dept) {
		DBManager db = new DBManager(this);
		contactsList = db.findContactsList(searchEditText.getText().toString(), dept);
		db.closeDB();
		ContactssListAdapter contactsAdapter = new ContactssListAdapter(
				contactsList.size(), this, contactsList);
		displayListView.setAdapter(contactsAdapter);
		//更新状态
	    updateSearchHint();
		contactsAdapter.notifyDataSetChanged();
		displayListView.invalidate();	
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
		ContactsInfo con = contactsList.get(selectID);
		if (con != null) {
			Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+con.getPhonenum()));
			startActivity(intent);
		}
	}
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 发送短信
	 */
	private void sendMessages(){
		ContactsInfo con = contactsList.get(selectID);
		if (con != null) {
			Uri smsToUri = Uri.parse("smsto:"+con.getPhonenum());
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
		ContactsInfo con = contactsList.get(selectID);
		if (con != null) {
			ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(con.getPhonenum());
			Toast.makeText(this, "已复制到粘贴板!", 1000).show();
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 更新查询结果数目
	 */
	private void updateSearchHint(){
		Resources res = getResources();
		String text = String.format(res.getString(R.string.contact_search_hint_text), contactsList.size());		
		countTextView.setHint(text);
	}
	
	/**
	 * 
	 * @author liweijun 2012-10-08 编写 <br>
	 *  内部类，用于组装展示数据
	 */
	private class ContactssListAdapter extends BaseAdapter {
		private Context context;
		private int count = 0;
		private List<ContactsInfo> contactslist;

		public ContactssListAdapter(int count, Context context,
				List<ContactsInfo> contactslist) {
			this.count = count;
			this.context = context;
			this.contactslist = contactslist;
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
			if (!contactslist.isEmpty()) {
				LayoutInflater inflater = LayoutInflater.from(context);
				LinearLayout layout = (LinearLayout) inflater.inflate(
						R.layout.contacts_list_item, null);
				ContactsInfo contacts = contactslist.get(position);
				TextView textview_name = (TextView) layout.findViewById(R.id.xyoa_contacts_name_TextView);
				TextView textview_detail = (TextView) layout.findViewById(R.id.xyoa_contacts_detail_TextView);
				textview_name.setText(contacts.getName()+"["+contacts.getDepartment()+"]");
				textview_detail.setText("手机:"+contacts.getPhonenum() + " 电话:"
						+("".equals(contacts.getOfficenum())?"---":contacts.getOfficenum()) );
				return layout;
			}
			return null;
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 * 系统对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case USER_OPERATE_SELECT_DEPT: //按照部门查询弹出部门选择框
			return new Builder(ContactsActivity.this)
			.setTitle(getString(R.string.contacts_department)).setSingleChoiceItems(depts, -1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dept_index = which;
				}
			}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					dept_name = depts[dept_index];
					displayDatas(dept_name);
					deptView.setText(dept_name);
					checkClearButton();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create();
		case USER_OPERATE_LONG_CLICK_ITEM: //长按弹出菜单
			return new AlertDialog.Builder(ContactsActivity.this)
            .setTitle(R.string.quick_operate)
            .setItems(R.array.quick_operate_items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	showOperates(which);
                }
            }).create();
		}
		return null;
	}
	
	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 *  部门重置图标开关
	 */
	private void checkClearButton(){
		if (!defautTitle.equals(deptView.getText())) {
			deptButton.setVisibility(View.VISIBLE);
		} else {
			deptButton.setVisibility(View.GONE);
		}
	}
}
