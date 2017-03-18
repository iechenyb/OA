package com.zzxy.oa.acitivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzxy.oa.util.ConstantsUtil;
import com.zzxy.oa.util.HttpConnectUtil;
import com.zzxy.oa.vo.KaoQin;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

/**
 * ���ܿ���
 * @author liweijun ��ΰ�� 2012-09-25 ��д
 */
public class BenzhouActivity extends Activity {

	private TableLayout tableLayout; //��񲼾�
	private TextView weekWiew; //����
	private TextView kaoqinView; //��������
	private List<KaoQin> kaoqinList; //��������List
	private final int NET_ERROR = 0; //�������Ӵ���
	private final int DATA_EMPTY = 1; //���ݷ���Ϊ��
	private final int DATA_SUCCESS = 2; //���سɹ�
	private  Map<String, String> weekMap; //����
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoqin);
		weekWiew = (TextView)findViewById(R.id.kaoqin_week_view);
		kaoqinView = (TextView)findViewById(R.id.kaoqin_kaoqin_view);
		weekMap = new HashMap<String, String>();
	    weekMap.put("1", "��һ");
	    weekMap.put("2", "�ܶ�");
	    weekMap.put("3", "����");
	    weekMap.put("4", "����");
	    weekMap.put("5", "����");
	    weekMap.put("6", "����");
	    weekMap.put("7", "����");
		tableLayout = (TableLayout)findViewById(R.id.kaoqin_table_layout);
		//ִ�в�ѯ
		showDialog(0); 
		kaoqinThread thread = new kaoqinThread();
		thread.start();
	}
	/**
	 * �ڲ����߳���
	 * @author liweijun ��ΰ�� 2012-09-25 ��д
	 */
	private  class  kaoqinThread extends Thread {
		@Override
		public void run() {
			showDialog(0);
			kaoqinList = HttpConnectUtil.findKaoQinList(getUserName(),"curweek");
			if (kaoqinList == null) {
				hander.sendEmptyMessage(NET_ERROR);
			} else {
				if (kaoqinList.isEmpty()) {
					hander.sendEmptyMessage(DATA_EMPTY);
				} else {
					hander.sendEmptyMessage(DATA_SUCCESS);
				}
			}
		}
	}
	
	/**
	 * ���ݴ����߳�
	 * @author liweijun ��ΰ�� 2012-09-25 ��д
	 */
	private Handler hander = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NET_ERROR:
				weekWiew.setVisibility(View.GONE);
				kaoqinView.setVisibility(View.GONE);
				dismissDialog(0);
				Toast.makeText(BenzhouActivity.this, "�������ӳ�ʱ", 1000).show();
				break;
			case DATA_EMPTY:
				dismissDialog(0);
				weekWiew.setVisibility(View.GONE);
				kaoqinView.setVisibility(View.GONE);
				Toast.makeText(BenzhouActivity.this, "������û�з������ݣ������ԣ�", 1000).show();
				break;
			case DATA_SUCCESS:
				weekWiew.setVisibility(View.VISIBLE);
				kaoqinView.setVisibility(View.VISIBLE);
				displayDatas(kaoqinList);
				dismissDialog(0);
				break;
			}
		}
	};
	
	/**
	 * չʾ����
	 * @author liweijun ��ΰ�� 2012-09-25 ��д
	 */
	private void displayDatas(List<KaoQin> kaoqinList){
		if (!kaoqinList.isEmpty()) {
			for (KaoQin kaoqin:kaoqinList) {
				TableRow tr = new TableRow(this);
				tr.setPadding(6, 12, 12, 6);
				tr.setGravity(Gravity.LEFT);
				tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				TextView mTvWeek = new TextView(this);
				TextView mTvKaoqin = new TextView(this);
				mTvWeek.setTextSize(18);
				String weekdate = kaoqin.getWeekdate();
				mTvWeek.setText(weekMap.get(kaoqin.getId())+"("+kaoqin.getWeekdate()+")");
				mTvWeek.setGravity(Gravity.CENTER);
				mTvKaoqin.setText(kaoqin.getContent());
				mTvKaoqin.setTextSize(18);
				if (weekdate != null && !"".equals(weekdate)) {
					if (weekdate.equals(getCurDate())) {
						mTvWeek.setTextColor(Color.RED);
						mTvKaoqin.setTextColor(Color.RED);
					}
				}
				mTvKaoqin.setGravity(Gravity.CENTER);
				mTvWeek.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				
				mTvKaoqin.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				tr.addView(mTvWeek);
				tr.addView(mTvKaoqin);
				tableLayout.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setMessage("���ݼ�����...");
		dialog.setCancelable(true);
		return dialog;
	}
	
	/**
	 * ��ȡ��ǰ����
	 * @author liweijun ��ΰ�� 2012-09-25 ��д
	 */
	private String getCurDate(){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(new Date());
	}
	
	/**
	 * @author liweijun ��ΰ�� 2012-09-25 ��д
	 *  ��ȡ�û�����
	 * @return
	 */
	private String getUserName(){
		SharedPreferences sp = getSharedPreferences(ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getString(ConstantsUtil.getUserinfoUsername(), "");
	}
}
