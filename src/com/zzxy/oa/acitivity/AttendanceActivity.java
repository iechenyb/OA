package com.zzxy.oa.acitivity;



import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
/**
 * ¿¼ÇÚ²éÑ¯
 * @author liweijun
 *
 */
public class AttendanceActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost
				.newTabSpec("benzhou_tab")
				.setIndicator(getString(R.string.kaoqin_benzhou),
						getResources().getDrawable(R.drawable.kaoqin_benzhou))
				.setContent(new Intent(this, BenzhouActivity.class)));

		tabHost.addTab(tabHost
				.newTabSpec("shangzhou_tab")
				.setIndicator(getString(R.string.kaoqin_shangzhou),
						getResources().getDrawable(R.drawable.kaoqin_shangzhou))
				.setContent(new Intent(this, ShangzhouActivity.class)));
	}
}
