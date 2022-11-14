package etrans.klnet.co.kr.etrans;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html.ImageGetter;
import android.util.Log;

public class DataSet {
	public String isrunning = "false";
	public String islogin = "false";
	public String userid = "";
	public String isbackground = "false";
	public String istext = "false";
	public String push_id;	// 푸시ID
	public String msg;    		// 알림 메세지
	public String obj_id;		// 푸시 연관 계시물 ID
	public String type;		// 메세지 종류

	//최초
	public static String isMode = "D";  //P-운영,D-개발  최초접속모드
	//public static String connect_url = "https://etrans2.klnet.co.kr";
	public static String connect_url = "https://devetrans.klnet.co.kr";
	public static String push_url = "https://push.plism.com";

	//운영
	public static String connect_real_url = "https://etrans.klnet.co.kr";
	public static String push_real_url = "https://push.plism.com";

	//개발
	public static String connect_test_url = "https://devetrans.klnet.co.kr";
	public static String push_test_url = "https://testpush.plism.com";


//	public static String push_url = "http://testpush.plism.com:12195";
//	public static String connect_url = "https://devetrans2.klnet.co.kr";

	public static String APPID = "METRANS2";


	private static DataSet _instance;

	static {
		_instance = new DataSet();
	}

	private DataSet() {
		
	}

	public static DataSet getInstance() {
		return _instance;
	}

	public static String getDeviceID(Context context)
	{
		String serial = "";
		String androidId = "";
		try {
			serial = (String)Build.class.getField("SERIAL").get(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), serial.hashCode());
		return deviceUuid.toString();
	}

}
