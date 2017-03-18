package com.zzxy.oa.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzxy.oa.vo.ContactsInfo;
import com.zzxy.oa.vo.DepartMent;
import com.zzxy.oa.vo.KaoQin;
import com.zzxy.oa.vo.OnLineSearch;
import com.zzxy.oa.vo.UserInfo;

import android.util.Log;

/**
 * 网络请求工具类
 * @author liweijun 2012-09-22 编写
 */
public class HttpConnectUtil {

	private static HttpPost request = null; //POST请求
	private static List<NameValuePair> params = null; //参数
	//private static String urlStr = "http://192.168.0.102:8080/xy_mobile_oa/UserServlet";
	// private static String urlStr =
	// "http://10.0.2.2:8080/xy_mobile_oa/UserServlet";
	// private static String urlStr =
	// private static String urlStr =
	//URL请求地址
	private static String urlStr = "http://office.xinyuan.com.cn:7003/xy_mobile_oa/UserServlet";
	private static final int REQUEST_TIMEOUT = 30 * 1000; // 设置请求超时
	private static final int SO_TIMEOUT = 30 * 1000; // 设置等待数据超时时间10秒钟

	/**
	 * @author liweijun 2012-09-22 编写 登陆验证
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return 结果 result
	 */
	public static UserInfo login(String username, String password) {
		UserInfo user = null;
		request = new HttpPost(urlStr);
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("flag", "login"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("userpass", password));
		HttpClient client = getHttpClient();
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse respose = client.execute(request);
			if (respose.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = respose.getEntity();
				if (entity != null) {
					String msg = EntityUtils.toString(entity);
					if (msg.contains("failure")) {
						user = new UserInfo(null, null,
								ConstantsUtil.getUserLoginFailure());
					} else {
						JSONObject result = new JSONObject(msg);
						user = new UserInfo(result.getString("username"),
								result.getString("name"),
								ConstantsUtil.getUserLoginSuccess());
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			user = new UserInfo(null, null,
					ConstantsUtil.getUserLoginNetError());
			Log.i("UnsupportedEncodingException", e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			user = new UserInfo(null, null,
					ConstantsUtil.getUserLoginNetError());
			Log.i("ClientProtocolException", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			user = new UserInfo(null, null,
					ConstantsUtil.getUserLoginNetError());
			Log.i("IOException", e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			user = new UserInfo(null, null,
					ConstantsUtil.getUserLoginNetError());
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return user;
	}
	/**
	 * @author liweijun 2012-09-25 编写
	 * @param username
	 * @param weekflag
	 * @return
	 */
	public static List<KaoQin> findKaoQinList(String username, String weekflag) {
		List<KaoQin> kaoqinList = null;
		request = new HttpPost(urlStr);
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("flag", "kaoqin"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("weekflag", weekflag));
		HttpClient client = getHttpClient();
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse respose = client.execute(request);
			if (respose.getStatusLine().getStatusCode() == 200) {
				kaoqinList = new ArrayList<KaoQin>();
				String msg = EntityUtils.toString(respose.getEntity());
				Log.i("JSON数据：", msg);
				// 生成 JSON 对象
				JSONObject result = new JSONObject(msg);
				JSONArray jsonArray = result.getJSONArray("data");
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = (JSONObject) jsonArray.opt(i);
						KaoQin kaoqin = new KaoQin(temp.getString("taskweek"),
								temp.getString("taskdate"),
								temp.getString("taskcontent"));
						kaoqinList.add(kaoqin);
					}
				} catch (Exception e) {
					Log.i("数据转换出错", e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return kaoqinList;
	}

	/**
	 * 加载通讯录
	 * @author liweijun 2012-09-29 编写
	 * @return
	 */
	public static List<ContactsInfo> getContactsInfoList() {
		List<ContactsInfo> contactsList = null;
		request = new HttpPost(urlStr);
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("flag", "loadContacts"));
		HttpClient client = getHttpClient();
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse respose = client.execute(request);
			if (respose.getStatusLine().getStatusCode() == 200) {
				contactsList = new ArrayList<ContactsInfo>();
				String msg = EntityUtils.toString(respose.getEntity());
				Log.i("JSON数据：", msg);
				JSONObject result = new JSONObject(msg);
				JSONArray jsonArray = result.getJSONArray("data");
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = (JSONObject) jsonArray.opt(i);
						ContactsInfo contacts = new ContactsInfo(0,
								temp.getString("USERID"),
								temp.getString("USERNAME"),
								temp.getString("PHONENUMBER"),
								temp.getString("OFFICEPHONE"),
								temp.getString("DEPARTMENT"),
								PinyinUtil.getPingYin(temp
										.getString("USERNAME")),
								PinyinUtil.getPinYinHeadChar(temp
										.getString("USERNAME")));
						contactsList.add(contacts);
					}
				} catch (Exception e) {
					Log.i("数据转换出错", e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return contactsList;
	}

	/**
	 * @author liweijun 2012-10-08 编写 获取部门信息
	 * @return
	 */
	public static List<DepartMent> getDepts() {
		List<DepartMent> deptLists = null;
		request = new HttpPost(urlStr);
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("flag", "loadDepts"));
		HttpClient client = getHttpClient();
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse respose = client.execute(request);
			if (respose.getStatusLine().getStatusCode() == 200) {
				deptLists = new ArrayList<DepartMent>();
				String msg = EntityUtils.toString(respose.getEntity());
				Log.i("JSON数据：", msg);
				JSONObject result = new JSONObject(msg);
				JSONArray jsonArray = result.getJSONArray("data");
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = (JSONObject) jsonArray.opt(i);
						DepartMent dept = new DepartMent(0,
								temp.getString("deptName"),
								PinyinUtil.getPingYin(temp
										.getString("deptName")),
								PinyinUtil.getPinYinHeadChar(temp
										.getString("deptName")));
						deptLists.add(dept);
					}
				} catch (Exception e) {
					Log.i("数据转换出错", e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return deptLists;
	}
	/**
	 * 在线查询
	 * @author liweijun 2012-09-25 编写
	 * @param department
	 * @param name
	 * @return
	 */
	public static List<OnLineSearch> findOsList(String department, String name) {
		List<OnLineSearch> osList = null;
		request = new HttpPost(urlStr);
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("flag", "onLineSearch"));
		params.add(new BasicNameValuePair("department", department));
		params.add(new BasicNameValuePair("name", name));
		HttpClient client = getHttpClient();
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse respose = client.execute(request);
			if (respose.getStatusLine().getStatusCode() == 200) {
				osList = new ArrayList<OnLineSearch>();
				String msg = EntityUtils.toString(respose.getEntity());
				Log.i("JSON数据：", msg);
				JSONObject result = new JSONObject(msg);
				JSONArray jsonArray = result.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject temp = (JSONObject) jsonArray.opt(i);
					OnLineSearch os = new OnLineSearch(temp.getString("phonenum"),
							temp.getString("department"), temp.getString("username"),
							temp.getString("taskcontent"));
					osList.add(os);
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return osList;
	}

	/**
	 * @author liweijun 2012-09-19 编写<br>
	 *         设置网络超时
	 * @return
	 */
	private static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
}
