package com.catdog2025.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.catdog2025.R;
import com.catdog2025.config.TTAdManagerHolder;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";
    
    // 最小启动页面展示时间，确保用户体验
    private static final int MIN_SPLASH_TIME = 3000; // 1秒
    // SDK初始化最大等待时间
    private static final int MAX_INIT_WAIT_TIME = 5000; // 3秒
    
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private long mStartTime;
    private boolean mHasJumped = false; // 防止重复跳转

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mStartTime = System.currentTimeMillis();//获取当前时间
//        Log.d(TAG, "onCreate: 启动StartActivity，开始时间: " + mStartTime);
        setContentView(R.layout.activity_mediation_start);//设置布局

        // 检查SDK状态并优化启动流程
        initializeAndStartSDK();
    }
    
    /**
     * 初始化并启动SDK的优化流程
     */
    private void initializeAndStartSDK() {
        if (TTAdSdk.isSdkReady()) {
            Log.d(TAG, "SDK已准备就绪，延迟启动开屏广告确保稳定性");
            // 即使SDK已准备就绪，也要等待最小启动时间以确保用户体验
            scheduleJumpToSplash(MIN_SPLASH_TIME);
            return;
        }
        
        Log.d(TAG, "SDK未准备就绪，开始初始化流程");
        
        // 先初始化SDK
        TTAdManagerHolder.init(this.getApplicationContext());
        
        // 启动SDK并处理回调
        TTAdManagerHolder.start(this);
        
        // 设置最大等待时间保底机制
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mHasJumped) {
                    Log.w(TAG, "SDK初始化超时，强制跳转到开屏广告页面");
                    jumpToSplashActivity("SDK初始化超时");
                }
            }
        }, MAX_INIT_WAIT_TIME);
    }
    
    /**
     * 计划跳转到开屏广告，确保最小展示时间
     */
    private void scheduleJumpToSplash(long minDelayTime) {
        long elapsedTime = System.currentTimeMillis() - mStartTime;
        long delayTime = Math.max(0, minDelayTime - elapsedTime);
        
        Log.d(TAG, "计划在 " + delayTime + "ms 后跳转到开屏广告");
        
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpToSplashActivity("正常启动流程");
            }
        }, delayTime);
    }
    
    /**
     * 统一的跳转方法，防止重复跳转
     */
    private void jumpToSplashActivity(String reason) {
        if (mHasJumped || isFinishing()) {
            Log.d(TAG, "已经跳转过或Activity正在结束，忽略跳转请求");
            return;
        }
        
        mHasJumped = true;
        long totalTime = System.currentTimeMillis() - mStartTime;
        Log.d(TAG, reason + "，跳转到开屏广告页面，总启动时间: " + totalTime + "ms");
        
        // 使用TTAdManagerHolder的跳转方法确保一致性
        TTAdManagerHolder.startActivity(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理Handler防止内存泄漏
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
