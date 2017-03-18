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
 *  手动发送
 * @author liweijun 2012-09-28 编写 <br>
 */
public class SendOrderActivity extends Activity {

	private Button sendButton, cancelButton;  //发送取消按钮
	private Spinner qjtypeSpinnner, startTimeSpinner;  //请假类型，时间，开始时间
	private EditText qjtimeEditText; //请假时间
	private ArrayAdapter<CharSequence> qjtypeAdapter; //请假类型迭代器
	private ArrayAdapter<CharSequence> startTimeAdapter; //请假时间迭代器
	private int qjtype; //请假类型
	private int starttime; //开始时间
	private String qjtimes; //请假时间
	private String RECEIVE_PHONE_NUM; //接收号码
	private TextView UserNameTv; //标题栏显示
	
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
		UserNameTv.setText("手动发送指令");
		SharedPreferences sp = getSharedPreferences(ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		RECEIVE_PHONE_NUM = sp.getString(ConstantsUtil.getSystemOrderPhonenum(), "13673669867");
		qjtypeSpinnner = (Spinner)findViewById(R.id.qjspinner);
		startTimeSpinner = (Spinner)findViewById(R.id.startspinner);
		qjtimeEditText = (EditText)findViewById(R.id.EditTextQjTime);
		sendButton = (Button)findViewById(R.id.sendButton);
		cancelButton = (Button)findViewById(R.id.cancelButton);
		//将可选内容与ArrayAdapter连接起来 
		qjtypeAdapter = ArrayAdapter.createFromResource(this, R.array.qjtypeItem, android.R.layout.simple_spinner_item);
		//设置下拉列表的风格  
		qjtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//将adapter 添加到spinner中  
		qjtypeSpinnner.setAdapter(qjtypeAdapter);
		qjtypeSpinnner.setVisibility(View.VISIBLE);
		
		startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.startTimeItem, android.R.layout.simple_spinner_item);
		startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startTimeSpinner.setAdapter(startTimeAdapter);
		startTimeSpinner.setVisibility(View.VISIBLE);
		//请假时间输入事件
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
		//请假类型选择事件
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
		//开始时间选择事件
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
		//发送事件
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String qjtimeStr = qjtimeEditText.getText().toString();
				if (qjtimeEditText.getText() == null || "".equals(qjtimeStr)) {
					qjtimeEditText.setError("请假时间不能为空哦！");
				    return;
				} else {
					if (qjtimeStr.contains(".")) {
						String checkstr = qjtimeStr.substring(qjtimeStr.lastIndexOf(".")+1,qjtimeStr.length());
						if (checkstr.length()>1 || !qjtimeEditText.getText().toString().contains("5")) {
							qjtimeEditText.setError("半天用0.5表示！");
						    return;
						}
					} else {
						int intqjtime = Integer.parseInt(qjtimeStr);
						if (intqjtime>30) {
							qjtimeEditText.setError("请假时间过长！");
						    return;
						}
					}
				}
				final AlertDialog.Builder builder = new Builder(SendOrderActivity.this);
				String message = "请假类型："+(String) qjtypeAdapter.getItem(qjtype)+
						                    "\n开始时间："+(String)startTimeAdapter.getItem(starttime)+
						                    "\n请假时间："+qjtimeEditText.getText().toString()+"天\n"+
						                    "确认要发送短信吗?";
				builder.setTitle("发送确认").setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String msg = qjTypeMap.get(qjtype)+startTimeMap.get(starttime)+","+qjtimes;
						sendMessage(msg);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
	
    	// 发送信息
		private void sendMessage(String messages) {
			Log.i("messages-------->", messages);
			if (messages != null && !"".equals(messages)) {
				Log.i("快速发短信接收手机号为：", RECEIVE_PHONE_NUM);
				Log.i("快速发短信获取的短信内容为:", messages);
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
		builder.setTitle("上午or下午？").setSingleChoiceItems(R.array.sjtimeradio, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				qjtimes = getResources().getStringArray(R.array.sjtimeradio)[which].substring(0, 1);
			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
		return dialog;
	}
	
}
