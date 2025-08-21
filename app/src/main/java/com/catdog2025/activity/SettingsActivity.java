package com.catdog2025.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.catdog2025.R;

/**
 * 设置页面Activity
 * 包含隐私政策、联系我们、关于我们等功能入口
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private LinearLayout btnPrivacyPolicy;
    private LinearLayout btnContactUs;
    //private LinearLayout btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        initListeners();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy);
        btnContactUs = findViewById(R.id.btn_contact_us);
        //btnAbout = findViewById(R.id.btn_about);
    }

    /**
     * 初始化点击监听器
     */
    private void initListeners() {
        btnBack.setOnClickListener(this);
        btnPrivacyPolicy.setOnClickListener(this);
        btnContactUs.setOnClickListener(this);
        //btnAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.btn_back) {
            // 返回按钮
            finish();
        } else if (id == R.id.btn_privacy_policy) {
            // 隐私政策
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        } else if (id == R.id.btn_contact_us) {
            // 联系我们
            startActivity(new Intent(this, ContactUsActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 