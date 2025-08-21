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
        titleText.setText("权限使用说明");
        
        // 设置内容
        String content = "为了给您提供更好的服务体验，本应用需要使用以下权限：\n\n" +
                "🎤 录音权限\n" +
                "用于录制您的声音并转换为可爱的猫狗叫声\n\n" +
                "💾 存储权限\n" +
                "用于保存录音文件到本地，方便您重复播放\n\n" +
                "🌐 网络权限\n" +
                "用于加载广告内容，支持应用免费使用\n\n" +
                "📍 位置权限（可选）\n" +
                "用于提供更精准的广告推荐，您可以选择拒绝\n\n" +
                "📱 设备信息权限\n" +
                "用于广告统计和防作弊，保护您的使用体验\n\n" +
                "我们承诺严格保护您的隐私，仅在必要时使用相关权限，不会收集与功能无关的信息。\n\n";
        
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