package etrans.klnet.co.kr.etrans;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import javax.net.ssl.HttpsURLConnection;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    ObservableWebView WebView01;
    RelativeLayout rel_intro;
    RelativeLayout rel_main;
    Context myApp;
    Uri mCapturedImageURI;
    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private final static int FILECHOOSER_NORMAL_REQ_CODE = 1;
    private final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2;
    private final Handler handler = new Handler();
    private RequestQueue queue;

    private Toast toast;

    boolean isLoginPage = false;
    boolean isMianPage = false;


    long LoginBackKeyClickTme;
    long MainBackKeyClickTme;
    String deviceId = "";
    String sHash = "";
    int clickCount = 0;
    int clickLongCount = 0;
    final private int get_my_devices = 1000;
    final private int get_setting_info = 1001;
    final private int regist_app = 1002;
    final private int delete_app = 1003;
    final private int other_delete_app = 1004;
    boolean isKeyboard = false;
    String sAuthKey = "";
    //Security Check
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "";
    public static final String ROOTING_PATH_1 = "/system/bin/su";
    public static final String ROOTING_PATH_2 = "/system/xbin/su";
    public static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    public static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";
    public static final String ROOTING_PATH_5 = "/system/app/Superuser.apk";


    public String[] RootFilesPath = new String[]{
            ROOT_PATH + ROOTING_PATH_1 ,
            ROOT_PATH + ROOTING_PATH_2 ,
            ROOT_PATH + ROOTING_PATH_3 ,
            ROOT_PATH + ROOTING_PATH_4 ,
            ROOT_PATH + ROOTING_PATH_5
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Rooting Check
        final AlertDialog.Builder alertDialogBuilderExit = new AlertDialog.Builder(this);


        if(!BuildConfig.DEBUG ) {
            queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, DataSet.connect_url + "/newmobile/selectMobileHashKey.do?app_id=ETRANS&app_os=android&app_version=1.2", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        sHash = response.getString("hash_code");
                        if (!sHash.trim().equals(getHashKey().trim())) {
                            alertDialogBuilderExit.setMessage("프로그램 무결성에 위배됩니다. \nPlayStore 내에서 \n 설치하시기 바랍니다.").setCancelable(false)
                                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MainActivity.this.finish();
                                        }
                                    });
                            AlertDialog dialog = alertDialogBuilderExit.create();
                            dialog.show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("########",error.toString());
                }
            });

            queue.add(jsonObjectRequest);
            //Rooting Check
            boolean isRootingFlag = false;
            try {
                Runtime.getRuntime().exec("su");
                isRootingFlag = true;
            } catch (Exception e) {
                // Exception 나면 루팅 false;
                isRootingFlag = false;
            }

            if (!isRootingFlag) {
                isRootingFlag = checkRootingFiles(createFiles(RootFilesPath));
            }

            Log.d("test", "isRootingFlag = " + isRootingFlag);

            alertDialogBuilderExit.setTitle("프로그램 종료");


            if (isRootingFlag == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }


            if (kernelBuildTagTest() == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.\n Error Code : 2").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }
            if (shellComendExecuteCheck() == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.\n Error Code : 3").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }


        }

        //Rooting Check End
        DataSet.getInstance().isrunning = "true";
        DataSet.getInstance().islogin = "false";
        DataSet.getInstance().userid = "";

        rel_intro = (RelativeLayout) findViewById(R.id.rel_intro);
        rel_main = (RelativeLayout) findViewById(R.id.rel_main);

        myApp = this;
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        deviceId = DataSet.getDeviceID(this);
        //     2019.04.30 버전체크 로직변경 LDG
        //forceUpdate();
        Log.d("CHECK", "deviceId :" + deviceId);

        WebView01 = (ObservableWebView) findViewById(R.id.webView);
        WebSettings webSettings = WebView01.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        WebView01.addJavascriptInterface(new AndroidBridge(), "AndroidInterface");
        WebView01.clearHistory();
        WebView01.clearCache(true);
        WebView01.clearView();

        WebView01.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                WebView01.getWindowVisibleDisplayFrame(r);
                int screenHeight = WebView01.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    if(isKeyboard == false) {
                        Log.i("CHECK", "keyboard show!!"+ keypadHeight);
                        WebView01.loadUrl("javascript:setKeyboard("+keypadHeight+",'Y')");
                        isKeyboard = true;
                    }
                }
                else {
                    if(isKeyboard == true) {
                        Log.i("CHECK", "keboard hide!!");
                        WebView01.loadUrl("javascript:setKeyboard(0, 'Y')");
                        isKeyboard = false;
                    }
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }





        //        String storeVersion = getMarketVersion(getPackageName());
//        String deviceVersion = "";
//        Log.e("###",storeVersion);
//        try {
//            deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            deviceVersion = "0";
//        }
//        Log.i("CHECK", "deviceVersion :" + deviceVersion);
//
//        if (storeVersion.compareTo(deviceVersion) > 0) {
//            // 업데이트 필요
//            AlertDialog.Builder alertDialogBuilder =
//                    new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light));
//            alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
//                    .setMessage("새로운버전("+storeVersion+")이 나왔습니다. 업데이트 하시겠습니까?")
//                    .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//
//                            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
//                            startActivity(intent);
//                        }
//                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.setCanceledOnTouchOutside(true);
//            alertDialog.show();
//        }


//        Log.i("CHECK", "url :" + DataSet.connect_url + "/newmobile/login.do");
//        WebView01.loadUrl(DataSet.connect_url + "/newmobile/login.do");

        Log.i("CHECK", "url :" + DataSet.connect_url + "/newmobile/login.do");
        WebView01.loadUrl(DataSet.connect_url + "/newmobile/login.do");

//        WebView01.setOnTouchListener(new OnClickWithOnTouchListener(this, new OnClickWithOnTouchListener.OnClickListener() {
//            @Override
//            public void onClick() {
//                // onclick 이벤트 처리
//                clickCount++;
//                if (clickCount > 5) {
//                    clickCount = 0;
//                }
//                Log.i("CHECK", "on Clik 이다...." + clickCount);
//            }
//            @Override
//            public void onLongClick() {
//                // 로그인 화면에서 길게누르고 5번클릭 길게 누르고
//                if (clickCount == 5 && clickLongCount == 1) {
//                    if ("D".equals(DataSet.isMode)) {
//                        //REAL 사이트
//                        DataSet.connect_url = DataSet.connect_real_url;
//                        DataSet.push_url = DataSet.push_real_url;
//                        DataSet.isMode = "P";
//                    } else {
//                        //TEST 사이트
//                        DataSet.connect_url = DataSet.connect_test_url;
//                        DataSet.push_url = DataSet.push_test_url;
//                        DataSet.isMode = "D";
//                    }
//                    WebView01.loadUrl("javascript:setChangeMode('"+DataSet.isMode+"')");
//                } else  clickLongCount = 1;
//
//                clickCount = 0;
//                Log.i("CHECK", "on Long Click 이다...." + clickLongCount);
//            }
//        }));



        WebView01.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimeType);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");
                    String fileName = contentDisposition.replace("inline; filename=", "");
                    fileName = fileName.replaceAll("\"", "");
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        } else {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        }
                    }

                }
            }
        });
//        WebView01.setDownloadListener(new DownloadListener() {
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//
//        });

        WebView01.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (view == null || url == null) {
                    return false;
                }


                if (url.contains("play.google.com")) {
                    // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
                    String[] params = url.split("details");
                    if (params.length > 1) {
                        url = "market://details" + params[1];
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }
                }


                if (url.contains(".text") || url.contains(".txt")) {
                    DataSet.getInstance().istext = "true";
                }

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    // HTTP/HTTPS 요청은 내부에서 처리한다.
                    view.loadUrl(url);
                } else {
                    Intent intent;

                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e) {
                        // 처리하지 못함
                        return false;
                    }

                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
                        if (url.startsWith("intent:") && intent.getPackage() != null) {
                            url = "market://details?id=" + intent.getPackage();
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        } else {
                            // 처리하지 못함
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("/newmobile/login.do")) {
                    isLoginPage = true;
                    isMianPage = false;
                } else if (url.contains("/newmobile/main.do")) {
                    rel_intro.setVisibility(View.GONE);
                    rel_main.setVisibility(View.VISIBLE);
                    isLoginPage = false;
                    isMianPage = true;
                } else {
                    isLoginPage = false;
                    isMianPage = false;
                }
            }


            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {

                new AlertDialog.Builder(myApp)
                        .setTitle("확인")
                        .setMessage("접속 할 수 없습니다. 관리자에게 문의 바랍니다.")
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataSet.getInstance().isrunning = "false";
                                        DataSet.getInstance().islogin = "false";
                                        DataSet.getInstance().userid = "";
                                        finish();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                super.onReceivedError(view, errorCode, description, failingUrl);
            }



            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.contains("/newmobile/login.do")) {
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    String isAutoLogin = prefs.getString("isAutoLogin", "");
                    String vId = prefs.getString("vId", "");
                    //String vPassword = prefs.getString("vPassword", "");  //deviceId

                    //Log.d("CHECK", "vId : " + vId + " vPassword : " + vPassword + " isAutoLogin : " + isAutoLogin);
                    Log.d("CHECK", "vId : " + vId + " deviceId : " + deviceId + " isAutoLogin : " + isAutoLogin);

                    WebView01.loadUrl("javascript:setIsAutoLogin('" + isAutoLogin + "','" + deviceId + "','" + vId + "')");
                    if (isAutoLogin.equals("Y") && !vId.equals("") && !deviceId.equals("")) {
                        WebView01.loadUrl("javascript:appAutoLogin('" + vId + "','" + deviceId + "')");
                    } else {
                        rel_intro.setVisibility(View.GONE);
                        rel_main.setVisibility(View.VISIBLE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
                    }
                }

                if (url.contains("/newmobile/main.do")) {
                    WebView01.loadUrl("javascript:getUnreadNotiCnt('"+DataSet.APPID+"','"+deviceId+"');");
                    if( DataSet.getInstance().push_id != null) {
                        WebView01.loadUrl("javascript:open_push_menu();");
                        WebView01.loadUrl("javascript:getNotiList('all','Y');");
                        DataSet.getInstance().push_id = null;
                        DataSet.getInstance().msg = null;
                    }
                }

                //최초 실행 여부 판단
                SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
                boolean first = pref.getBoolean("isFirst", false);
                if(first==false){
                    Log.d("first","THE FIRST TIME");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirst",true);
                    editor.commit();
                    DialogHtmlView();
                }

            }
        });


        WebView01.setWebChromeClient(new WebChromeClient() {
            //            // For Android < 3.0
//            public void openFileChooser( ValueCallback<Uri> uploadMsg) {
//                Log.d("MainActivity", "3.0 <");
//                openFileChooser(uploadMsg, "");
//            } // For Android 3.0+
//            public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType) {
//                Log.d("MainActivity", "3.0+");
//                filePathCallbackNormal = uploadMsg;
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("image/*");
//                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
//            }
//                // For Android 4.1+
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                Log.d("MainActivity", "4.1+");
//                openFileChooser(uploadMsg, acceptType);
//            }
//            // For Android 5.0+
//            public boolean onShowFileChooser( WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
//                Log.d("MainActivity", "5.0+");
//
//                if (filePathCallbackLollipop != null) {
//                    filePathCallbackLollipop.onReceiveValue(null);
//                    filePathCallbackLollipop = null;
//                }
//                filePathCallbackLollipop = filePathCallback;
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("image/*");
//                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_LOLLIPOP_REQ_CODE);
//                return true;
//            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
                Log.e("###","3.0 " + uploadMsg);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                filePathCallbackNormal = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
                Log.e("###","3.0+ " + uploadMsg + " " + acceptType);
            }

            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
                Log.e("###","4.1+ " + uploadMsg + " " + acceptType + " " + capture);
            }


            // For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                Log.e("###","5.0+ " + fileChooserParams.toString() + " " + filePathCallback.toString());
                if (filePathCallbackLollipop != null) {
//                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
                filePathCallbackLollipop = filePathCallback;


                // Create AndroidExampleFolder at sdcard
                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }

                // Create camera captured image file path and name
                try {
                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mCapturedImageURI = Uri.fromFile(file);
                }catch (Exception e) {
                    Log.e("###", e.toString());
                }
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");

                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "작업 선택");
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
                return true;

            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(myApp)
                        .setTitle("")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                // TODO Auto-generated method stub
                //return super.onJsConfirm(view, url, message, result);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("확인")
                        .setMessage(message)
                        .setPositiveButton("확인",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("취소",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
        checkPermissionF();
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void SendVersion(final String arg) {
            handler.post(new Runnable() {
                public void run() {

                    String versionName = "";
                    try {
                        PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionName = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                    if (arg != null && !arg.equals(versionName)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("확인")
                                .setMessage("새로운버전("+arg+")이 나왔습니다. 업데이트 하시겠습니까?")
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.klnet.plism3.plsim3")));
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                }
            });
        }

        @JavascriptInterface
        public void SendAppUrlBack(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendAppUrlBack(" + arg + ")");
                    WebView01.loadUrl(DataSet.connect_url + arg);
                }
            });
        }
        @JavascriptInterface
        public void sendToast(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(),arg,Toast.LENGTH_SHORT).show();
                }
            });
        }
        @JavascriptInterface
        public void sendLog(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                }
            });
        }
//
//        //로그인 후 저장
//        @JavascriptInterface
//        public void SendAppAutoRegister(final String vId, final String vIsAutoLogin, final String vIsAutoKey) {
//            handler.post(new Runnable() {
//                public void run() {
//
//
//                    SharedPreferences prefsAuth = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefsAuth.edit();
//
//                    if (vIsAutoLogin.equals("N")) {
//                        editor.putString("isAutoLogin", "N");
//                        DataSet.getInstance().islogin = "false";
//                    }else {
//                        editor.putString("isAutoLogin", "Y");
//                        DataSet.getInstance().islogin = "true";
//                    }
//                    editor.putString("AuthKey",vIsAutoKey);
//                    editor.commit();
//
//                    DataSet.getInstance().userid = vId;
//
//
//                    WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.jsp");
//
//                }
//            });
//        }
        //로그인 후 저장
        @JavascriptInterface
        public void SendAppAutoRegister(final String vId, final String vDeviceKey, final String isAutoLogin) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppAutoRegister(" + vId + ","+vDeviceKey+","+isAutoLogin+")");
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("isAutoLogin", isAutoLogin);
                    editor.putString("vId", vId);
                    editor.putString("vDeviceKey", vDeviceKey);
                    editor.remove("vPassword");

                    editor.commit();
                    DataSet.getInstance().userid = vId;
                    DataSet.getInstance().islogin = "true";

                    WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.do");

                }
            });
        }
        //기기값 전달
        @JavascriptInterface
        public void SendDeviceId() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String deviceid = deviceId;
                    WebView01.loadUrl("javascript:fn_setDeviceId('" + deviceid + "')");
                }
            });
        }

        //자동로그인 처리
        @JavascriptInterface
        public void SendAppAutoLoginResult(final String arg, final String arg1) {
            handler.post(new Runnable() {
                public void run() {
                    if("success".equals(arg)) {
                        //rel_intro.setVisibility(View.GONE);
                        //rel_main.setVisibility(View.VISIBLE);
                        DataSet.getInstance().userid = arg1;
                        DataSet.getInstance().islogin = "true";
                        WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.do");

                    } else {
                        rel_intro.setVisibility(View.GONE);
                        rel_main.setVisibility(View.VISIBLE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
                    }

                }
            });
        }

        //푸시 사용여부
        @JavascriptInterface
        public void SendAppPushOnOff(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    new Thread() {
                        public void run() {
                            if("ON".equals(arg)) {
                                SharedPreferences prefs2 = getSharedPreferences("JPP_GCM_Property", Activity.MODE_PRIVATE);
                                String sRegId = prefs2.getString("prefGCMRegsterID", null);
                                String surl = DataSet.push_url + "/ccsFcm.do?cmd=regist_app&appid=" + DataSet.APPID + "&did=" + deviceId + "&userid=" + DataSet.getInstance().userid + "&os=fcm_and&token=" + sRegId + "&model_name=" + Build.MODEL;
                                byte[] bRet = getHttp(surl);

                                String str = "";
                                try {
                                    str = new String(bRet, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    Log.e("CHECK", "", e);
                                }

                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = regist_app;
                                msg.obj = str;
                                mHandler.sendMessage(msg);
                            } else {
                                String surl = DataSet.push_url + "/ccsFcm.do?cmd=delete_app&appid=" + DataSet.APPID + "&did=" + deviceId;
                                byte[] bRet = getHttp(surl);

                                String str = "";
                                try {
                                    str = new String(bRet, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    Log.e("CHECK", "", e);
                                }

                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = delete_app;
                                msg.obj = str;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }.start();

                }
            });
        }

        //푸시 사용기기 삭제
        @JavascriptInterface
        public void SendAppPushDeviceDelete(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppPushDeviceDelete(" + arg + ")");
                    new Thread() {
                        public void run() {
                            String surl = DataSet.push_url + "/ccsFcm.do?cmd=delete_app&appid=" + DataSet.APPID + "&did=" + arg;
                            Log.i("CHECK", surl);
                            byte[] bRet = getHttp(surl);

                            String str = "";
                            try {
                                str = new String(bRet, "UTF-8");
                                Log.i("CHECK", str);
                            } catch (UnsupportedEncodingException e) {
                                Log.e("CHECK", "", e);
                            }

                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = other_delete_app;
                            msg.obj = str;
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }
            });
        }


        //푸시 시간 요일 설정
        @JavascriptInterface
        public void SendAppSetTime(final String startTime, final  String endTime, final String notiRecvDay) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetTime(" + startTime + ", " + endTime + ", " + notiRecvDay + ")");

                    String notiFromHHmm = startTime;
                    String notiToHHmm = endTime;
                    if ("".equals(startTime)) {
                        notiFromHHmm = "0000";
                        WebView01.loadUrl("javascript:setStartTime()");
                    } else {
                        if ("".equals(endTime)) notiToHHmm = "2359";
                        WebView01.loadUrl("javascript:setEndTime()");
                    }
                    final String surl = DataSet.push_url + "/ccsFcm.do?cmd=update_setting_time&appid=" + DataSet.APPID + "&regUser="+DataSet.getInstance().userid+
                            "&notiFromHHmm="+notiFromHHmm+"&notiToHHmm="+notiToHHmm+"&notiRecvDay="+notiRecvDay;
                    new Thread() {
                        public void run() {

                            byte[] bRet = getHttp(surl);

                            String str = "";
                            try {
                                str = new String(bRet, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.e("CHECK", "", e);
                            }

                            Message msg = mHandler.obtainMessage();
                            //msg.obj = str;
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }
            });
        }


//        //환경설정세팅
//        @JavascriptInterface
//        public void SendAppInitConfig() {
//            handler.post(new Runnable() {
//                public void run() {
//                    Log.d("CHECK", "SendAppInitConfig()");
//                    SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
//                    String isAutoLogin = prefs.getString("isAutoLogin", "");
//                    if ("Y".equals(isAutoLogin)) {
//                        WebView01.loadUrl("javascript:setConfigIsAutoLogin('Y');");
//                    }
//
//                    String rversion = null;
//                    try {
//                        rversion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//                        Log.i("CHECK", "version : "+rversion);
//                        WebView01.loadUrl("javascript:setAppVersion('"+rversion+"');");
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    new Thread() {
//                        public void run() {
//                            String surl = DataSet.push_url+"/ccsFcm.do?cmd=get_my_devices&appid="+DataSet.APPID+"&userid="+DataSet.getInstance().userid;
//                            Log.i("CHECK", surl);
//                            byte[] bRet = getHttp(surl);
//
//                            String str = "";
//                            try {
//                                str = new String(bRet, "UTF-8");
//                                Log.i("CHECK", str);
//                            } catch (UnsupportedEncodingException e) {
//                                Log.e("CHECK", "", e);
//                            }
//
//                            Message msg = mHandler.obtainMessage();
//                            msg.arg1 = get_my_devices;
//                            msg.obj = str;
//                            mHandler.sendMessage(msg);
//
//                            str = "";
//                            surl = DataSet.push_url+"/ccsFcm.do?cmd=get_setting_info&appid="+DataSet.APPID+"&userid="+DataSet.getInstance().userid;
//                            Log.i("CHECK", surl);
//                            bRet = getHttp(surl);
//                            try {
//                                str = new String(bRet, "UTF-8");
//                                Log.i("CHECK", str);
//                            } catch (UnsupportedEncodingException e) {
//                                Log.e("CHECK", "", e);
//                            }
//
//                            msg = mHandler.obtainMessage();
//                            msg.arg1 = get_setting_info;
//                            msg.obj = str;
//                            mHandler.sendMessage(msg);
//                        }
//                    }.start();
//
//                }
//            });
//        }
        //환경설정세팅
        @JavascriptInterface
        public void SendAppInitConfig() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppInitConfig()");
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    String isAutoLogin = prefs.getString("isAutoLogin", "");
                    if ("Y".equals(isAutoLogin)) {
                        WebView01.loadUrl("javascript:setConfigIsAutoLogin('Y');");
                    }

                    String rversion = null;
                    try {
                        rversion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        Log.i("CHECK", "version : "+rversion);
                        WebView01.loadUrl("javascript:setAppVersion('"+rversion+"');");
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    new Thread() {
                        public void run() {
                            String surl = DataSet.push_url+"/ccsFcm.do?cmd=get_my_devices&appid="+DataSet.APPID+"&userid="+DataSet.getInstance().userid;
                            Log.i("CHECK", surl);
                            byte[] bRet = getHttp(surl);

                            String str = "";
                            try {
                                str = new String(bRet, "UTF-8");
                                Log.i("CHECK", str);
                            } catch (UnsupportedEncodingException e) {
                                Log.e("CHECK", "", e);
                            }

                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = get_my_devices;
                            msg.obj = str;
                            mHandler.sendMessage(msg);

                            str = "";
                            surl = DataSet.push_url+"/ccsFcm.do?cmd=get_setting_info&appid="+DataSet.APPID+"&userid="+DataSet.getInstance().userid;
                            Log.i("CHECK", surl);
                            bRet = getHttp(surl);
                            try {
                                str = new String(bRet, "UTF-8");
                                Log.i("CHECK", str);
                            } catch (UnsupportedEncodingException e) {
                                Log.e("CHECK", "", e);
                            }

                            msg = mHandler.obtainMessage();
                            msg.arg1 = get_setting_info;
                            msg.obj = str;
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }
            });
        }
        //자동로그인시 웹뷰보이도록
        @JavascriptInterface
        public void SendAppLoadWebview(final String arg1) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppLoadWebview(), arg1 :: " + arg1);

                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    //SharedPreferences.Editor editor = prefs.edit();
                    //editor.putString("isAutoLogin", isAutoLogin);
                    //editor.putString("vId", vId);
                    //editor.putString("vPassword", vEncPwd);

                    //editor.commit();
                    String vId = prefs.getString("vId", "");
                    DataSet.getInstance().userid = vId;
                    DataSet.getInstance().islogin = "true";

                    rel_intro.setVisibility(View.GONE);
                    rel_main.setVisibility(View.VISIBLE);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
                }
            });
        }


//        //환경설정 자동로그인 세팅
//        @JavascriptInterface
//        public void SendAppConfigSetAutoLogin(final String arg, final  String arg1) {
//            handler.post(new Runnable() {
//                public void run() {
//                    Log.d("CHECK", "SendAppConfigSetAutoLogin(" + arg + ",'" + arg1 +"')");
//
//                    SharedPreferences prefsAuto = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
//                    SharedPreferences.Editor editorAuth = prefsAuto.edit();
//                    editorAuth.putString("AuthKey", arg1);
//                    editorAuth.putString("isAutoLogin", arg);
//                    editorAuth.commit();
//                }
//            });
//        }
        //환경설정 자동로그인 세팅
        @JavascriptInterface
        public void SendAppConfigSetAutoLogin(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppConfigSetAutoLogin(" + arg + ")");

                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("isAutoLogin", arg);

                    editor.commit();
                }
            });
        }

        //환경설정 자동로그인 세팅
        @JavascriptInterface
        public void SendAppSetServiceList(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetServiceList(" + arg + ")");
                    final JSONArray[] jsonArray = {null};
                    new Thread() {
                        public void run() {
                            try {
                                jsonArray[0] = new JSONArray(arg);

                                for (int i = 0; i < jsonArray[0].length(); i++) {
                                    JSONObject jobj = jsonArray[0].getJSONObject(i);
                                    String url = jobj.getString("urlStr");
                                    String[] tmp = url.split("&");

                                    String pushServiceCode = "";
                                    String startTime = "";
                                    String endTime = "";
                                    String useYn = "";
                                    String klnetId = "";
                                    String notiRecvDay = "";

                                    for (int j = 0; j < tmp.length; j++) {
                                        String[] keyVal = tmp[j].split("=");

                                        String key = keyVal[0];
                                        String val = "";
                                        if ("pushServiceCode".equals(keyVal[0]))
                                            pushServiceCode = keyVal[1];
                                        else if ("startTime".equals(keyVal[0]))
                                            startTime = keyVal[1];
                                        else if ("endTime".equals(keyVal[0])) endTime = keyVal[1];
                                        else if ("useYn".equals(keyVal[0])) useYn = keyVal[1];
                                        else if ("klnetId".equals(keyVal[0])) klnetId = keyVal[1];
                                        else if ("notiRecvDay".equals(keyVal[0]))
                                            notiRecvDay = keyVal[1];

                                    }

                                    final String infoUrl = DataSet.push_url + "/ccsFcm.do?cmd=register_setting_service&appid=" + DataSet.APPID + "&pushServiceCode=" + pushServiceCode +
                                            "&regUser=" + DataSet.getInstance().userid + "&notiFromHHmm=" + startTime + "&notiToHHmm=" + endTime + "&useYn=" + useYn + "&klnetId=" + klnetId + "&notiRecvDay=" + notiRecvDay;

                                    Log.i("CHECK", infoUrl);
                                    byte[] bRet = getHttp(infoUrl);

                                    String str = "";
                                    try {
                                        str = new String(bRet, "UTF-8");
                                        Log.i("CHECK", str);
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e("CHECK", "", e);
                                    }
                                }
                                Message msg = mHandler.obtainMessage();
                                //msg.arg1 = other_delete_app;
                                //msg.obj = str;
                                mHandler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            });
        }


        //알림내역조회
        @JavascriptInterface
        public void SendAppNotiList(final String type, final String updateYn) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppNotiList(" + type + "," + updateYn + ")");

                    if("Y".equals(updateYn)) {
                        WebView01.loadUrl("javascript:setPushReceiveAll('"+DataSet.getInstance().userid+"','"+DataSet.APPID+"','"+deviceId+"');");
                    }
                    WebView01.loadUrl("javascript:getPushRecentListOfDevice('"+DataSet.getInstance().userid+"','"+DataSet.APPID+"','"+deviceId+"','"+type+"');");
                }
            });
        }


//        //로그아웃 이후 처리
//        @JavascriptInterface
//        public void SendAppLogout(final String arg) {
//            handler.post(new Runnable() {
//                public void run() {
//                    Log.d("CHECK", "SendAppLogout(" + arg + ")");
//                    SharedPreferences prefsAuto = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
//                    SharedPreferences.Editor editorAuth = prefsAuto.edit();
//                    editorAuth.putString("AuthKey", "");
//                    editorAuth.putString("isAutoLogin", "N");
//                    editorAuth.commit();
//
//                    WebView01.loadUrl(DataSet.connect_url + arg);
//                }
//            });
//        }
//        //
        //로그아웃 이후 처리
        @JavascriptInterface
        public void SendAppLogout(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppLogout(" + arg + ")");
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    //editor.putString("isAutoLogin", "N");
                    //editor.putString("vId", "");
                    editor.putString("vDeviceKey", "");

                    editor.commit();
                    WebView01.loadUrl(DataSet.connect_url + arg);
                }
            });
        }


        //앱스토어 화면으로 이동
        @JavascriptInterface
        public void SendAppGoAppUpdate() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppGoAppUpdate()");
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }

        //즐겨찾기 선사 추가 삭제
        @JavascriptInterface
        public void SendAppCarrierCodeSetting(final String type, final String code, final String name) {
            handler.post(new Runnable() {
                public void run() {
                    if("insert".equals(type)) {
                        Log.d("CHECK", "SendAppCarrierCodeSetting(" + type + "," + code + "," + name + ")");
                        SharedPreferences prefs = getSharedPreferences("CarrierCode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String json = prefs.getString("CarrierCode", null);
                        try {
                            JSONArray array = null;
                            if (json != null) {
                                array = new JSONArray(json);
                            } else {
                                array = new JSONArray();
                            }
                            JSONObject tmpj = new JSONObject();
                            tmpj.put("code", code);
                            tmpj.put("name", name);

                            array.put(tmpj);
                            editor.putString("CarrierCode", array.toString());
                            Log.d("CHECK", "array : " + array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    } else if("delete".equals(type)) {
                        Log.d("CHECK", "SendAppCarrierCodeSetting(" + type + "," + code + "," + name + ")");
                        SharedPreferences prefs = getSharedPreferences("CarrierCode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String json = prefs.getString("CarrierCode", null);
                        try {
                            JSONArray array = null;
                            if (json != null) {
                                array = new JSONArray(json);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String jcode = obj.getString("code");
                                    if(jcode.equals(code)) {
                                        array.remove(i);
                                        break;
                                    }

                                }
                            } else {
                                array = new JSONArray();
                            }

                            editor.putString("CarrierCode", array.toString());
                            Log.d("CHECK", "array : " + array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    }
                }
            });
        }

        //즐겨찾기 선사 세팅
        @JavascriptInterface
        public void SendAppSetBookmarkCarrierCode() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetBookmarkCarrierCode()");
                    SharedPreferences prefs = getSharedPreferences("CarrierCode", Activity.MODE_PRIVATE);
                    String json = prefs.getString("CarrierCode", null);


                    WebView01.loadUrl("javascript:goCarrierView("+json+");");


                }
            });
        }

        //즐겨찾기 터미널 추가 삭제
        @JavascriptInterface
        public void SendAppTerminalCodeSetting(final String type, final String code, final String name) {
            handler.post(new Runnable() {
                public void run() {
                    if("insert".equals(type)) {
                        Log.d("CHECK", "SendAppTerminalCodeSetting(" + type + "," + code + "," + name + ")");
                        SharedPreferences prefs = getSharedPreferences("TerminalCode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String json = prefs.getString("TerminalCode", null);
                        try {
                            JSONArray array = null;
                            if (json != null) {
                                array = new JSONArray(json);
                            } else {
                                array = new JSONArray();
                            }
                            JSONObject tmpj = new JSONObject();
                            tmpj.put("code", code);
                            tmpj.put("name", name);

                            array.put(tmpj);
                            editor.putString("TerminalCode", array.toString());
                            Log.d("CHECK", "array : " + array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    } else if("delete".equals(type)) {
                        Log.d("CHECK", "SendAppTerminalCodeSetting(" + type + "," + code + "," + name + ")");
                        SharedPreferences prefs = getSharedPreferences("TerminalCode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String json = prefs.getString("TerminalCode", null);
                        try {
                            JSONArray array = null;
                            if (json != null) {
                                array = new JSONArray(json);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String jcode = obj.getString("code");
                                    if(jcode.equals(code)) {
                                        array.remove(i);
                                        break;
                                    }

                                }
                            } else {
                                array = new JSONArray();
                            }

                            editor.putString("TerminalCode", array.toString());
                            Log.d("CHECK", "array : " + array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    }
                }
            });
        }

        //즐겨찾기 터미널 세팅
        @JavascriptInterface
        public void SendAppSetBookmarkTerminalCode() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetBookmarkTerminalCode()");
                    SharedPreferences prefs = getSharedPreferences("TerminalCode", Activity.MODE_PRIVATE);
                    String json = prefs.getString("TerminalCode", null);


                    WebView01.loadUrl("javascript:goTerminalView("+json+");");


                }
            });
        }


        //즐겨찾기 Pod 추가 삭제
        @JavascriptInterface
        public void SendAppPodCodeSetting(final String type, final String code, final String name) {
            handler.post(new Runnable() {
                public void run() {
                    if("insert".equals(type)) {
                        Log.d("CHECK", "SendAppPodCodeSetting(" + type + "," + code + "," + name + ")");
                        SharedPreferences prefs = getSharedPreferences("PodCode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String json = prefs.getString("PodCode", null);
                        try {
                            JSONArray array = null;
                            if (json != null) {
                                array = new JSONArray(json);
                            } else {
                                array = new JSONArray();
                            }
                            JSONObject tmpj = new JSONObject();
                            tmpj.put("code", code);
                            tmpj.put("name", name);

                            array.put(tmpj);
                            editor.putString("PodCode", array.toString());
                            Log.d("CHECK", "array : " + array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    } else if("delete".equals(type)) {
                        Log.d("CHECK", "SendAppPodCodeSetting(" + type + "," + code + "," + name + ")");
                        SharedPreferences prefs = getSharedPreferences("PodCode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String json = prefs.getString("PodCode", null);
                        try {
                            JSONArray array = null;
                            if (json != null) {
                                array = new JSONArray(json);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String jcode = obj.getString("code");
                                    if(jcode.equals(code)) {
                                        array.remove(i);
                                        break;
                                    }

                                }
                            } else {
                                array = new JSONArray();
                            }

                            editor.putString("PodCode", array.toString());
                            Log.d("CHECK", "array : " + array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    }
                }
            });
        }

        //즐겨찾기 Pod 세팅
        @JavascriptInterface
        public void SendAppSetBookmarkPodCode() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetBookmarkPodCode()");
                    SharedPreferences prefs = getSharedPreferences("PodCode", Activity.MODE_PRIVATE);
                    String json = prefs.getString("PodCode", null);


                    WebView01.loadUrl("javascript:goPodView("+json+");");


                }
            });
        }

        //선사콤보박스 세팅
        @JavascriptInterface
        public void SendAppSetCarrierCode() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetCarrierCode()");
                    SharedPreferences prefs = getSharedPreferences("CarrierCode", Activity.MODE_PRIVATE);
                    String json = prefs.getString("CarrierCode", null);


                    WebView01.loadUrl("javascript:setComboCarrierCode("+json+");");

                }
            });
        }

        //터미널 콤보박스 세팅
        @JavascriptInterface
        public void SendAppSetTerminalCode() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetTerminalCode()");
                    SharedPreferences prefs = getSharedPreferences("TerminalCode", Activity.MODE_PRIVATE);
                    String json = prefs.getString("TerminalCode", null);


                    WebView01.loadUrl("javascript:setComboTerminalCode("+json+");");

                }
            });
        }


        //Pod 콤보박스 세팅
        @JavascriptInterface
        public void SendAppSetPodCode() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppSetPodCode()");
                    SharedPreferences prefs = getSharedPreferences("PodCode", Activity.MODE_PRIVATE);
                    String json = prefs.getString("PodCode", null);


                    WebView01.loadUrl("javascript:setComboPodCode("+json+");");

                }
            });
        }


        //웹페이지 이동
        @JavascriptInterface
        public void SendAppGoWebUrl(final String url) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppGoWebUrl("+url+")");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DataSet.connect_url + url));
                    startActivity(intent);
                }
            });
        }

        //운영,개발 사이트 연결 수정
        @JavascriptInterface
        public void setChangeMode(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "setChangeMode()");
                    if ("D".equals(DataSet.isMode)) {
                        //REAL 사이트
                        DataSet.connect_url = DataSet.connect_real_url;
                        DataSet.push_url = DataSet.push_real_url;
                        DataSet.isMode = "P";
                    } else {
                        //TEST 사이트
                        DataSet.connect_url = DataSet.connect_test_url;
                        DataSet.push_url = DataSet.push_test_url;
                        DataSet.isMode = "D";
                    }
                    WebView01.loadUrl(DataSet.connect_url + arg);

                }
            });
        }

        @JavascriptInterface
        public void SendAppAutoReLogin(final String sVersion) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "SendAppAutoReLogin("+sVersion+")");

                    float fVersion=Float.parseFloat(sVersion);
                    PackageManager packageManager = getPackageManager();
                    PackageInfo packageInfo = null;
                    Log.d("###","forceUpdate");
                    try {
                        packageInfo =packageManager.getPackageInfo(getPackageName(),0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String currentVersion = packageInfo.versionName;
                    float fCurrentVersion=Float.parseFloat(currentVersion);
                    if(fVersion > fCurrentVersion) {
                        AlertDialog.Builder alertDialogBuilder =
                                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_DeviceDefault_Light));
                        alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
                                .setMessage("새로운버전("+fVersion+")이 나왔습니다. 업데이트 하시겠습니까?")
                                .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);

                                        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.this.finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(true);
                        alertDialog.show();
                    } else {
                        SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                        String isAutoLogin = prefs.getString("isAutoLogin", "Y");
                        String vId = prefs.getString("vId", DataSet.getInstance().userid);
                        //String vPassword = prefs.getString("vPassword", "");

                        Log.d("CHECK", "vId : " + vId + " isAutoLogin : " + isAutoLogin);
                        Log.d("CHECK", "deviceId " + deviceId + " isAutoLogin : " + isAutoLogin);
                        Log.d("CHECK","DataSet.getInstance().userid : "+DataSet.getInstance().userid);
                        Log.d("CHECK","DataSet.getInstance().islogin : "+DataSet.getInstance().islogin);

                        WebView01.loadUrl("javascript:setIsAutoLogin('"+isAutoLogin+"','" + deviceId + "','" + vId + "')");
                        if (DataSet.getInstance().islogin.equals("true") && isAutoLogin.equals("Y") && !vId.equals("") ) {
                            WebView01.loadUrl("javascript:appAutoLogin('" + vId + "','" + deviceId + "')");
                        }
                    }
                }
            });
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int duration = 2000;
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isLoginPage) {
                if (System.currentTimeMillis() > LoginBackKeyClickTme + 2000) {
                    LoginBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;
                }

                if (System.currentTimeMillis() <= LoginBackKeyClickTme + 2000) {
                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    this.finish();
                    return true;
                }
            } else if (isMianPage) {
                if (System.currentTimeMillis() > MainBackKeyClickTme + 2000) {
                    MainBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;
                }

                if (System.currentTimeMillis() <= MainBackKeyClickTme + 2000) {
                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    finish();
                    return true;
                }
            } else if(DataSet.getInstance().istext.equals("true")) {
                DataSet.getInstance().istext = "false";
                WebView01.goBack();
                return true;
            }else {
                WebView01.loadUrl("javascript:appUrlBack();");
                //WebView01.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    public void finishGuide() {
        toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public byte[] getHttp(String surl)
    {
        try
        {
            URL url = new URL(surl);
            HttpURLConnection conn = null;
            if (url.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                conn = https;
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String cookies = CookieManager.getInstance().getCookie(surl);
            if (cookies != null && !cookies.equals(""))
                conn.setRequestProperty("Cookie", cookies);

            conn.connect();

            BufferedInputStream bis = new BufferedInputStream(
                    conn.getInputStream());

            List<String> cookies2 = conn.getHeaderFields().get("set-cookie");
            if (cookies2 != null) {
                for (String cookie : cookies2) {
                    CookieManager.getInstance().setCookie(surl, cookie);
                }
            }

            int size = conn.getContentLength();
            if (size == -1) size = 1024;
            byte[] temp = new byte[size];
            ByteArrayOutputStream b1 = new ByteArrayOutputStream();
            int len = -1;
            do {
                len = bis.read(temp);
                if (len > -1)
                    b1.write(temp, 0, len);
            } while (len > -1);
            b1.flush();

            bis.close();

            conn.disconnect();

            return b1.toByteArray();
        }
        catch(Exception ex)
        {
            Log.e("CHECK", "", ex);

            return null;
        }

    }


    @Override
    protected void onResume() {
        if(DataSet.getInstance().islogin.equals("true")) {
            if (DataSet.getInstance().push_id != null && DataSet.getInstance().isbackground.equals("true")) {
                //앱이 실행된 상태에서 푸시 클릭한 경우 처리 부분

                //앱 실행 아이콘 개수 조절
                Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                badgeIntent.putExtra("badge_count_package_name", "com.etrans.klnet.co.kr.etrans");
                badgeIntent.putExtra("badge_count_class_name", "com.etrans.klnet.co.kr.etrans.MainActivity");
                sendBroadcast(badgeIntent);

                /**
                 * 푸시 처리 관련 앱 기능 추가
                 * */

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);

                DataSet.getInstance().isbackground = "false";
                WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.do");
            }
        } else {
            if (DataSet.getInstance().push_id != null && DataSet.getInstance().isbackground.equals("true")) {

                //앱 실행 아이콘 개수 조절
                Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                badgeIntent.putExtra("badge_count_package_name", "com.etrans.klnet.co.kr.etrans");
                badgeIntent.putExtra("badge_count_class_name", "com.etrans.klnet.co.kr.etrans.MainActivity");
                sendBroadcast(badgeIntent);

                /**
                 * 푸시 처리 관련 앱 기능 추가
                 * */

                DataSet.getInstance().isbackground = "false";
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);
            }
        }

        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        Log.d("CHECK", "push_id : " + intent.getStringExtra("push_id"));
        //앱이 실행된 상태에서 푸시를 보는 경우
        if (intent != null) {
            if(DataSet.getInstance().islogin.equals("true")) {
                if (intent.getStringExtra("push_id") != null) {
                    //앱이 실행된 상태에서 푸시 클릭한 경우 처리 부분

                    DataSet.getInstance().push_id = intent.getStringExtra("push_id");
                    DataSet.getInstance().msg = intent.getStringExtra("msg");

                    //앱 실행 아이콘 개수 조절
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                    badgeIntent.putExtra("badge_count_package_name", "com.etrans.klnet.co.kr.etrans");
                    badgeIntent.putExtra("badge_count_class_name", "com.etrans.klnet.co.kr.etrans.MainActivity");
                    sendBroadcast(badgeIntent);

                    /**
                     * 푸시 처리 관련 앱 기능 추가
                     * */

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);

                    //
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("알림")
                            .setMessage( DataSet.getInstance().msg)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.do");
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataSet.getInstance().push_id = null;
                                            DataSet.getInstance().msg = null;
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            } else {
                if (intent.getStringExtra("push_id") != null) {
                    DataSet.getInstance().push_id = intent.getStringExtra("push_id");
                    DataSet.getInstance().msg = intent.getStringExtra("msg");

                    //앱 실행 아이콘 개수 조절
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                    badgeIntent.putExtra("badge_count_package_name", "com.etrans.klnet.co.kr.etrans");
                    badgeIntent.putExtra("badge_count_class_name", "com.etrans.klnet.co.kr.etrans.MainActivity");
                    sendBroadcast(badgeIntent);

                    /**
                     * 푸시 처리 관련 앱 기능 추가
                     * */

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);

                    //
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("알림")
                            .setMessage( DataSet.getInstance().msg)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataSet.getInstance().push_id = null;
                                            DataSet.getInstance().msg = null;
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == get_my_devices) {
                String str = (String) msg.obj;
                WebView01.loadUrl("javascript:setDeviceResult("+str+",'" +deviceId+"');");
            } else if (msg.arg1 == get_setting_info) {
                String str = (String) msg.obj;
                WebView01.loadUrl("javascript:getServiceResult("+str+");");
            } else if (msg.arg1 == regist_app) {
                String str = (String) msg.obj;
                WebView01.loadUrl("javascript:setPushOnOff("+str+",'ON');");
            } else if (msg.arg1 == delete_app) {
                String str = (String) msg.obj;
                WebView01.loadUrl("javascript:setPushOnOff("+str+",'OFF');");
            } else if (msg.arg1 == other_delete_app) {
                Log.i("CHECK", "TEST");
                String str = (String) msg.obj;
                WebView01.loadUrl("javascript:resultDeleteDevice("+str+");");
            }
        }
    };

    public static String getMarketVersion(String packageName) {

        Log.d("CHECK", packageName);

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get();
            Elements Version = document.select(".content");
            for (Element element : Version) {
                if (element.attr("itemprop").equals("softwareVersion")) {
                    return element.text().trim();
                }
            }
        } catch (Exception ex) {
            Log.e("CHECK",ex.toString());
            return "1";
        }
        return null;
    }

    public static String getMarketVersionFast(String packageName) {

        String mData = "", mVer = null;

        try {
            URL mUrl = new URL("https://play.google.com/store/apps/details?id=" + packageName);

            HttpURLConnection mConnection = (HttpURLConnection) mUrl.openConnection();
            if (mConnection == null) {
                return null;
            }

            mConnection.setConnectTimeout(5000);
            mConnection.setUseCaches(false);
            mConnection.setDoOutput(true);

            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
                while(true) {
                    String line = bufferedReader.readLine();
                    if (line == null)
                        break;
                    mData += line;
                }
                bufferedReader.close();
            }
            mConnection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        String startToken = "softwareVersion\">";
        String endToken = "<";
        int index = mData.indexOf(startToken);

        if (index == -1) {
            mVer = null;
        } else {
            mVer = mData.substring(index + startToken.length(), index + startToken.length() + 100);
            mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
        }
        return mVer;
    }


    private void checkPermissionF() {

        if (android.os.Build.VERSION.SDK_INT >= M) {
            // only for LOLLIPOP and newer versions
            Log.d("CHECK","Hello Marshmallow (마시멜로우)");
            int permissionResult = getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int camerapermissionResult = getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA);
            if (permissionResult == PackageManager.PERMISSION_DENIED || camerapermissionResult == PackageManager.PERMISSION_DENIED) {
                //요청한 권한( WRITE_EXTERNAL_STORAGE )이 없을 때..거부일때...
                /* 사용자가 WRITE_EXTERNAL_STORAGE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                 * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                 */



                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light));
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("단말기의 파일쓰기 권한 및 카메라 권한이 필요합니다.\n계속하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= M) {

                                        Log.i("CHECK","감사합니다. 권한을 허락했네요 (마시멜로우)");
                                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                                    }

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create()
                            .show();

                    //최초로 권한을 요청할 때.
                } else {
                    Log.i("CHECK","최초로 권한을 요청할 때. (마시멜로우)");
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 1);
                }


            }else{
                //권한이 있을 때.
            }

        } else {
            Log.d("CHECK","(마시멜로우 이하 버전입니다.)");
            //   getThumbInfo();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
/*            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*/

            for(int i = 0 ; i < permissions.length ; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult WRITE_EXTERNAL_STORAGE ( 권한 성공 ) ");
                    }


                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult READ_EXTERNAL_STORAGE ( 권한 성공 ) ");
                    }

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult CAMERA ( 권한 성공 ) ");
                    }
                }


            }

        } else {
            Log.i("CHECK","onRequestPermissionsResult ( 권한 거부) ");
            Toast.makeText(getApplicationContext(), "요청 권한 거부", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 루팅파일 의심 Path를 가진 파일들을 생성 한다.
     */
    private File[] createFiles(String[] sfiles){
        File[] rootingFiles = new File[sfiles.length];
        for(int i=0 ; i < sfiles.length; i++){
            rootingFiles[i] = new File(sfiles[i]);
        }
        return rootingFiles;
    }

    /**
     * 루팅파일 여부를 확인 한다.
     */
    private boolean checkRootingFiles(File... file){
        boolean result = false;
        for(File f : file){
            if(f != null && f.exists() && f.isFile()){
                result = true;
                break;
            }else{
                result = false;
            }
        }
        return result;
    }
    /**
     * 최초 실행 알림창
     */
    private void DialogHtmlView(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("[필수적 접근 권한] \n" +
                "*인터넷 : 인터넷을 이용한 eTrans 서비스 접근 \n" +
                "*저장공간 : 기기 사진, 미디어, 파일 액세스 권한으로 다운로드 파일 보관\n" +
                "[선택적 접근 권한] \n" +
                "*푸시알림 : PUSH 알림 서비스");
        ab.setPositiveButton("확인", null);
        AlertDialog title = ab.create();
        title.setTitle("앱 권한 이용 안내");
        title.show();
    }
    /////////////////////////플레이스토어 VerSion체크 로직 추가 2019.04.30 LDG/////////////////////////
    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        Log.e("###","forceUpdate");
        try {
            packageInfo =packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion,this).execute();
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;
        private Context context;
        public ForceUpdateAsync(String currentVersion, Context context){
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName()+ "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.e("latestversion","---"+latestVersion);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null){
                if(!currentVersion.equalsIgnoreCase(latestVersion)){
                    AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(new ContextThemeWrapper(this.context, android.R.style.Theme_DeviceDefault_Light));
                    alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
                            .setMessage("새로운버전("+latestVersion+")이 나왔습니다. 업데이트 하시겠습니까?")
                            .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);

                                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                    startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    /* 커널 빌드 태그 검사 */
    public boolean kernelBuildTagTest() {

        String buildTags = Build.TAGS;

        if(buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }else {
            return false;
        }
    }
    /* Shell 명령어 실행 가능 여부 */
    public boolean shellComendExecuteCheck() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public String getHashKey(){
        String hashKey = "";
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("####",e.toString());
            e.printStackTrace();
        }
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);

            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest.");
                return null;
            }
        }
        return hashKey;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
            if (filePathCallbackNormal == null) return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            filePathCallbackNormal.onReceiveValue(result);
            filePathCallbackNormal = null;
        } else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {
            Uri[] result = new Uri[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if(resultCode == RESULT_OK){
                    result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                }
                filePathCallbackLollipop.onReceiveValue(result);
            }
        }
    }
}
