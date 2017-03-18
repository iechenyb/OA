package com.zzxy.oa.acitivity;


import java.util.HashMap;
import java.util.Map;

import com.zzxy.oa.util.ConstantsUtil;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/**
 *  �ֶ�����
 * @author liweijun 2012-09-28 ��д <br>
 */
public class SendOrderActivity extends Activity {

	private Button sendButton, cancelButton;  //����ȡ����ť
	private Spinner qjtypeSpinnner, startTimeSpinner;  //������ͣ�ʱ�䣬��ʼʱ��
	private EditText qjtimeEditText; //���ʱ��
	private ArrayAdapter<CharSequence> qjtypeAdapter; //������͵�����
	private ArrayAdapter<CharSequence> startTimeAdapter; //���ʱ�������
	private int qjtype; //�������
	private int starttime; //��ʼʱ��
	private String qjtimes; //���ʱ��
	private String RECEIVE_PHONE_NUM; //���պ���
	private TextView UserNameTv; //��������ʾ
	
	private static Map<Integer, String> qjTypeMap = new HashMap<Integer,String>();
	private static Map<Integer, String> startTimeMap = new HashMap<Integer,String>();
	static{
		qjTypeMap.put(0, "qj");
		qjTypeMap.put(1, "cc");
		qjTypeMap.put(2, "tx");
		qjTypeMap.put(3, "wc");
		qjTypeMap.put(4, "bs");
		
		startTimeMap.put(0, "0"); 
		startTimeMap.put(1, "1");
		startTimeMap.put(2, "2");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addorder);
		UserNameTv = (TextView) findViewById(R.id.tv_user_name);
		UserNameTv.setText("�ֶ�����ָ��");
		SharedPreferences sp = getSharedPreferences(ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		RECEIVE_PHONE_NUM = sp.getString(ConstantsUtil.getSystemOrderPhonenum(), "13673669867");
		qjtypeSpinnner = (Spinner)findViewById(R.id.qjspinner);
		startTimeSpinner = (Spinner)findViewById(R.id.startspinner);
		qjtimeEditText = (EditText)findViewById(R.id.EditTextQjTime);
		sendButton = (Button)findViewById(R.id.sendButton);
		cancelButton = (Button)findViewById(R.id.cancelButton);
		//����ѡ������ArrayAdapter�������� 
		qjtypeAdapter = ArrayAdapter.createFromResource(this, R.array.qjtypeItem, android.R.layout.simple_spinner_item);
		//���������б�ķ��  
		qjtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//��adapter ��ӵ�spinner��  
		qjtypeSpinnner.setAdapter(qjtypeAdapter);
		qjtypeSpinnner.setVisibility(View.VISIBLE);
		
		startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.startTimeItem, android.R.layout.simple_spinner_item);
		startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startTimeSpinner.setAdapter(startTimeAdapter);
		startTimeSpinner.setVisibility(View.VISIBLE);
		//���ʱ�������¼�
		qjtimeEditText.addTextChangedListener(new TextWatcher() {
			@SuppressWarnings("deprecation")
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if ("0.5".equals(qjtimeEditText.getText().toString())) {
					showDialog(0);
				} else {
					qjtimes = qjtimeEditText.getText().toString();
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
		//�������ѡ���¼�
		qjtypeSpinnner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				qjtype = arg2;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			} 
			
		});
		//��ʼʱ��ѡ���¼�
		startTimeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				starttime = arg2;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			} 
			
		});
		//�����¼�
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String qjtimeStr = qjtimeEditText.getText().toString();
				if (qjtimeEditText.getText() == null || "".equals(qjtimeStr)) {
					qjtimeEditText.setError("���ʱ�䲻��Ϊ��Ŷ��");
				    return;
				} else {
					if (qjtimeStr.contains(".")) {
						String checkstr = qjtimeStr.substring(qjtimeStr.lastIndexOf(".")+1,qjtimeStr.length());
						if (checkstr.length()>1 || !qjtimeEditText.getText().toString().contains("5")) {
							qjtimeEditText.setError("������0.5��ʾ��");
						    return;
						}
					} else {
						int intqjtime = Integer.parseInt(qjtimeStr);
						if (intqjtime>30) {
							qjtimeEditText.setError("���ʱ�������");
						    return;
						}
					}
				}
				final AlertDialog.Builder builder = new Builder(SendOrderActivity.this);
				String message = "������ͣ�"+(String) qjtypeAdapter.getItem(qjtype)+
						                    "\n��ʼʱ�䣺"+(String)startTimeAdapter.getItem(starttime)+
						                    "\n���ʱ�䣺"+qjtimeEditText.getText().toString()+"��\n"+
						                    "ȷ��Ҫ���Ͷ�����?";
				builder.setTitle("����ȷ��").setMessage(message).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String msg = qjTypeMap.get(qjtype)+startTimeMap.get(starttime)+","+qjtimes;
						sendMessage(msg);
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						builder.create().dismiss();
					}
				}).create().show();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
    	// ������Ϣ
		private void sendMessage(String messages) {
			Log.i("messages-------->", messages);
			if (messages != null && !"".equals(messages)) {
				Log.i("���ٷ����Ž����ֻ���Ϊ��", RECEIVE_PHONE_NUM);
				Log.i("���ٷ����Ż�ȡ�Ķ�������Ϊ:", messages);
				Uri smsToUri = Uri.parse("smsto:"+RECEIVE_PHONE_NUM);
				Intent intent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra("sms_body", messages);
				startActivity( intent);
			} 
		}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		Builder builder=new android.app.AlertDialog.Builder(this);
		builder.setTitle("����or���磿").setSingleChoiceItems(R.array.sjtimeradio, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				qjtimes = getResources().getStringArray(R.array.sjtimeradio)[which].substring(0, 1);
			}
		}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
		return dialog;
	}
	
}
