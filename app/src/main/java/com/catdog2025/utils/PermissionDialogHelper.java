package com.catdog2025.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * 权限对话框帮助类
 * 用于显示权限说明和引导用户到设置页面
 */
public class PermissionDialogHelper {
    
    /**
     * 显示权限说明对话框
     * @param context 上下文
     * @param title 对话框标题
     * @param message 对话框内容
     * @param onConfirm 确认回调
     */
    public static void showPermissionExplanationDialog(Context context, String title, String message, 
                                                       Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onConfirm != null) {
                            onConfirm.run();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * 显示权限被拒绝后的引导对话框
     * @param context 上下文
     * @param permissionName 权限名称
     * @param reason 需要权限的原因
     */
    public static void showPermissionDeniedDialog(Context context, String permissionName, String reason) {
        String message = "应用需要" + permissionName + "权限来" + reason + "。\n\n" +
                "您可以在设置中手动开启此权限：\n" +
                "设置 > 应用管理 > 猫狗录音 > 权限";
        
        new AlertDialog.Builder(context)
                .setTitle("权限被拒绝")
                .setMessage(message)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSettings(context);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    
    /**
     * 显示录音权限说明对话框
     * @param context 上下文
     * @param onConfirm 确认回调
     */
    public static void showRecordAudioPermissionDialog(Context context, Runnable onConfirm) {
        String message = "应用需要录音权限来录制您的声音，然后播放对应的动物叫声。\n\n" +
                "我们承诺：\n" +
                "• 只在您主动录音时使用麦克风\n" +
                "• 录音文件仅保存在本地\n" +
                "• 不会上传或分享您的录音";
        
        showPermissionExplanationDialog(context, "录音权限说明", message, onConfirm);
    }
    
    /**
     * 显示存储权限说明对话框
     * @param context 上下文
     * @param onConfirm 确认回调
     */
    public static void showStoragePermissionDialog(Context context, Runnable onConfirm) {
        String message = "应用需要存储权限来保存您的录音文件。\n\n" +
                "我们承诺：\n" +
                "• 只访问应用自己的存储空间\n" +
                "• 不会读取您的其他文件\n" +
                "• 录音文件可随时删除";
        
        showPermissionExplanationDialog(context, "存储权限说明", message, onConfirm);
    }
    
    /**
     * 显示位置权限说明对话框
     * @param context 上下文
     * @param onConfirm 确认回调
     */
    public static void showLocationPermissionDialog(Context context, Runnable onConfirm) {
        String message = "应用需要位置权限来为您提供更精准的广告内容。\n\n" +
                "我们承诺：\n" +
                "• 仅用于广告投放优化\n" +
                "• 不会记录您的具体位置\n" +
                "• 拒绝此权限不影响核心功能";
        
        showPermissionExplanationDialog(context, "位置权限说明", message, onConfirm);
    }
    
    /**
     * 显示电话状态权限说明对话框
     * @param context 上下文
     * @param onConfirm 确认回调
     */
    public static void showPhoneStatePermissionDialog(Context context, Runnable onConfirm) {
        String message = "应用需要电话状态权限来确保广告功能正常运行。\n\n" +
                "我们承诺：\n" +
                "• 仅用于广告SDK正常工作\n" +
                "• 不会拨打或监听电话\n" +
                "• 不会获取您的通话记录";
        
        showPermissionExplanationDialog(context, "电话状态权限说明", message, onConfirm);
    }
    
    /**
     * 显示通知权限说明对话框
     * @param context 上下文
     * @param onConfirm 确认回调
     */
    public static void showNotificationPermissionDialog(Context context, Runnable onConfirm) {
        String message = "应用需要通知权限来向您发送重要消息。\n\n" +
                "我们承诺：\n" +
                "• 只发送必要的应用通知\n" +
                "• 不会发送垃圾信息\n" +
                "• 您可以随时在设置中关闭";
        
        showPermissionExplanationDialog(context, "通知权限说明", message, onConfirm);
    }
    
    /**
     * 打开应用设置页面
     * @param context 上下文
     */
    public static void openAppSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            // 如果无法打开应用设置，则打开通用设置
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }
    
    /**
     * 获取权限被拒绝的原因说明
     * @param permission 权限名称
     * @return 原因说明
     */
    public static String getPermissionDeniedReason(String permission) {
        String permissionName = PermissionManager.getPermissionName(permission);
        switch (permission) {
            case android.Manifest.permission.RECORD_AUDIO:
                return "录制您的声音并播放动物叫声";
            case android.Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case android.Manifest.permission.READ_EXTERNAL_STORAGE:
                return "保存录音文件";
            case android.Manifest.permission.ACCESS_COARSE_LOCATION:
            case android.Manifest.permission.ACCESS_FINE_LOCATION:
                return "提供更精准的广告内容";
            case android.Manifest.permission.READ_PHONE_STATE:
                return "确保广告功能正常运行";
            default:
                if (PermissionManager.NOTIFICATION_PERMISSION.equals(permission)) {
                    return "发送重要通知";
                }
                return "正常运行应用功能";
        }
    }
}