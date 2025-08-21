package com.catdog2025.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.catdog2025.R;

/**
 * 隐私政策页面Activity
 * 使用WebView显示本地HTML隐私政策文件
 */
public class PrivacyPolicyActivity extends AppCompatActivity {

    private ImageView btnBack;
    private WebView webViewPrivacy;
    private LinearLayout layoutLoading;
    private LinearLayout layoutError;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        
        initViews();
        initListeners();
        loadPrivacyPolicy();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        webViewPrivacy = findViewById(R.id.webview_privacy);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutError = findViewById(R.id.layout_error);
        btnRetry = findViewById(R.id.btn_retry);
    }

    /**
     * 初始化点击监听器
     */
    private void initListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRetry.setOnClickListener(v -> loadPrivacyPolicy());
    }

    /**
     * 加载隐私政策
     */
    private void loadPrivacyPolicy() {
        showLoading();
        
        // 配置WebView设置
        webViewPrivacy.getSettings().setJavaScriptEnabled(false);
        webViewPrivacy.getSettings().setBuiltInZoomControls(true);
        webViewPrivacy.getSettings().setDisplayZoomControls(false);
        webViewPrivacy.getSettings().setSupportZoom(true);
        webViewPrivacy.getSettings().setUseWideViewPort(true);
        webViewPrivacy.getSettings().setLoadWithOverviewMode(true);
        webViewPrivacy.getSettings().setDefaultTextEncodingName("UTF-8");

        // 设置WebViewClient
        webViewPrivacy.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                showError();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                showError();
            }
        });

        try {
            // 加载本地HTML文件
            String url = "file:///android_asset/privacy_policy.html";
            webViewPrivacy.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            showError();
        }
    }

    /**
     * 显示加载状态
     */
    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        webViewPrivacy.setVisibility(View.GONE);
    }

    /**
     * 隐藏加载状态
     */
    private void hideLoading() {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        webViewPrivacy.setVisibility(View.VISIBLE);
    }

    /**
     * 显示错误状态
     */
    private void showError() {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        webViewPrivacy.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (webViewPrivacy.canGoBack()) {
            webViewPrivacy.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (webViewPrivacy != null) {
            webViewPrivacy.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webViewPrivacy.clearHistory();
            webViewPrivacy.destroy();
        }
        super.onDestroy();
    }
} 