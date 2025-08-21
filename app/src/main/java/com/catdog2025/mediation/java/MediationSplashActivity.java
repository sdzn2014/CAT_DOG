package com.catdog2025.mediation.java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.CSJSplashCloseType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.catdog2025.R;
import com.catdog2025.activity.CatDogActivity;
import com.catdog2025.config.TTAdManagerHolder;
import com.catdog2025.mediation.java.utils.Const;
import com.catdog2025.utils.UIUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 融合demo，开屏广告使用示例。更多功能参考接入文档。
 *
 * 注意：每次加载的广告，只能展示一次
 *
 * 接入步骤：
 * 1、创建AdSlot对象
 * 2、创建TTAdNative对象
 * 3、创建加载、展示监听器
 * 4、加载广告
 * 5、加载并渲染成功后，展示广告
 * 6、在onDestroy中销毁广告
 */
public class MediationSplashActivity extends Activity {

    private static final String TAG = "MediationSplashActivity";

    private FrameLayout mSplashContainer;
    private TextView mCountdownTextView; // 倒计时控件

    private CSJSplashAd mCsjSplashAd;

    private TTAdNative.CSJSplashAdListener mCSJSplashAdListener;

    private CSJSplashAd.SplashAdListener mCSJSplashInteractionListener;
    
    // 超时处理
    private Handler mTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimeoutRunnable;
    private boolean isAdLoaded = false;
    
    // 倒计时处理
    private Handler mCountdownHandler = new Handler(Looper.getMainLooper());
    private Runnable mCountdownRunnable;
    private int mCountdownSeconds = 6; // 倒计时秒数
    private boolean isCountdownRunning = false; // 倒计时是否在运行

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: 创建开屏广告页面（优化版本）");
        setContentView(R.layout.mediation_activity_splash);
        mSplashContainer = findViewById(R.id.fl_content);
        mCountdownTextView = findViewById(R.id.tv_countdown);

        // 隐藏倒计时控件，避免遮挡开屏广告的跳过按钮
        if (mCountdownTextView != null) {
            mCountdownTextView.setVisibility(View.GONE);
            Log.d(TAG, "已隐藏倒计时控件，避免遮挡广告跳过按钮");
        }

        // 检查网络状态
        checkNetworkStatus();
        
        // 检查本地配置支持情况
        checkLocalConfigSupport();

        // 启动倒计时（保持逻辑，但不显示UI）
        startCountdown();
        
        // 加载并展示广告
        loadAndShowSplashAd();
    }

    /**
     * 检查网络状态
     */
    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        
        Log.d(TAG, "🌐 网络状态检查:");
        Log.d(TAG, "   连接状态: " + (isConnected ? "已连接" : "未连接"));
        if (networkInfo != null) {
            Log.d(TAG, "   网络类型: " + networkInfo.getTypeName());
        }
        
        if (!isConnected) {
            Log.w(TAG, "⚠️ 网络未连接，将依赖本地配置和兜底机制");
            if (mCountdownTextView != null) {
                mCountdownTextView.setText("网络异常，使用离线配置");
                // 保持隐藏状态，不显示网络异常提示避免遮挡广告
                mCountdownTextView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 检查本地配置支持情况
     */
    private void checkLocalConfigSupport() {
        boolean localConfigSupported = TTAdManagerHolder.isLocalConfigSupported();
        boolean fallbackSupported = TTAdManagerHolder.isFallbackSupported();
        
        Log.d(TAG, "🔧 配置兜底支持情况:");
        Log.d(TAG, "   本地配置支持: " + (localConfigSupported ? "✅ 支持" : "❌ 不支持"));
        Log.d(TAG, "   自定义兜底支持: " + (fallbackSupported ? "✅ 支持" : "❌ 不支持"));
        
        if (localConfigSupported) {
            Log.d(TAG, "   本地配置文件: " + TTAdManagerHolder.getLocalConfigFilePath());
        }
        if (fallbackSupported) {
            Log.d(TAG, "   兜底代码位: " + TTAdManagerHolder.getSplashFallbackCodeId());
        }
        
        if (!localConfigSupported && !fallbackSupported) {
            Log.i(TAG, "📢 当前为标准SDK，使用基础错误处理（功能正常）");
        }
    }

    private void loadAndShowSplashAd() {
        /** 1、创建AdSlot对象 */
        String splashMediaId = getResources().getString(R.string.splash_media_id);
        Log.d(TAG, "loadAndShowSplashAd: 开始加载开屏广告，广告位ID: " + splashMediaId);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(splashMediaId)
                .setImageAcceptedSize(UIUtils.getScreenWidthInPx(this),UIUtils.getScreenHeightInPx(this))
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */
        if (adNativeLoader != null) {
            // 设置超时处理
            startTimeoutHandler();
            // 增加广告加载超时时间从3.5秒到6秒，给广告更多加载机会
            Log.d(TAG, "loadAndShowSplashAd: 开始加载广告，超时设置6000ms（增加到6秒）");
            adNativeLoader.loadSplashAd(adSlot, mCSJSplashAdListener, 6000);
        } else {
            Log.e(TAG, "loadAndShowSplashAd: adNativeLoader为空，无法加载广告");
            jumpToCatDogActivity("广告加载器为空");
        }
    }

    private void initListeners() {
        // 广告加载监听器

        this.mCSJSplashAdListener = new TTAdNative.CSJSplashAdListener() {
            @Override

            public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
                /** 5、渲染成功后，展示广告 */
                Log.d(TAG, "广告渲染成功，开始展示");
                isAdLoaded = true;
                cancelTimeoutHandler();
                
                // 广告成功加载，停止倒计时
                stopCountdown();
                if (mCountdownTextView != null) {
                    //mCountdownTextView.setText("广告中");
                    // 保持隐藏状态，不显示倒计时避免遮挡广告跳过按钮
                    mCountdownTextView.setVisibility(View.GONE);
                }
                
                mCsjSplashAd = csjSplashAd;
                csjSplashAd.setSplashAdListener(mCSJSplashInteractionListener);
                View splashView = csjSplashAd.getSplashView();
                UIUtils.removeFromParent(splashView);
                mSplashContainer.removeAllViews();
                mSplashContainer.addView(splashView);
            }

            public void onSplashLoadSuccess() {
                Log.d(TAG, "splash load success");
            }

            @Override

            public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {

            }

            @Override

            public void onSplashLoadFail(CSJAdError csjAdError) {
                Log.e(TAG, "❌ 开屏广告加载失败详情:");
                Log.e(TAG, "   错误代码: " + csjAdError.getCode());
                Log.e(TAG, "   错误信息: " + csjAdError.getMsg());
                
                // 分析错误原因并记录
                analyzeAdLoadError(csjAdError);
                
                // 更新倒计时显示
                if (mCountdownTextView != null) {
                    mCountdownTextView.setText("广告加载失败，使用兜底配置");
                }
                
                // 检查是否有兜底机制可用
                if (TTAdManagerHolder.isFallbackSupported()) {
                    Log.i(TAG, "🛡️ 尝试使用自定义兜底机制");
                } else {
                    Log.w(TAG, "⚠️ 无兜底机制可用，等待超时处理");
                }
                
                // 继续等待6秒超时机制处理，确保开屏页面展示时间
                Log.d(TAG, "⏰ 广告加载失败，继续等待超时机制处理跳转");
            }

            @Override

            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
                Log.d(TAG, "广告渲染失败, 错误代码: " + csjAdError.getCode() + ", 错误信息: " + csjAdError.getMsg());
                // 注意：广告渲染失败时不立即跳转，让超时机制处理，确保开屏页面展示时间  
                Log.d(TAG, "广告渲染失败，但继续等待6秒超时机制处理跳转，确保足够展示时间");
                // 不调用 jumpToCatDogActivity，让6秒超时机制处理
            }
        };
        // 广告展示监听器

        this.mCSJSplashInteractionListener = new CSJSplashAd.SplashAdListener() {
            @Override

            public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                Log.d(TAG, "splash show");
            }

            @Override

            public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                Log.d(TAG, "splash click");
            }

            @Override

            public void onSplashAdClose(CSJSplashAd csjSplashAd, int closeType) {

                if (closeType == CSJSplashCloseType.CLICK_SKIP) {
                    Log.d(TAG, "开屏广告点击跳过");

                } else if (closeType == CSJSplashCloseType.COUNT_DOWN_OVER) {
                    Log.d(TAG, "开屏广告点击倒计时结束");

                } else if (closeType == CSJSplashCloseType.CLICK_JUMP) {
                    Log.d(TAG, "点击跳转");
                }
                
                // 跳转到Cat&Dog展示页面
                jumpToCatDogActivity("广告正常结束");
            }
        };
    }

    /**
     * 分析广告加载错误原因
     * @param error 错误信息
     */
    private void analyzeAdLoadError(CSJAdError error) {
        int errorCode = error.getCode();
        String errorMsg = error.getMsg();
        
        Log.d(TAG, "📊 广告错误分析:");
        
        // 常见错误代码分析
        switch (errorCode) {
            case 40001:
                Log.d(TAG, "   原因: 配置拉取失败（网络问题或服务器异常）");
                Log.d(TAG, "   建议: 本地配置和兜底机制将发挥作用");
                break;
            case 40002:
                Log.d(TAG, "   原因: 广告位配置错误");
                Log.d(TAG, "   建议: 检查广告位ID是否正确");
                break;
            case 40003:
                Log.d(TAG, "   原因: 无广告填充");
                Log.d(TAG, "   建议: 正常现象，兜底机制处理");
                break;
            case 20005:
                Log.d(TAG, "   原因: 全部代码位请求失败（聚合广告位无可用资源）");
                Log.d(TAG, "   说明: 所有聚合的广告平台都无法返回广告");
                Log.d(TAG, "   建议: 正常现象，超时机制保证用户体验");
                break;
            case -8:
                Log.d(TAG, "   原因: 网络超时");
                Log.d(TAG, "   建议: 本地配置将提供保障");
                break;
            default:
                Log.d(TAG, "   原因: 其他错误 (" + errorCode + ")");
                Log.d(TAG, "   描述: " + errorMsg);
                break;
        }
        
        // 检查网络状态
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        Log.d(TAG, "   当前网络: " + (isConnected ? "正常" : "异常"));
        
        // 兜底机制状态
        Log.d(TAG, "   本地配置: " + (TTAdManagerHolder.isLocalConfigSupported() ? "可用" : "不可用"));
        Log.d(TAG, "   自定义兜底: " + (TTAdManagerHolder.isFallbackSupported() ? "可用" : "不可用"));
    }

    /**
     * 启动倒计时
     */
    private void startCountdown() {
        if (isCountdownRunning) {
            return; // 防止重复启动
        }
        
        isCountdownRunning = true;
        mCountdownSeconds = 6; // 重置倒计时为6秒
        updateCountdownDisplay();
        
        mCountdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCountdownSeconds > 0 && !isFinishing()) {
                    mCountdownSeconds--;
                    updateCountdownDisplay();
                    
                    if (mCountdownSeconds > 0) {
                        // 继续倒计时
                        mCountdownHandler.postDelayed(this, 1000);
                    } else {
                        // 倒计时结束
                        Log.d(TAG, "倒计时结束，准备跳转");
                        isCountdownRunning = false;
                    }
                } else {
                    isCountdownRunning = false;
                }
            }
        };
        
        // 1秒后开始第一次倒计时更新
        mCountdownHandler.postDelayed(mCountdownRunnable, 1000);
    }
    
    /**
     * 停止倒计时
     */
    private void stopCountdown() {
        if (mCountdownHandler != null && mCountdownRunnable != null) {
            mCountdownHandler.removeCallbacks(mCountdownRunnable);
            isCountdownRunning = false;
        }
    }
    
    /**
     * 更新倒计时显示
     */
    private void updateCountdownDisplay() {
        if (mCountdownTextView != null) {
            if (mCountdownSeconds > 0) {
                mCountdownTextView.setText(mCountdownSeconds + "s");
                // 保持隐藏状态，不显示倒计时避免遮挡广告跳过按钮
                // mCountdownTextView.setVisibility(View.VISIBLE);
            } else {
                mCountdownTextView.setText("跳转中...");
            }
            // 确保倒计时始终隐藏
            mCountdownTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 开始超时处理
     */
    private void startTimeoutHandler() {
        mTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdLoaded && !isFinishing()) {
                    Log.d(TAG, "开屏页面6秒展示完成，超时跳转");
                    jumpToCatDogActivity("开屏展示超时");
                }
            }
        };
        // 设置6秒超时，确保开屏页面至少展示6秒（无论广告成功还是失败）
        mTimeoutHandler.postDelayed(mTimeoutRunnable, 6000);
    }
    
    /**
     * 取消超时处理
     */
    private void cancelTimeoutHandler() {
        if (mTimeoutHandler != null && mTimeoutRunnable != null) {
            mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
        }
    }

    /**
     * 统一的跳转方法，确保最小展示时间
     * @param reason 跳转原因
     */
    private void jumpToCatDogActivity(String reason) {
        if (isFinishing()) {
            return; // 防止重复跳转
        }
        
        // 停止倒计时
        stopCountdown();
        
        Log.d(TAG, reason + "，立即跳转到CatDogActivity");
        Intent intent = new Intent(MediationSplashActivity.this, CatDogActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理超时处理器
        cancelTimeoutHandler();
        // 清理倒计时处理器
        stopCountdown();
        
        /** 6、在onDestroy中销毁广告 */
        if (mCsjSplashAd != null && mCsjSplashAd.getMediationManager() != null) {
            mCsjSplashAd.getMediationManager().destroy();
        }
    }
}

