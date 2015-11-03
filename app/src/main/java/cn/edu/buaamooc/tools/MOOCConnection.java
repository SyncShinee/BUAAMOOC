package cn.edu.buaamooc.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.edu.buaamooc.CONST;


public class MOOCConnection {
	private static String token = ""; //保存初始化获取的csrftoken，供之后的访问使用
	private static List<Cookie> cookies; //保存初始化和登陆界面返回的所有cookie，供之后的访问使用
	private JSONObject resultJsonObject = null; //服务器返回的数据转换成的JSON数据
	private JSONArray Jsonarray = null; //服务器返回的数据转换成的JSON数组

	public boolean MOOCInit() {
		//初始化连接获得token和服务器参数，GET，无参数
		//返回0，初始化失败，返回1：初始化成功
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(CONST.INITURL);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			cookies = httpclient.getCookieStore().getCookies(); //获取全部cookie
			for (int i = 0; i < cookies.size(); i++) {
				//从cookie中获取“csrftoken”
				if (cookies.get(i).getName().equals("csrftoken")) {
					token = cookies.get(i).getValue();
					break;
				}
			}
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false; //Todo
		} finally {
			//Todo         
		}
		return true;
	}

	public JSONObject MOOCLogin(String username, String password) {
		//登陆，POST，参数：email用户名，password密码
		//返回登录时服务器返回的状态JSON
		try {
			HttpPost httppost = new HttpPost(CONST.LOGINURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", username));
			params.add(new BasicNameValuePair("password", password));
			//将用户名，密码加入param中，供传给服务器使用
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			//对params进行utf8编码，防止中文乱码
			httppost.addHeader("X-CSRFToken", token);
			//将X-CSRFToken加入httppost
			DefaultHttpClient httpclient = new DefaultHttpClient();
			org.apache.http.client.CookieStore store = httpclient.getCookieStore();
			for (int i = 0; i < cookies.size(); i++) {
				store.addCookie(cookies.get(i));
			}
			//将保存的全部cookie加入httpClient，m1阶段失败就是因为少这个
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			StringBuilder entityStringBuilder = new StringBuilder();
			if (statusCode == HttpStatus.SC_OK) {
				//访问服务器成功，获取服务器数据
				if (entity != null) {
					try {
						BufferedReader bufferedReader = new BufferedReader
								(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line);
						}

						resultJsonObject = new JSONObject(entityStringBuilder.toString());
						//将服务器的数据转换为JSON数据
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			cookies = httpclient.getCookieStore().getCookies();
			//保存login的cookie，证明已经登录，否则，用之前init获得的cookie，服务器会认为未登录
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultJsonObject;
	}

	public JSONArray MOOCCourses() {
		//全部课程列表，GET，无参数
		//返回全部课程的JSON数组
		//内部的代码注释与login相同
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(CONST.COURSEURL);
			org.apache.http.client.CookieStore store = httpclient.getCookieStore();
			for (int i = 0; i < cookies.size(); i++) {
				store.addCookie(cookies.get(i));
			}
			//将保存的全部cookie加入httpClient
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			StringBuilder entityStringBuilder = new StringBuilder();
			if (statusCode == HttpStatus.SC_OK) {
				//访问服务器成功，获取服务器数据
				if (entity != null) {
					try {
						BufferedReader bufferedReader = new BufferedReader
								(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line);
						}
						Jsonarray = new JSONArray(entityStringBuilder.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			//Todo
		} finally {
			//Todo         
		}
		return Jsonarray;
	}

	public JSONObject MOOCCourseAbout(String couse_Id) {
		//获取特定课程信息，POST，参数：course_id课程ID（对应courses里的id参数）
		//返回课程信息的JSON
		try {
			HttpPost httppost = new HttpPost(CONST.COURSEABOUTURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("course_id", couse_Id));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httppost.addHeader("X-CSRFToken", token);
			//将X-CSRFToken加入httppost
			DefaultHttpClient httpclient = new DefaultHttpClient();
			org.apache.http.client.CookieStore store = httpclient.getCookieStore();
			for (int i = 0; i < cookies.size(); i++) {
				store.addCookie(cookies.get(i));
			}
			//将保存的全部cookie加入httpClient
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			StringBuilder entityStringBuilder = new StringBuilder();
			if (statusCode == HttpStatus.SC_OK) {
				//访问服务器成功，获取服务器数据
				if (entity != null) {
					try {
						BufferedReader bufferedReader = new BufferedReader
								(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line);
						}
						resultJsonObject = new JSONObject(entityStringBuilder.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultJsonObject;
	}

	public JSONObject MOOCCourseware(String couse_Id) {
		//某门课程的课程章节列表，POST，参数：course_id课程ID
		//返回课程列表的JSON数据，如果访问失败，则返回null
		try {
			HttpPost httppost = new HttpPost(CONST.COURSEWAREURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("course_id", couse_Id));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httppost.addHeader("X-CSRFToken", token);
			//将X-CSRFToken加入httppost
			DefaultHttpClient httpclient = new DefaultHttpClient();
			org.apache.http.client.CookieStore store = httpclient.getCookieStore();
			for (int i = 0; i < cookies.size(); i++) {
				store.addCookie(cookies.get(i));
			}
			//将保存的全部cookie加入httpClient
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			StringBuilder entityStringBuilder = new StringBuilder();
			if (statusCode == HttpStatus.SC_OK) {
				//访问服务器成功，获取服务器数据
				if (entity != null) {
					try {
						BufferedReader bufferedReader = new BufferedReader
								(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line);
						}
						resultJsonObject = new JSONObject(entityStringBuilder.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultJsonObject;
	}

	public boolean MOOCCourseEnroll(String couse_Id, int enroll) {
		//选课/退选，POST，参数：course_id课程ID，enrollment_action：enroll=1选课，enroll=0退课
		//返回是否选课成功的boolean数据
		String enrollStr;
		boolean status = false;
		if (enroll == 1)
			enrollStr = "enroll";
		else if (enroll == 0)
			enrollStr = "unenroll";
		else
			return false;
		try {
			HttpPost httppost = new HttpPost(CONST.ENROLLURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("course_id", couse_Id));
			params.add(new BasicNameValuePair("enrollment_action", enrollStr));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httppost.addHeader("X-CSRFToken", token);
			//将X-CSRFToken加入httppost
			DefaultHttpClient httpclient = new DefaultHttpClient();
			org.apache.http.client.CookieStore store = httpclient.getCookieStore();
			for (int i = 0; i < cookies.size(); i++) {
				store.addCookie(cookies.get(i));
			}
			//将保存的全部cookie加入httpClient
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			StringBuilder entityStringBuilder = new StringBuilder();
			if (statusCode == HttpStatus.SC_OK) {
				//访问服务器成功，获取服务器数据
				if (entity != null) {
					try {
						BufferedReader bufferedReader = new BufferedReader
								(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line);
						}
						resultJsonObject = new JSONObject(entityStringBuilder.toString());
						status = resultJsonObject.getBoolean("status");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return status;
	}

	public JSONObject MOOCGetCourseEnrollment() {
		//获取已选课程列表，GET，无参数，
		//返回保存已选课程的JSON数据
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(CONST.GETENROLLURL);
			org.apache.http.client.CookieStore store = httpclient.getCookieStore();
			for (int i = 0; i < cookies.size(); i++) {
				store.addCookie(cookies.get(i));
			}
			//将保存的全部cookie加入httpClient
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			StringBuilder entityStringBuilder = new StringBuilder();
			if (statusCode == HttpStatus.SC_OK) {
				//访问服务器成功，获取服务器数据
				if (entity != null) {
					try {
						BufferedReader bufferedReader = new BufferedReader
								(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line);
						}
						resultJsonObject = new JSONObject(entityStringBuilder.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			//Todo
		} finally {
			//Todo         
		}
		return resultJsonObject;
	}

	public Bitmap MOOCGetImage(String path) {
		//根据地址访问服务器获取图片，参数图片的相对路径（相对于"http://mooc.buaa.edu.cn"）
		//返回获取的bitmap，如果获取失败，返回null
		Bitmap bitmap = null;
		String sourceUrl = CONST.URL + path;
		try {
			String[] a = path.split("/");
			sourceUrl = CONST.URL;
			for (int i = 0; i < a.length; i++) {
				a[i] = URLEncoder.encode(a[i], "utf-8");
			}
			for (int i = 0; i < a.length; i++) {
				sourceUrl += "/" + a[i];
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		//传入网络图片地址
		try {
			URL url = new URL(sourceUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			conn.connect();

			//以下部分代码我选则了保留，被注释掉的代码是将获取的bitmap保存为本地图片，可能以后的改进会使用，
			//毕竟每次都从服务器抓起图片浪费网络资源，可以保存本地，当图片发生变化再从服务器获取
	            
	            /*String expandName = sourceUrl.substring(
						sourceUrl.lastIndexOf(".") + 1, sourceUrl.length()).toLowerCase();
				String fileName = sourceUrl.substring(
						sourceUrl.lastIndexOf("/") + 1,
						sourceUrl.lastIndexOf("."));*/
			//File file = new File("/sdcard/pictures/" + fileName + "." + expandName);
			//FileOutputStream fos = new FileOutputStream(file);
			InputStream in = conn.getInputStream();
			//byte[] buffer=new byte[1024];
			//int len = 0;
			//while((len=in.read(buffer))!=-1){
			//fos.write(buffer, 0, len);
			//}
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
			//fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}
}