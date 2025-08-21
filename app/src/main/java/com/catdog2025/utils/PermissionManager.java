package com.catdog2025.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理工具类
 * 统一处理应用中所有危险权限的申请和检查
 */
public class PermissionManager {
    
    // 权限请求码
    public static final int REQUEST_CODE_PERMISSIONS = 1001;
    public static final int REQUEST_CODE_RECORD_AUDIO = 1002;
    public static final int REQUEST_CODE_STORAGE = 1003;
    public static final int REQUEST_CODE_LOCATION = 1004;
    public static final int REQUEST_CODE_PHONE_STATE = 1005;
    public static final int REQUEST_CODE_NOTIFICATION = 1006;
    public static final int REQUEST_CODE_SYSTEM_ALERT = 1007;
    
    // 应用中使用的所有危险权限
    public static final String[] DANGEROUS_PERMISSIONS = {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.SYSTEM_ALERT_WINDOW
    };
    
    // Android 13+ 通知权限
    public static final String NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS";
    
    /**
     * 检查单个权限是否已授予
     * @param context 上下文
     * @param permission 权限名称
     * @return 是否已授予权限
     */
    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 检查多个权限是否都已授予
     * @param context 上下文
     * @param permissions 权限数组
     * @return 是否所有权限都已授予
     */
    public static boolean hasAllPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取未授予的权限列表
     * @param context 上下文
     * @param permissions 需要检查的权限数组
     * @return 未授予的权限列表
     */
    public static String[] getDeniedPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new String[0];
        }
        
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[0]);
    }
    
    /**
     * 请求单个权限
     * @param activity Activity实例
     * @param permission 权限名称
     * @param requestCode 请求码
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }
    
    /**
     * 请求多个权限
     * @param activity Activity实例
     * @param permissions 权限数组
     * @param requestCode 请求码
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] deniedPermissions = getDeniedPermissions(activity, permissions);
            if (deniedPermissions.length > 0) {
                ActivityCompat.requestPermissions(activity, deniedPermissions, requestCode);
            }
        }
    }
    
    /**
     * 检查录音权限
     * @param context 上下文
     * @return 是否有录音权限
     */
    public static boolean hasRecordAudioPermission(Context context) {
        return hasPermission(context, Manifest.permission.RECORD_AUDIO);
    }
    
    /**
     * 请求录音权限
     * @param activity Activity实例
     */
    public static void requestRecordAudioPermission(Activity activity) {
        requestPermission(activity, Manifest.permission.RECORD_AUDIO, REQUEST_CODE_RECORD_AUDIO);
    }
    
    /**
     * 请求录音权限（带说明对话框）
     * @param activity Activity实例
     */
    public static void requestRecordAudioPermissionWithDialog(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            // 显示权限说明对话框
            PermissionDialogHelper.showRecordAudioPermissionDialog(activity, new Runnable() {
                @Override
                public void run() {
                    requestRecordAudioPermission(activity);
                }
            });
        } else {
            requestRecordAudioPermission(activity);
        }
    }
    
    /**
     * 检查存储权限
     * @param context 上下文
     * @return 是否有存储权限
     */
    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 不再需要存储权限
            return true;
        }
        return hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    
    /**
     * 请求存储权限
     * @param activity Activity实例
     */
    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            String[] storagePermissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestPermissions(activity, storagePermissions, REQUEST_CODE_STORAGE);
        }
    }
    
    /**
     * 请求存储权限（带说明对话框）
     * @param activity Activity实例
     */
    public static void requestStoragePermissionWithDialog(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 不需要存储权限
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 显示权限说明对话框
            PermissionDialogHelper.showStoragePermissionDialog(activity, new Runnable() {
                @Override
                public void run() {
                    requestStoragePermission(activity);
                }
            });
        } else {
            requestStoragePermission(activity);
        }
    }
    
    /**
     * 检查位置权限
     * @param context 上下文
     * @return 是否有位置权限
     */
    public static boolean hasLocationPermission(Context context) {
        return hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ||
               hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    
    /**
     * 请求位置权限
     * @param activity Activity实例
     */
    public static void requestLocationPermission(Activity activity) {
        String[] locationPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        };
        requestPermissions(activity, locationPermissions, REQUEST_CODE_LOCATION);
    }
    
    /**
     * 请求位置权限（带说明对话框）
     * @param activity Activity实例
     */
    public static void requestLocationPermissionWithDialog(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // 显示权限说明对话框
            PermissionDialogHelper.showLocationPermissionDialog(activity, new Runnable() {
                @Override
                public void run() {
                    requestLocationPermission(activity);
                }
            });
        } else {
            requestLocationPermission(activity);
        }
    }
    
    /**
     * 检查电话状态权限
     * @param context 上下文
     * @return 是否有电话状态权限
     */
    public static boolean hasPhoneStatePermission(Context context) {
        return hasPermission(context, Manifest.permission.READ_PHONE_STATE);
    }
    
    /**
     * 请求电话状态权限
     * @param activity Activity实例
     */
    public static void requestPhoneStatePermission(Activity activity) {
        requestPermission(activity, Manifest.permission.READ_PHONE_STATE, REQUEST_CODE_PHONE_STATE);
    }
    
    /**
     * 请求电话状态权限（带说明对话框）
     * @param activity Activity实例
     */
    public static void requestPhoneStatePermissionWithDialog(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
            // 显示权限说明对话框
            PermissionDialogHelper.showPhoneStatePermissionDialog(activity, new Runnable() {
                @Override
                public void run() {
                    requestPhoneStatePermission(activity);
                }
            });
        } else {
            requestPhoneStatePermission(activity);
        }
    }
    
    /**
     * 检查通知权限（Android 13+）
     * @param context 上下文
     * @return 是否有通知权限
     */
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return hasPermission(context, NOTIFICATION_PERMISSION);
        }
        return true; // Android 13以下默认有通知权限
    }
    
    /**
     * 请求通知权限（Android 13+）
     * @param activity Activity实例
     */
    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(activity, NOTIFICATION_PERMISSION, REQUEST_CODE_NOTIFICATION);
        }
    }
    
    /**
     * 请求通知权限（带说明对话框）（Android 13+）
     * @param activity Activity实例
     */
    public static void requestNotificationPermissionWithDialog(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, NOTIFICATION_PERMISSION)) {
                // 显示权限说明对话框
                PermissionDialogHelper.showNotificationPermissionDialog(activity, new Runnable() {
                    @Override
                    public void run() {
                        requestNotificationPermission(activity);
                    }
                });
            } else {
                requestNotificationPermission(activity);
            }
        }
    }
    
    /**
     * 检查悬浮窗权限
     * @param context 上下文
     * @return 是否有悬浮窗权限
     */
    public static boolean hasSystemAlertWindowPermission(Context context) {
        return hasPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
    }
    
    /**
     * 请求悬浮窗权限
     * @param activity Activity实例
     */
    public static void requestSystemAlertWindowPermission(Activity activity) {
        requestPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW, REQUEST_CODE_SYSTEM_ALERT);
    }
    
    /**
     * 检查权限申请结果
     * @param grantResults 权限申请结果数组
     * @return 是否所有权限都被授予
     */
    public static boolean isAllPermissionsGranted(int[] grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取权限的友好名称
     * @param permission 权限名称
     * @return 友好名称
     */
    public static String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.RECORD_AUDIO:
                return "录音";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "存储";
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "位置";
            case Manifest.permission.READ_PHONE_STATE:
                return "电话状态";
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                return "悬浮窗";
            default:
                if (NOTIFICATION_PERMISSION.equals(permission)) {
                    return "通知";
                }
                return "未知权限";
        }
    }
}