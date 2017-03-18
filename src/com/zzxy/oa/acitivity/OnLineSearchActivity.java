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
 * ���߲�ѯ
 * @author liweijun 2012-10-09 ��д
 */
public class OnLineSearchActivity extends Activity {

	private ListView displayListView;  //����չʾView
	private ProgressBar pb; //������
	private TextView loadingTv; //������ʾ
	private TextView deptView; //���Ų�ѯView
	private final int CALL_PHONENUM = 0; // ����绰
	private final int SEND_MESSAGE = 1; // ���Ͷ���
	private final int COPY_PHONENUM = 2; // �������
	private final int USER_OPERATE_LONG_CLICK_ITEM = 3; //�����˵�
	private String department = null; //��������
	private String name = null; //����
	private Spinner sp; //����ѡ����
	private EditText username_view; //����
	private DBManager db; //���ݿ����
	private String[] depts = null; //��������
	private int dept_index = 0; //��������
	private final String default_title = "���в���"; //Ĭ����ʾ����
	private List<OnLineSearch> osList = null; //��ѯ���ݼ���
	private final int SUCCESS_SEARCH = 0; //���سɹ�
	private final int FAILURE_SEARCH = 1; //����ʧ��
	private final int ERROR_SEARCH = 2; //�����쳣
	private int selectID = 0; //ͨѶ¼����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_search);
		//��������View
		deptView = (TextView) findViewById(R.id.contacts_search_button_online);
		//����չʾView
		displayListView = (ListView) findViewById(R.id.contactslistViewOnline);
		//������ 
		pb = (ProgressBar)findViewById(R.id.onlinedatasloadingBar);
		//��������ʾ��Ϣ
		loadingTv = (TextView)findViewById(R.id.onlinedatasloadingText);
		//��ʱ�䵥��ͨѶ¼�¼�
		displayListView.setOnItemLongClickListener(contactsLongClickListener);
		//���ݼ�������¼�
		deptView.setOnClickListener(searchClickListener);
		//��ʼ����������
		initDepartMents();
		//����������ѯ��
		showSearchDialog();
	}
	
	/**
	 * @author liweijun 2012-10-09 ��д
	 * ����ͨѶ¼�¼�
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
	 * @author liweijun 2012-10-09 ��д
	 * ���ݼ�������¼�
	 */
	private View.OnClickListener searchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showSearchDialog();
		}
	};
	
	/**
	 * @author liweijun 2012-10-09 ��д
	 * չʾ����
	 */
	private void displayDatas() {
		SearchThread thread = new SearchThread();
		thread.start();
		displayListView.setVisibility(View.GONE);
		pb.setVisibility(View.VISIBLE);
		loadingTv.setVisibility(View.VISIBLE);
	}
	/**
	 * @author liweijun 2012-10-09 ��д
	 * ���ؽ�����
	 */
	private void hiddenProgressBar(){
		pb.setVisibility(View.GONE);
		loadingTv.setVisibility(View.GONE);
	}
	/**
	 * @author liweijun 2012-10-09 ��д
	 * ��װListView
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
     * @author liweijun 2012-10-09 ��д
     * ���ݼ����߳�
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
	 * @author liweijun 2012-10-09 ��д
	 * ���̴���
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ERROR_SEARCH:
				hiddenProgressBar();
				Toast.makeText(OnLineSearchActivity.this, "��������ʧ��!", 1000).show();
				break;
			case SUCCESS_SEARCH:
				hiddenProgressBar();
				diplayView();
				break;
			case FAILURE_SEARCH:
				hiddenProgressBar();
				Toast.makeText(OnLineSearchActivity.this, "������û�����ݷ���!", 1000).show();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * @author liweijun 2012-10-09 ��д
	 * ��ʼ��������Ϣ
	 */
	private void initDepartMents(){
		try {
			db = new DBManager(this);
			List<String> deptList = db.findDeptList();
			db.closeDB();
			Log.i("��ѯ������Ϣ", deptList.toString());
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
	 * @author liweijun 2012-10-09 ��д
	 *  ������ѯѡ���
	 */
	private void showSearchDialog() {
		ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, depts);   
		deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.alert_dialog_search_entry, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				OnLineSearchActivity.this);
		builder.setTitle("���ݼ���");
		builder.setView(view);
		username_view = (EditText) view.findViewById(R.id.online_username_edit);
		sp = (Spinner) view.findViewById(R.id.online_department_search_spinner);
		sp.setAdapter(deptAdapter);
		sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				dept_index = arg2;
				 Log.i("����ѡ��", depts[dept_index]);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			} 
			
		});
		builder.setPositiveButton("��ѯ", new DialogInterface.OnClickListener() {
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
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
	
	/**
	 * @author liweijun 2012-10-09 ��д
	 *  �ڲ���
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
						tempContent += "����:" + os.getContent();
					}
				}
				textview_detail.setText("�ֻ�:" + os.getPhoneNum() + " "
						+ tempContent);
				return layout;
			}
			return null;
		}
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
		OnLineSearch os = osList.get(selectID);
		if (os != null) {
			Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+os.getPhoneNum()));
			startActivity(intent);
		}
	}
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 * ���Ͷ���
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
	 * @author liweijun 2012-10-08 ��д <br>
	 * ���Ƶ�ճ����
	 */
	private void copyPhoneNum(){
		OnLineSearch os = osList.get(selectID);
		if (os != null) {
			ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(os.getPhoneNum());
			Toast.makeText(this, "�Ѹ��Ƶ�ճ����!", 1000).show();
		}
	}
	
	/**
	 * @author liweijun 2012-10-08 ��д <br>
	 *ϵͳ�Ի���
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
