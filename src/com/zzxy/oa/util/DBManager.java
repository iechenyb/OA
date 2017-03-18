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
	private final static String DATABASE_NAME = "zzxy_oa.db"; // ���ݿ���
	// ͨѶ¼��
	private final static String TABLE_ZZXY_CONTACTS = "CREATE TABLE IF NOT EXISTS zzxy_contacts ("
			+ " contacts_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ " userid VARCHAR2(36),"
			+ " name VARCHAR2(30),"
			+ " phonenum VARCHAR2(30), "
			+ " officenum NVARCHAR2(30), "
			+ " department VARCHAR2(50),"
			+ " py_name VARCHAR2(30), " + " py_szm_name VARCHAR(10) )";
	// ������Ϣ��
	private final static String TABLE_ZZXY_DEPARTMENTS = "CREATE TABLE IF NOT EXISTS zzxy_department( "
			+ " dept_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ " dept_name VARCHAR2(30), "
			+ " dept_py_name VARCHAR2(30), "
			+ " dept_py_szm_name VARCHAR2(30))";
	private static final int DATABASE_VERSION = 1; // ���ݿ�汾
	private SQLiteDatabase db; // ���ݿ����

	public DBManager(Context context) {
		dbHelp = new DBHelper(context);
		/**
		 * ��ΪgetWritableDatabase�ڲ������� mContext.openOrCreateDatabase(mName, 0,
		 * mFactory); ����Ҫȷ��context�ѳ�ʼ��,���ǿ��԰�ʵ����DBManager �Ĳ������Activity��onCreate��
		 */
		db = dbHelp.getWritableDatabase();
	}

	/**
	 * ���ͨѶ¼
	 * 
	 * @author liweijun 2012-09-30��д<br>
	 * @param contactsList
	 */
	public void addContacts(List<ContactsInfo> contactsList) {
		try {
			db.beginTransaction(); // ��ʼ����
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
	 * @author liweijun 2012-10-08��д<br>
	 *         ˵�������²�����Ϣ
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
			Log.i("���²�����Ϣ", "���²�����Ϣ�쳣��");
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * ��ȡͨѶ¼��Ϣ
	 * 
	 * @author liweijun 2012-09-30 ��д
	 * @param param
	 *            ƴ��������ĸ
	 * @param dept
	 *            ����
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
	 * @author liweijun 2012-10-08 ��д <br>
	 *         ˵������ѯͨѶ¼
	 * @param param
	 *            ƴ��������ĸ
	 * @param dept
	 *            ����
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
	 * @author liweijun 2012-10-08 ��д <br>
	 *         ˵�� ��ȡ������Ϣ
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
	 * @author liweijun ��ѯ������Ϣ
	 * @return
	 */
	private Cursor queryDeptsCursor() {
		String sql = "select dept_name from zzxy_department where 1=1 ";
		Cursor c = db.rawQuery(sql, null);
		return c;
	}

	/**
	 * @author liweijun ����ID��ѯ
	 * @param id
	 *            ͨѶ¼ID
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
	 * @author liweijun ����ID��ѯ
	 * @param id
	 *            ͨѶ¼ID
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
	 * ������ݿ�
	 */
	public void deleteAll() {
		db.execSQL("delete from zzxy_contacts");
		db.execSQL("delete from zzxy_department");
	}

	/**
	 * @author liweijun �ر����ݿ�
	 */
	public void closeDB() {
		db.close();
	}

	/**
	 * 2012-09-25 ��д <br>
	 * ˵����ά���͹������ݿ�Ļ���
	 * 
	 * @author liweijun
	 * 
	 */
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * �������ݿ��
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_ZZXY_CONTACTS);
			db.execSQL(TABLE_ZZXY_DEPARTMENTS);
		}

		/**
		 * ���ݿ���Ҫ����ʱ����,<br>
		 * һ���������������ݿ���������ݿ������ת�Ƶ������ݿ����
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("update database", "�������ݿ�");
		}

	}
}
