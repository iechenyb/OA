package com.zzxy.oa.acitivity;




import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {
	private TextView title_view; // ��ʾ��ǰ��¼�û�������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		title_view = (TextView)findViewById(R.id.tv_user_name);
		title_view.setText("ϵͳʹ��˵��");
	}
}
