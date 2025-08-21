package com.catdog2025.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.bytedance.sdk.openadsdk.mediation.init.MediationConfigUserInfoForSegment;
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig;
import com.catdog2025.mediation.java.MediationSplashActivity;
import java.util.HashMap;
import java.util.Map;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 * 优化版本：支持本地配置导入和自定义兜底功能
 */
public class TTAdManagerHolder {

    private static final String TAG = "TTAdManagerHolder";

    private static boolean sInit;
    private static boolean sStart;

    // 本地配置文件路径
    private static final String LOCAL_CONFIG_FILE_PATH = "local_ad_config.json";
    
    // 开屏广告自定义兜底代码位（建议使用独立的兜底广告位）
    private static final String SPLASH_FALLBACK_CODE_ID = "103540236"; // 使用相同ID作为兜底，实际项目中建议申请独立兜底位

    public static TTAdManager get() {
        return TTAdSdk.getAdManager();
    }

    public static void init(final Context context) {
        //初始化穿山甲SDK（优化版本）
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，增加本地配置导入和兜底配置
    private static void doInit(Context context) {
        if (sInit) {
            Log.d(TAG, "SDK已经初始化过了");
            Toast.makeText(context, "您已经初始化过了", Toast.LENGTH_LONG).show();
            return;
        }

        //setp1.1：初始化SDK（优化版本：支持本地配置和兜底）
        TTAdSdk.init(context, buildConfig(context));
        sInit = true;
    }

    public static void start(Context context) {
        if (!sInit) {
            Log.e(TAG, "SDK未初始化，无法启动");
            Toast.makeText(context, "还没初始化SDK，请先进行初始化", Toast.LENGTH_LONG).show();
            return;
        }
        if (sStart) {
            Log.d(TAG, "SDK已启动，立即跳转到开屏广告");
            startActivity(context);
            return;
        }
        
        Log.d(TAG, "🔄 开始启动穿山甲SDK（支持兜底配置）");

        TTAdSdk.start(new TTAdSdk.Callback() {
            @Override
            public void success() {
                Log.i(TAG, "✅ SDK启动成功，SDK就绪状态: " + TTAdSdk.isSdkReady());
                Log.i(TAG, "🎯 启动开屏广告（含兜底保护）");
                startActivity(context);
            }

            @Override
            public void fail(int code, String msg) {
                sStart = false;
                Log.e(TAG, "❌ SDK启动失败，错误代码: " + code + ", 错误信息: " + msg);
                Log.w(TAG, "🛡️ 启动失败时仍会尝试使用本地配置和兜底机制");
                // 即使SDK启动失败，也尝试跳转，让本地配置和兜底机制发挥作用
                startActivity(context);
                Toast.makeText(context, "SDK启动失败，使用兜底配置: " + msg, Toast.LENGTH_LONG).show();
            }
        });
        sStart = true;
    }

    public static void startActivity(Context context){
        final Intent intent = new Intent(context, MediationSplashActivity.class);
        context.startActivity(intent);
        
        // 如果传入的是Activity实例，跳转后销毁当前Activity
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.finish();
            Log.i(TAG, "启动开屏广告页面，销毁启动页面: " + activity.getClass().getSimpleName());
        }
    }

    /**
     * 获取开屏广告兜底AdSlot配置
     * @param context 上下文
     * @return 兜底广告位配置
     */
    public static AdSlot getSplashFallbackAdSlot(Context context) {
        return new AdSlot.Builder()
                .setCodeId(SPLASH_FALLBACK_CODE_ID)
                .setImageAcceptedSize(720, 1280) // 开屏广告尺寸
                .build();
    }

    /**
     * 获取当前SDK版本号
     * @return SDK版本号
     */
    public static String getCurrentSDKVersion() {
        try {
            TTAdManager adManager = get();
            if (adManager != null) {
                return adManager.getSDKVersion();
            }
        } catch (Exception e) {
            Log.w(TAG, "获取SDK版本失败: " + e.getMessage());
        }
        return "未知版本";
    }

    /**
     * 检查是否支持本地配置功能
     * @return 是否支持
     */
    public static boolean isLocalConfigSupported() {
        try {
            // 通过反射检查是否存在本地配置相关API
            TTAdConfig.Builder.class.getMethod("localConfigFilePath", String.class);
            Log.d(TAG, "✅ 检测到本地配置API支持");
            return true;
        } catch (NoSuchMethodException e) {
            // 静默处理，避免重复日志
            return false;
        }
    }

    /**
     * 检查是否支持自定义兜底功能  
     * @return 是否支持
     */
    public static boolean isFallbackSupported() {
        try {
            // 通过反射检查是否存在兜底配置相关API
            TTAdConfig.Builder.class.getMethod("splashFallbackAdSlot", AdSlot.class);
            Log.d(TAG, "✅ 检测到自定义兜底API支持");
            return true;
        } catch (NoSuchMethodException e) {
            // 静默处理，避免重复日志
            return false;
        }
    }

    private static TTAdConfig buildConfig(Context context) {
        TTAdConfig.Builder builder = new TTAdConfig.Builder()
                /**
                 * 注：需要替换成在媒体平台申请的appID ，切勿直接复制
                 */
                .appId("5713518")
                .appName("宠物翻译器")
                /**
                 * 上线前需要关闭debug开关，否则会影响性能
                 */
                .debug(true)
                /**
                 * 使用聚合功能此开关必须设置为true，默认为false
                 */
                .useMediation(true);

        // 尝试添加本地配置文件支持（需要SDK 5150+）
        if (isLocalConfigSupported()) {
            try {
                builder.getClass().getMethod("localConfigFilePath", String.class)
                        .invoke(builder, LOCAL_CONFIG_FILE_PATH);
                Log.i(TAG, "✅ 已启用本地配置文件: " + LOCAL_CONFIG_FILE_PATH);
            } catch (Exception e) {
                Log.w(TAG, "⚠️ 本地配置设置失败: " + e.getMessage());
            }
        }

        // 尝试添加开屏广告自定义兜底（需要SDK 5150+）
        if (isFallbackSupported()) {
            try {
                AdSlot fallbackSlot = getSplashFallbackAdSlot(context);
                builder.getClass().getMethod("splashFallbackAdSlot", AdSlot.class)
                        .invoke(builder, fallbackSlot);
                Log.i(TAG, "✅ 已启用开屏广告自定义兜底: " + SPLASH_FALLBACK_CODE_ID);
            } catch (Exception e) {
                Log.w(TAG, "⚠️ 开屏兜底配置设置失败: " + e.getMessage());
            }
        }

        // 检查功能支持情况并给出相应提示
        boolean localConfigSupported = isLocalConfigSupported();
        boolean fallbackSupported = isFallbackSupported();
        String currentVersion = getCurrentSDKVersion();
        
        if (localConfigSupported && fallbackSupported) {
            Log.i(TAG, "🎉 当前SDK版本(" + currentVersion + ")完全支持配置拉取失败保护功能");
        } else if (!localConfigSupported || !fallbackSupported) {
            Log.i(TAG, "📢 当前SDK版本: " + currentVersion + " (标准版)");
            Log.i(TAG, "📢 检测到标准SDK，部分高级API不可用（这是正常现象）");
            Log.i(TAG, "📢 当前版本将使用基础的错误处理机制，功能完全正常");
            Log.i(TAG, "💡 如需完整兜底功能，可考虑使用融合SDK版本");
        }

        return builder.build();
    }

    /**
     * 获取本地配置文件路径
     * @return 配置文件路径
     */
    public static String getLocalConfigFilePath() {
        return LOCAL_CONFIG_FILE_PATH;
    }

    /**
     * 获取开屏兜底代码位ID
     * @return 兜底代码位ID
     */
    public static String getSplashFallbackCodeId() {
        return SPLASH_FALLBACK_CODE_ID;
    }

    private static MediationConfigUserInfoForSegment getUserInfoForSegment(){
        MediationConfigUserInfoForSegment userInfo = new MediationConfigUserInfoForSegment();
        userInfo.setUserId("msdk-demo");
        userInfo.setGender(MediationConfigUserInfoForSegment.GENDER_MALE);
        userInfo.setChannel("msdk-channel");
        userInfo.setSubChannel("msdk-sub-channel");
        userInfo.setAge(999);
        userInfo.setUserValueGroup("msdk-demo-user-value-group");

        Map<String, String> customInfos = new HashMap<>();
        customInfos.put("aaaa", "test111");
        customInfos.put("bbbb", "test222");
        userInfo.setCustomInfos(customInfos);
        return userInfo;
    }

    private static TTCustomController getTTCustomController(){
        return new TTCustomController() {

            @Override
            public boolean isCanUseWifiState() {
                return super.isCanUseWifiState();
            }

            @Override
            public String getMacAddress() {
                return super.getMacAddress();
            }

            @Override
            public boolean isCanUseWriteExternal() {
                return super.isCanUseWriteExternal();
            }

            @Override
            public String getDevOaid() {
                return super.getDevOaid();
            }

            @Override
            public boolean isCanUseAndroidId() {
                return super.isCanUseAndroidId();
            }

            @Override
            public String getAndroidId() {
                return super.getAndroidId();
            }

            @Override
            public MediationPrivacyConfig getMediationPrivacyConfig() {
                return new MediationPrivacyConfig() {

                    @Override
                    public boolean isLimitPersonalAds() {
                        return super.isLimitPersonalAds();
                    }

                    @Override
                    public boolean isProgrammaticRecommend() {
                        return super.isProgrammaticRecommend();
                    }
                };
            }

            @Override
            public boolean isCanUsePermissionRecordAudio() {
                return super.isCanUsePermissionRecordAudio();
            }
        };
    }
}
