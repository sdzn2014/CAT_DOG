package com.catdog2025.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.catdog2025.R;

/**
 * 联系我们页面Activity
 * 提供开发者联系信息和邮件发送功能
 */
public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private TextView tvEmail;
    private ImageView btnCopyEmail;
    private Button btnCopyEmailFull;
    private Button btnSendEmail;

    private static final String EMAIL_ADDRESS = "SDZN2014@163.COM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        
        initViews();
        initListeners();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvEmail = findViewById(R.id.tv_email);
        btnCopyEmail = findViewById(R.id.btn_copy_email);
        btnCopyEmailFull = findViewById(R.id.btn_copy_email_full);
        btnSendEmail = findViewById(R.id.btn_send_email);
    }

    /**
     * 初始化点击监听器
     */
    private void initListeners() {
        btnBack.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        btnCopyEmail.setOnClickListener(this);
        btnCopyEmailFull.setOnClickListener(this);
        btnSendEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.btn_back) {
            // 返回按钮
            finish();
        } else if (id == R.id.tv_email || id == R.id.btn_copy_email || id == R.id.btn_copy_email_full) {
            // 复制邮箱地址
            copyEmailToClipboard();
        } else if (id == R.id.btn_send_email) {
            // 发送邮件
            sendEmail();
        }
    }

    /**
     * 复制邮箱地址到剪贴板
     */
    private void copyEmailToClipboard() {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("email", EMAIL_ADDRESS);
            clipboardManager.setPrimaryClip(clipData);
            
            // 显示复制成功提示
            Toast.makeText(this, getString(R.string.contact_email_copied), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "复制失败，请手动复制邮箱地址", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + EMAIL_ADDRESS));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "宠物翻译器用户反馈");
            emailIntent.putExtra(Intent.EXTRA_TEXT, 
                "尊敬的开发者：\n\n" +
                "我在使用宠物翻译器时遇到了以下问题/建议：\n\n" +
                "【请在此处描述您的问题或建议】\n\n" +
                "设备信息：\n" +
                "- 应用版本：1.0\n" +
                "- 设备型号：" + android.os.Build.MODEL + "\n" +
                "- 系统版本：Android " + android.os.Build.VERSION.RELEASE + "\n\n" +
                "感谢您的辛勤开发！\n");

            // 检查是否有邮件客户端
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(emailIntent, "选择邮件客户端"));
            } else {
                // 没有邮件客户端，提示用户复制邮箱
                copyEmailToClipboard();
                Toast.makeText(this, "未检测到邮件客户端，邮箱地址已复制到剪贴板", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 发送邮件失败，提示用户复制邮箱
            copyEmailToClipboard();
            Toast.makeText(this, "发送邮件失败，邮箱地址已复制到剪贴板", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 