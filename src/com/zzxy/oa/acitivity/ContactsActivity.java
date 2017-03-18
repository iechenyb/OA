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
 * @author liweijun 2012-09-29 ��д
 *  ����ͨѶ¼
 */
public class ContactsActivity extends Activity {

	private EditText searchEditText; //������
	private List<ContactsInfo> contactsList; //ͨѶ¼���ݼ�
	private ListView displayListView; //����չʾView
	private TextView countTextView; //��ѯ�����ĿView
	private TextView deptView; //�����Ų�ѯView
	private ImageView deptButton; //���ò��Ű�ť
	private int dept_index; //��������
	private String dept_name = null; //��������
	private final String defautTitle = "���в���"; //Ĭ����ʾ
	private String[] depts = null; //��������
	private DBManager db; //���ݿ����
	private int selectID = 0; //ͨѶ¼����
	private final int CALL_PHONENUM = 0; //����绰
	private final int SEND_MESSAGE = 1; //���Ͷ���
	private final int COPY_PHONENUM = 2; //�������
	private final int USER_OPERATE_SELECT_DEPT = 1; //��������ѡ���
	private final int USER_OPERATE_LONG_CLICK_ITEM = 2; //ͨѶ¼�����¼�

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
		// ���������¼�
		searchEditText.addTextChangedListener(textChangeListener);
		//ѡ�����¼�
		deptView.setOnClickListener(deptClickListener);
		//��ѯ���в���
		deptButton.setOnClickListener(searchAllListener);
		//��ʱ�䵥��ͨѶ¼�¼�
		displayListView.setOnItemLongClickListener(contactsLongClickListener);
		//չʾ����
		displayDatas(dept_name);
	}
	
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ˵����������Ų�ѯ�¼�
	 */
	public View.OnClickListener deptClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			showDialog(USER_OPERATE_SELECT_DEPT);
		}
	};
	
	/**
	 *  @author liweijun 2012-10-08 ��д <br>
	 *  ��ѯȫ�������¼�
	 */
	private View.OnClickListener searchAllListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			searchAll();
		}
	};
	
	/**
	 *  @author liweijun 2012-10-08 ��д <br>
	 *   ��ѯȫ��
	 */
	private void searchAll(){
		displayDatas(null);
		dept_name = null;
		deptView.setText(defautTitle);
		deptButton.setVisibility(View.GONE);
	}
	
	/**
	 *  @author liweijun 2012-10-08 ��д <br>
	 *   ��ȡ������Ϣ
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
	 *  @author liweijun 2012-10-08 ��д <br>
	 *   ����ͨѶ¼�¼�
	 */
	private OnItemLongClickListener contactsLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long arg3) {
		    selectID =position;
		    showDialog(USER_OPERATE_LONG_CLICK_ITEM); //�����˵�
			return false;
		}
	};
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ��������¼�
	 */
	private TextWatcher textChangeListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			Log.i("��ȡ����", s.toString());
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
	 * @author liweijun 2012-10-08 ��д <br>
	 * չʾ����
	 */
	private void displayDatas(String dept) {
		DBManager db = new DBManager(this);
		contactsList = db.findContactsList(searchEditText.getText().toString(), dept);
		db.closeDB();
		ContactssListAdapter contactsAdapter = new ContactssListAdapter(
				contactsList.size(), this, contactsList);
		displayListView.setAdapter(contactsAdapter);
		//����״̬
	    updateSearchHint();
		contactsAdapter.notifyDataSetChanged();
		displayListView.invalidate();	
	}
	
	private void showOperates(int operateID){
		switch (operateID) {
		//�������
		case CALL_PHONENUM:
			callPhoneNum();
			break;
		//���Ͷ���
		case SEND_MESSAGE:
		    sendMessages();
			break;
		//���ƺ���
		case COPY_PHONENUM:
			copyPhoneNum();
		break;
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ˵��������绰
	 */
	private void callPhoneNum(){
		ContactsInfo con = contactsList.get(selectID);
		if (con != null) {
			Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+con.getPhonenum()));
			startActivity(intent);
		}
	}
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ���Ͷ���
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
	 * @author liweijun 2012-10-08 ��д <br>
	 * ���Ƶ�ճ����
	 */
	private void copyPhoneNum(){
		ContactsInfo con = contactsList.get(selectID);
		if (con != null) {
			ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(con.getPhonenum());
			Toast.makeText(this, "�Ѹ��Ƶ�ճ����!", 1000).show();
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ���²�ѯ�����Ŀ
	 */
	private void updateSearchHint(){
		Resources res = getResources();
		String text = String.format(res.getString(R.string.contact_search_hint_text), contactsList.size());		
		countTextView.setHint(text);
	}
	
	/**
	 * 
	 * @author liweijun 2012-10-08 ��д <br>
	 *  �ڲ��࣬������װչʾ����
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
				textview_detail.setText("�ֻ�:"+contacts.getPhonenum() + " �绰:"
						+("".equals(contacts.getOfficenum())?"---":contacts.getOfficenum()) );
				return layout;
			}
			return null;
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ϵͳ�Ի���
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case USER_OPERATE_SELECT_DEPT: //���ղ��Ų�ѯ��������ѡ���
			return new Builder(ContactsActivity.this)
			.setTitle(getString(R.string.contacts_department)).setSingleChoiceItems(depts, -1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dept_index = which;
				}
			}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					dept_name = depts[dept_index];
					displayDatas(dept_name);
					deptView.setText(dept_name);
					checkClearButton();
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create();
		case USER_OPERATE_LONG_CLICK_ITEM: //���������˵�
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
	 * @author liweijun 2012-10-08 ��д <br>
	 *  ��������ͼ�꿪��
	 */
	private void checkClearButton(){
		if (!defautTitle.equals(deptView.getText())) {
			deptButton.setVisibility(View.VISIBLE);
		} else {
			deptButton.setVisibility(View.GONE);
		}
	}
}
