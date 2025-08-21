package com.catdog2025.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.catdog2025.R;

/**
 * 权限使用说明对话框
 * 在应用首次启动时显示，向用户说明各种权限的用途
 */
public class PermissionExplanationDialog extends Dialog {
    
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_FIRST_LAUNCH = "is_first_launch";
    
    private Context mContext;
    private OnDialogActionListener mListener;
    
    /**
     * 对话框操作监听器
     */
    public interface OnDialogActionListener {
        /**
         * 用户点击"我知道了"按钮
         */
        void onConfirm();
        
        /**
         * 用户点击拒绝按钮
         */
        void onReject();
        
        /**
         * 用户点击"查看详细隐私政策"按钮
         */
        void onViewPrivacyPolicy();
    }
    
    public PermissionExplanationDialog(@NonNull Context context) {
        super(context, R.style.CustomDialogStyle);
        this.mContext = context;
    }
    
    public PermissionExplanationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_permission_explanation);
        
        // 设置对话框不可取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        
        initViews();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        TextView titleText = findViewById(R.id.tv_dialog_title);
        TextView contentText = findViewById(R.id.tv_dialog_content);
        Button agreeButton = findViewById(R.id.btn_agree);
        Button rejectButton = findViewById(R.id.btn_reject);
        Button privacyButton = findViewById(R.id.btn_privacy_policy);
        
        // 设置标题
        titleText.setText("隐私政策与服务条款");
        
        // 设置内容
        String content = "欢迎使用宠物翻译器！\n\n" +
                "🔒 隐私保护承诺\n" +
                "我们严格遵循最小化原则，仅在您使用相关功能时申请必要权限\n\n" +
                "🎤 录音功能说明\n" +
                "当您点击录音按钮时，我们会申请录音权限来录制您的声音\n\n" +
                "💾 数据存储说明\n" +
                "录音文件仅保存在您的设备本地，不会上传到服务器\n\n" +
                "🌐 网络使用说明\n" +
                "我们使用网络加载广告内容，支持应用免费使用\n\n" +
                "📋 权限申请原则\n" +
                "所有权限都会在使用前向您说明用途并征得同意\n\n" +
                "继续使用即表示您同意我们的隐私政策和服务条款。\n\n";
        
        // 创建SpannableString来设置公司名称居中显示
        String companyName = "山东闪迪智能科技股份有限公司\n© 2014-2025";
        SpannableString spannableContent = new SpannableString(content + companyName);
        
        // 设置公司名称居中对齐
        int companyStart = content.length();
        int companyEnd = companyStart + companyName.length();
        spannableContent.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 
                companyStart, companyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        contentText.setText(spannableContent);
        
        // 设置按钮点击事件
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onConfirm();
                }
            }
        });
        
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onReject();
                }
            }
        });
        
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onViewPrivacyPolicy();
                }
            }
        });
    }
    
    /**
     * 设置对话框操作监听器
     */
    public void setOnDialogActionListener(OnDialogActionListener listener) {
        this.mListener = listener;
    }
    
    /**
     * 检查是否为首次启动
     * @param context 上下文
     * @return true表示首次启动，false表示非首次启动
     */
    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }
    
    /**
     * 标记已经不是首次启动
     * @param context 上下文
     */
    public static void markNotFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }
    
    /**
     * 重置首次启动标记（用于测试）
     * @param context 上下文
     */
    public static void resetFirstLaunchFlag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, true).apply();
    }
}