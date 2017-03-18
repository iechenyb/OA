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
 * 上周考勤
 * @author liweijun 李伟军 2012-09-25 编写
 */
public class BenzhouActivity extends Activity {

	private TableLayout tableLayout; //表格布局
	private TextView weekWiew; //日期
	private TextView kaoqinView; //考勤内容
	private List<KaoQin> kaoqinList; //考勤数据List
	private final int NET_ERROR = 0; //网络连接错误
	private final int DATA_EMPTY = 1; //数据返回为空
	private final int DATA_SUCCESS = 2; //加载成功
	private  Map<String, String> weekMap; //星期
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoqin);
		weekWiew = (TextView)findViewById(R.id.kaoqin_week_view);
		kaoqinView = (TextView)findViewById(R.id.kaoqin_kaoqin_view);
		weekMap = new HashMap<String, String>();
	    weekMap.put("1", "周一");
	    weekMap.put("2", "周二");
	    weekMap.put("3", "周三");
	    weekMap.put("4", "周四");
	    weekMap.put("5", "周五");
	    weekMap.put("6", "周六");
	    weekMap.put("7", "周日");
		tableLayout = (TableLayout)findViewById(R.id.kaoqin_table_layout);
		//执行查询
		showDialog(0); 
		kaoqinThread thread = new kaoqinThread();
		thread.start();
	}
	/**
	 * 内部多线程类
	 * @author liweijun 李伟军 2012-09-25 编写
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
	 * 数据处理线程
	 * @author liweijun 李伟军 2012-09-25 编写
	 */
	private Handler hander = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NET_ERROR:
				weekWiew.setVisibility(View.GONE);
				kaoqinView.setVisibility(View.GONE);
				dismissDialog(0);
				Toast.makeText(BenzhouActivity.this, "网络连接超时", 1000).show();
				break;
			case DATA_EMPTY:
				dismissDialog(0);
				weekWiew.setVisibility(View.GONE);
				kaoqinView.setVisibility(View.GONE);
				Toast.makeText(BenzhouActivity.this, "服务器没有返回数据，请重试！", 1000).show();
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
	 * 展示数据
	 * @author liweijun 李伟军 2012-09-25 编写
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
		dialog.setMessage("数据加载中...");
		dialog.setCancelable(true);
		return dialog;
	}
	
	/**
	 * 获取当前日期
	 * @author liweijun 李伟军 2012-09-25 编写
	 */
	private String getCurDate(){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(new Date());
	}
	
	/**
	 * @author liweijun 李伟军 2012-09-25 编写
	 *  获取用户姓名
	 * @return
	 */
	private String getUserName(){
		SharedPreferences sp = getSharedPreferences(ConstantsUtil.getZzxyOaSystemUserInfo(), 0);
		return sp.getString(ConstantsUtil.getUserinfoUsername(), "");
	}
}
