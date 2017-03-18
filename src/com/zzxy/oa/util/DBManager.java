package com.zzxy.oa.util;

import java.util.ArrayList;
import java.util.List;

import com.zzxy.oa.vo.ContactsInfo;
import com.zzxy.oa.vo.DepartMent;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager {

	private DBHelper dbHelp;
	private final static String DATABASE_NAME = "zzxy_oa.db"; // 数据库名
	// 通讯录表
	private final static String TABLE_ZZXY_CONTACTS = "CREATE TABLE IF NOT EXISTS zzxy_contacts ("
			+ " contacts_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ " userid VARCHAR2(36),"
			+ " name VARCHAR2(30),"
			+ " phonenum VARCHAR2(30), "
			+ " officenum NVARCHAR2(30), "
			+ " department VARCHAR2(50),"
			+ " py_name VARCHAR2(30), " + " py_szm_name VARCHAR(10) )";
	// 部门信息表
	private final static String TABLE_ZZXY_DEPARTMENTS = "CREATE TABLE IF NOT EXISTS zzxy_department( "
			+ " dept_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ " dept_name VARCHAR2(30), "
			+ " dept_py_name VARCHAR2(30), "
			+ " dept_py_szm_name VARCHAR2(30))";
	private static final int DATABASE_VERSION = 1; // 数据库版本
	private SQLiteDatabase db; // 数据库对象

	public DBManager(Context context) {
		dbHelp = new DBHelper(context);
		/**
		 * 因为getWritableDatabase内部调用了 mContext.openOrCreateDatabase(mName, 0,
		 * mFactory); 所以要确保context已初始化,我们可以把实例化DBManager 的步骤放在Activity的onCreate里
		 */
		db = dbHelp.getWritableDatabase();
	}

	/**
	 * 添加通讯录
	 * 
	 * @author liweijun 2012-09-30编写<br>
	 * @param contactsList
	 */
	public void addContacts(List<ContactsInfo> contactsList) {
		try {
			db.beginTransaction(); // 开始事务
			String sql = "insert into zzxy_contacts values(null, ?, ?, ?, ?, ?, ?) ";
			for (ContactsInfo con : contactsList) {
				db.execSQL(
						sql,
						new Object[] { con.getName(), con.getPhonenum(),
								con.getOfficenum(), con.getDepartment(),
								con.getPy_name(), con.getPy_szm_name() });
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * @author liweijun 2012-10-08编写<br>
	 *         说明：更新部门信息
	 * @param deptLists
	 */
	public void addDepts(List<DepartMent> deptLists) {
		try {
			db.beginTransaction();
			String sql = "insert into zzxy_department values(null, ?, ?, ?) ";
			for (DepartMent dept : deptLists) {
				db.execSQL(sql,
						new Object[] { dept.getName(), dept.getPinyin_name(),
								dept.getPinyin_szm_name() });
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.i("更新部门信息", "更新部门信息异常！");
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 获取通讯录信息
	 * 
	 * @author liweijun 2012-09-30 编写
	 * @param param
	 *            拼音或首字母
	 * @param dept
	 *            部门
	 * @return
	 */
	public List<ContactsInfo> findContactsList(String param, String dept) {
		List<ContactsInfo> contactsList = new ArrayList<ContactsInfo>();
		Cursor c = queryContactsCursor(param, dept);
		while (c.moveToNext()) {
			ContactsInfo con = new ContactsInfo(c.getInt(c
					.getColumnIndex("contacts_id")), c.getString(c
					.getColumnIndex("userid")), c.getString(c
					.getColumnIndex("name")), c.getString(c
					.getColumnIndex("phonenum")), c.getString(c
					.getColumnIndex("officenum")), c.getString(c
					.getColumnIndex("department")), c.getString(c
					.getColumnIndex("py_name")), c.getString(c
					.getColumnIndex("py_szm_name")));
			contactsList.add(con);
		}
		c.close();
		return contactsList;
	}

	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 *         说明：查询通讯录
	 * @param param
	 *            拼音或首字母
	 * @param dept
	 *            部门
	 * @return
	 */
	private Cursor queryContactsCursor(String param, String dept) {
		String sql = "select contacts_id, name, phonenum, officenum,department, py_name, py_szm_name from zzxy_contacts where 1=1 ";
		StringBuffer sqlBuffer = new StringBuffer(sql);
		if (param != null && !"".equals(param)) {
			sqlBuffer.append("and  (py_name like '%" + param
					+ "%' or py_szm_name like '%" + param + "%') ");
		}
		if (dept != null && !"".equals(dept)) {
			sqlBuffer.append("and department like '%" + dept + "%' ");
		}
		Cursor c = db.rawQuery(sqlBuffer.toString(), null);
		return c;
	}

	/**
	 * @author liweijun 2012-10-08 编写 <br>
	 *         说明 获取部门信息
	 * @return
	 */
	public List<String> findDeptList() {
		List<String> deptList = new ArrayList<String>();
		Cursor c = queryDeptsCursor();
		while (c.moveToNext()) {
			deptList.add(c.getString(c.getColumnIndex("dept_name")));
		}
		c.close();
		return deptList;
	}

	/**
	 * @author liweijun 查询部门信息
	 * @return
	 */
	private Cursor queryDeptsCursor() {
		String sql = "select dept_name from zzxy_department where 1=1 ";
		Cursor c = db.rawQuery(sql, null);
		return c;
	}

	/**
	 * @author liweijun 根据ID查询
	 * @param id
	 *            通讯录ID
	 * @return
	 */
	public ContactsInfo findContactsByID(int id) {
		ContactsInfo con = null;
		Cursor c = queryCursorByID(id);
		if (c != null) {
			if (c.moveToFirst()) {
				con = new ContactsInfo(
						c.getInt(c.getColumnIndex("contacts_id")),
						c.getString(c.getColumnIndex("userid")), c.getString(c
								.getColumnIndex("name")), c.getString(c
								.getColumnIndex("phonenum")), c.getString(c
								.getColumnIndex("officenum")), c.getString(c
								.getColumnIndex("department")), null, null);
			}
		}
		c.close();
		return con;
	}

	/**
	 * @author liweijun 根据ID查询
	 * @param id
	 *            通讯录ID
	 * @return
	 */
	private Cursor queryCursorByID(int id) {
		Cursor mCursor = db
				.rawQuery(
						"select contacts_id, name, phonenum, officenum, department from zzxy_contacts  where contacts_id = "
								+ id, null);
		return mCursor;
	}

	/**
	 * 清空数据库
	 */
	public void deleteAll() {
		db.execSQL("delete from zzxy_contacts");
		db.execSQL("delete from zzxy_department");
	}

	/**
	 * @author liweijun 关闭数据库
	 */
	public void closeDB() {
		db.close();
	}

	/**
	 * 2012-09-25 编写 <br>
	 * 说明：维护和管理数据库的基类
	 * 
	 * @author liweijun
	 * 
	 */
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * 创建数据库表
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_ZZXY_CONTACTS);
			db.execSQL(TABLE_ZZXY_DEPARTMENTS);
		}

		/**
		 * 数据库需要升级时调用,<br>
		 * 一般用来升级旧数据库表并将旧数据库表数据转移到新数据库表中
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("update database", "更新数据库");
		}

	}
}
