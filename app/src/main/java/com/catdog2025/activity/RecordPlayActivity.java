package com.catdog2025.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.catdog2025.R;
import com.catdog2025.utils.PermissionManager;
import com.catdog2025.utils.PermissionDialogHelper;
import java.io.File;
import java.io.IOException;

/**
 * 录音播放Activity
 * 录制用户人声，播放对应的动物叫声
 */
public class RecordPlayActivity extends Activity {
    private static final String TAG = "RecordPlayActivity";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final String AUDIO_FILE_NAME = "user_voice.3gp"; // 固定文件名
    
    // UI组件
    private ImageView mPetImageView;
    private TextView mPetNameTextView;
    private TextView mRecordTimeTextView;
    private Button mRecordButton;
    private Button mPlayButton;
    private Button mBackButton;
    
    // 宠物类型
    private String mPetType; // "cat" 或 "dog"
    
    // 录音相关
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private String mAudioFilePath;
    private boolean mIsRecording = false;
    private boolean mIsPlaying = false;
    
    // 计时相关
    private Handler mTimeHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimeRunnable;
    private int mRecordSeconds = 0; // 录音时长（秒）

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play);
        
        // 获取宠物类型
        Intent intent = getIntent();
        mPetType = intent.getStringExtra("pet_type");
        if (mPetType == null) {
            mPetType = "cat"; // 默认为cat
        }
        
        Log.d(TAG, "onCreate: 录音播放界面启动，宠物类型: " + mPetType);
        
        // 初始化视图
        initViews();
        
        // 检查录音权限
        checkPermissions();
        
        // 初始化音频文件路径
        initAudioPath();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        mPetImageView = findViewById(R.id.iv_pet);
        mPetNameTextView = findViewById(R.id.tv_pet_name);
        mRecordTimeTextView = findViewById(R.id.tv_record_time);
        mRecordButton = findViewById(R.id.btn_record);
        mPlayButton = findViewById(R.id.btn_play);
        mBackButton = findViewById(R.id.btn_back);
        
        // 根据宠物类型设置UI
        if ("cat".equals(mPetType)) {
            mPetImageView.setImageResource(R.drawable.cat);
            mPetNameTextView.setText("🐱 猫咪沟通器");
        } else {
            mPetImageView.setImageResource(R.drawable.dog);
            mPetNameTextView.setText("🐶 狗狗沟通器");
        }
        
        // 设置按钮点击事件
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });
        
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlaying();
            }
        });
        
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 初始状态
        updateUI();
        
        Log.d(TAG, "initViews: 界面初始化完成");
    }
    
    /**
     * 检查录音相关权限
     */
    private void checkPermissions() {
        Log.d(TAG, "开始检查录音相关权限");
        
        // 检查录音权限
        if (!PermissionManager.hasRecordAudioPermission(this)) {
            Log.d(TAG, "缺少录音权限，申请中...");
            PermissionManager.requestRecordAudioPermissionWithDialog(this);
            return;
        }
        
        // 检查存储权限（用于保存录音文件）
        if (!PermissionManager.hasStoragePermission(this)) {
            Log.d(TAG, "缺少存储权限，申请中...");
            PermissionManager.requestStoragePermissionWithDialog(this);
            return;
        }
        
        Log.d(TAG, "所有录音相关权限检查完成");
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        Log.d(TAG, "权限申请结果 - requestCode: " + requestCode + ", 权限数量: " + permissions.length);
        
        boolean allGranted = PermissionManager.isAllPermissionsGranted(grantResults);
        
        switch (requestCode) {
            case PermissionManager.REQUEST_CODE_RECORD_AUDIO:
                if (allGranted) {
                    Log.d(TAG, "录音权限已授予");
                    Toast.makeText(this, "录音权限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "录音权限被拒绝");
                    // 权限被拒绝，显示引导对话框
                    PermissionDialogHelper.showPermissionDeniedDialog(this, "录音", 
                            PermissionDialogHelper.getPermissionDeniedReason(Manifest.permission.RECORD_AUDIO));
                }
                // 继续检查其他权限
                checkPermissions();
                break;
                
            case PermissionManager.REQUEST_CODE_STORAGE:
                if (allGranted) {
                    Log.d(TAG, "存储权限已授予");
                    Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "存储权限被拒绝");
                    // 权限被拒绝，显示引导对话框
                    PermissionDialogHelper.showPermissionDeniedDialog(this, "存储", 
                            PermissionDialogHelper.getPermissionDeniedReason(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                }
                // 继续检查其他权限
                checkPermissions();
                break;
                
            case PERMISSION_REQUEST_CODE:
                // 兼容旧的权限申请码
                if (allGranted) {
                    Toast.makeText(this, "权限已获取", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "需要相关权限才能使用录音功能", Toast.LENGTH_LONG).show();
                }
                break;
                
            default:
                Log.w(TAG, "未知的权限申请码: " + requestCode);
                break;
        }
    }
    
    /**
     * 初始化音频文件路径（固定文件名）
     */
    private void initAudioPath() {
        File cacheDir = getCacheDir();
        mAudioFilePath = cacheDir.getAbsolutePath() + "/" + AUDIO_FILE_NAME;
        Log.d(TAG, "录音文件路径: " + mAudioFilePath);
    }
    
    /**
     * 切换录音状态
     */
    private void toggleRecording() {
        if (mIsRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }
    
    /**
     * 开始录音
     */
    private void startRecording() {
        // 检查录音权限
        if (!PermissionManager.hasRecordAudioPermission(this)) {
            PermissionManager.requestRecordAudioPermissionWithDialog(this);
            return;
        }
        
        // 检查存储权限
        if (!PermissionManager.hasStoragePermission(this)) {
            PermissionManager.requestStoragePermissionWithDialog(this);
            return;
        }
        
        try {
            // 删除旧的录音文件（固定文件名，覆盖保存）
            File audioFile = new File(mAudioFilePath);
            if (audioFile.exists()) {
                audioFile.delete();
                Log.d(TAG, "删除旧录音文件");
            }
            
            // 配置MediaRecorder
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(mAudioFilePath);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            
            mIsRecording = true;
            mRecordSeconds = 0;
            
            startTimeCounter();
            updateUI();
            
            Log.d(TAG, "开始录音");
            //Toast.makeText(this, "开始录制你的声音，准备与宠物沟通...", Toast.LENGTH_SHORT).show();
            
        } catch (IOException e) {
            Log.e(TAG, "录音失败: " + e.getMessage());
            Toast.makeText(this, "录音失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 停止录音
     */
    private void stopRecording() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                
                mIsRecording = false;
                stopTimeCounter();
                updateUI();
                
                Log.d(TAG, "录音结束，时长: " + mRecordSeconds + "秒");
                //Toast.makeText(this, "录音完成(" + mRecordSeconds + "秒)，现在可以播放给宠物听了！", Toast.LENGTH_LONG).show();
                
            } catch (Exception e) {
                Log.e(TAG, "停止录音失败: " + e.getMessage());
                mIsRecording = false;
                updateUI();
            }
        }
    }
    
    /**
     * 切换播放状态
     */
    private void togglePlaying() {
        if (mIsPlaying) {
            stopPlaying();
        } else {
            startPlaying();
        }
    }
    
    /**
     * 开始播放动物叫声（根据录音时长播放对应时长的cat.mp3或dog.mp3）
     */
    private void startPlaying() {
        // 检查是否有录音记录
        if (mRecordSeconds <= 0) {
            Toast.makeText(this, "请先录音", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // 根据宠物类型选择音频资源
            int audioResId;
            String animalName;
            if ("cat".equals(mPetType)) {
                audioResId = R.raw.cat;
                animalName = "猫咪";
            } else {
                audioResId = R.raw.dog;
                animalName = "狗狗";
            }
            
            mMediaPlayer = MediaPlayer.create(this, audioResId);
            if (mMediaPlayer == null) {
                Toast.makeText(this, "音频文件加载失败", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 设置播放完成监听器
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
            
            mMediaPlayer.start();
            mIsPlaying = true;
            updateUI();
            
            Log.d(TAG, "开始播放" + animalName + "叫声，原录音时长: " + mRecordSeconds + "秒");
            //Toast.makeText(this, "正在播放" + animalName + "叫声给宠物听...", Toast.LENGTH_SHORT).show();
            
            // 根据录音时长控制播放时长
            if (mRecordSeconds > 0) {
                mTimeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsPlaying) {
                            stopPlaying();
                            Toast.makeText(RecordPlayActivity.this, 
                                "播放完成(" + mRecordSeconds + "秒)，观察宠物反应吧！", Toast.LENGTH_LONG).show();
                        }
                    }
                }, mRecordSeconds * 1000); // 按录音时长停止播放
            }
            
        } catch (Exception e) {
            Log.e(TAG, "播放失败: " + e.getMessage());
            Toast.makeText(this, "播放失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 停止播放
     */
    private void stopPlaying() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
            
            mIsPlaying = false;
            updateUI();
            
            Log.d(TAG, "停止播放");
        }
    }
    
    /**
     * 开始计时
     */
    private void startTimeCounter() {
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mIsRecording) {
                    mRecordSeconds++;
                    mRecordTimeTextView.setText(formatTime(mRecordSeconds));
                    mTimeHandler.postDelayed(this, 1000);
                }
            }
        };
        mTimeHandler.postDelayed(mTimeRunnable, 1000);
    }
    
    /**
     * 停止计时
     */
    private void stopTimeCounter() {
        if (mTimeHandler != null && mTimeRunnable != null) {
            mTimeHandler.removeCallbacks(mTimeRunnable);
        }
    }
    
    /**
     * 格式化时间显示
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    /**
     * 更新UI状态
     */
    private void updateUI() {
        if (mIsRecording) {
            mRecordButton.setText("停止录音");
            mRecordButton.setEnabled(true);
            mPlayButton.setEnabled(false);
        } else if (mIsPlaying) {
            mRecordButton.setEnabled(false);
            mPlayButton.setText("停止播放");
            mPlayButton.setEnabled(true);
        } else {
            mRecordButton.setText("录制人声");
            mRecordButton.setEnabled(true);
            
            // 检查是否有录音记录
            if (mRecordSeconds > 0) {
                String petType = "cat".equals(mPetType) ? "猫咪" : "狗狗";
                mPlayButton.setText("播放给" + petType);
                mPlayButton.setEnabled(true);
            } else {
                String petType = "cat".equals(mPetType) ? "猫咪" : "狗狗";
                mPlayButton.setText("播放给" + petType);
                mPlayButton.setEnabled(false);
            }
        }
        
        // 显示录音时间
        if (!mIsRecording && mRecordSeconds > 0) {
            mRecordTimeTextView.setText("录音时长: " + formatTime(mRecordSeconds));
        } else if (!mIsRecording) {
            mRecordTimeTextView.setText("00:00");
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 清理资源
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        
        stopTimeCounter();
        
        Log.d(TAG, "onDestroy: 录音播放页面销毁");
    }
}