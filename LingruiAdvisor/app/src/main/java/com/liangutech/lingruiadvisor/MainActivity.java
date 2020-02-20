package com.liangutech.lingruiadvisor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    WebView webView;
   
    IntentFilter intentFilter;

    LinearLayout welcomeLayout;
    LinearLayout logoLayout;
    private long exitTime = 0;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;


    String url = "http://test-advisor.liangutech.com/advisor";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        welcomeLayout=findViewById(R.id.welcomeLayout);
        logoLayout=findViewById(R.id.logoLayout);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        webView = findViewById(R.id.webView);
        //启用JavaScript。
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
        webView.getSettings().setTextZoom(100);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        webView.setBackgroundColor(0);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }





        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    webView.setVisibility(View.VISIBLE);
                    //登陆、工作台、我的客户、项目库、获客咨询
                    if ("登录".equals(view.getTitle()) | "工作台".equals(view.getTitle()) |
                            "我的客户".equals(view.getTitle()) | "项目库".equals(view.getTitle()) | "获客咨询".equals(view.getTitle())) {
                                hideTop();

                    } else {

                    }
                    logoLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //加载方法
                view.loadUrl("javascript:hideOther();");

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }


    }

    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");//文件上传
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    private void hideTop() {
        try {
            //定义javaScript方法
            String javascript = "javascript:function hideTop() { "
                    + "document.getElementsByClassName('top-menu toolbar')[0].style.display='none'"
                    + "}";

            //加载方法
            webView.loadUrl(javascript);
            //执行方法
            webView.loadUrl("javascript:hideTop();");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == event.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {

        }
    });

}
