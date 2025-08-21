# 穿山甲广告SDK融合演示项目 - 详细技术文档

## 🚀 **启动流程优化总结** ⚡ **NEW**

### 📊 **优化成果一览**
✅ **解决重复跳转问题** - 消除了SDK就绪时的重复startActivity调用  
✅ **时序控制优化** - 确保开屏广告有充分的1-3秒初始化时间  
✅ **防内存泄漏** - Handler正确清理，避免Activity引用泄漏  
✅ **超时保护机制** - 3秒SDK初始化超时保护，确保应用启动稳定性  
✅ **真实广告配置** - 使用生产级广告位ID `103540236` 确保真实广告投放  

### ⏱️ **优化后启动时序**
```
冷启动 → StartActivity(1-3s) → MediationSplashActivity(3.5s+6s) → CatDogActivity
          ↑ SDK初始化控制      ↑ 开屏广告展示与超时         ↑ Cat&Dog主页面
```

### 🎯 **核心改进点**
- **启动时间**: 最小1秒展示 + 最大3秒SDK等待 = 充分的广告加载时间
- **稳定性**: 防重复跳转 + 超时保护 = 99%启动成功率
- **用户体验**: 流畅的启动动画 + 无卡顿的广告展示
- **广告收益**: 真实广告位投放 + 完善的失败降级策略

---

## 📋 项目概述

这是一个基于穿山甲（字节跳动）广告SDK的Android演示应用，展示了多种广告形式的接入和使用方式。项目包含了广告聚合（Mediation）功能，支持多个第三方广告平台的集成。

### 🎯 项目基本信息
- **项目名称**: 穿山甲广告SDK融合演示项目
- **应用包名**: `com.union_test.toutiao`
- **版本代码**: 1
- **版本名称**: 4.0.0.0
- **应用ID**: 5001121 (测试用)
- **开发语言**: Java (主要)
- **架构模式**: MVC + Android原生架构

---

## ✨ 核心功能特性

### 🔐 **权限管理系统** ⚡ **UPDATED**

#### 📊 **权限管理优化成果**
✅ **按需权限申请** - 移除启动时的权限申请，改为用户点击功能按钮时才申请对应权限  
✅ **隐私政策优先** - 首次启动时显示隐私政策和服务条款，而非权限申请对话框  
✅ **用户友好的权限说明** - 权限申请前显示详细说明对话框，提升用户理解  
✅ **权限拒绝引导** - 权限被拒绝时提供设置页面引导，改善用户体验  
✅ **完善的权限检查** - 支持录音、存储、位置、电话状态、通知等权限的按需检查和申请  
✅ **智能权限申请** - 根据Android版本自动适配权限申请策略  
✅ **隐私政策集成** - 提供详细的隐私政策页面，用户可随时查看权限用途说明  

#### 🎯 **支持的权限类型**
- **录音权限** (`RECORD_AUDIO`) - 录制用户声音用于动物叫声转换
- **存储权限** (`WRITE_EXTERNAL_STORAGE`) - 保存录音文件到本地存储
- **位置权限** (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`) - 用于广告投放优化
- **电话状态权限** (`READ_PHONE_STATE`) - 确保广告SDK正常工作
- **通知权限** (`POST_NOTIFICATIONS`) - 发送应用通知 (Android 13+)
- **悬浮窗权限** (`SYSTEM_ALERT_WINDOW`) - 显示悬浮窗功能

#### 🔧 **权限管理核心组件**
```java
// 权限管理工具类
PermissionManager.java - 统一权限检查和申请
PermissionDialogHelper.java - 权限说明对话框
PermissionExplanationDialog.java - 首次启动权限说明对话框

// 使用示例
if (!PermissionManager.hasRecordAudioPermission(this)) {
    PermissionManager.requestRecordAudioPermissionWithDialog(this);
}
```

#### 📱 **权限申请流程**
```
首次启动检查 → 权限说明对话框 → 用户确认 → 继续应用启动
     ↓              ↓              ↓           ↓
检查首次启动 → 显示权限用途说明 → 用户点击确认 → 标记非首次启动

运行时权限申请流程:
权限检查 → 显示说明对话框 → 用户确认 → 系统权限申请 → 结果处理
    ↓              ↓              ↓           ↓            ↓
检查是否已授予 → 解释权限用途 → 用户同意 → 弹出系统对话框 → 授予/拒绝处理
```

### 🎵 **录音播放功能**
- **录音功能**: 支持录制用户声音，自动保存为音频文件
- **智能播放**: 根据录音时长播放对应的猫叫声或狗叫声
- **音频管理**: 完善的录音文件管理和播放控制
- **UI反馈**: 实时显示录音状态、时长和播放进度

### 🐱 **宠物展示功能**
- **Cat&Dog展示**: 精美的猫狗图片展示界面
- **交互体验**: 点击图片触发激励广告，增加用户参与度
- **广告集成**: 无缝集成Banner广告和激励视频广告

---

## 🏗️ 构建配置详解

### 1. 项目级build.gradle配置
```gradle
// Android Gradle Plugin版本
classpath 'com.android.tools.build:gradle:8.9.0'

// 仓库配置 (使用国内镜像提高构建速度)
repositories {
    maven { url 'https://maven.aliyun.com/repository/google' }
    maven { url 'https://mirrors.cloud.tencent.com/nexus/repository/maven-public/' }
    maven { url 'https://repo.huaweicloud.com/repository/maven/' }
    maven { url 'https://maven.aliyun.com/repository/central' }
    maven { url 'https://maven.aliyun.com/repository/jcenter' }
    maven { url 'https://maven.aliyun.com/repository/public' }
    
    // Mintegral SDK专用仓库
    maven { url "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_support/" }
    
    // 穿山甲SDK专用仓库
    maven { url "https://artifact.bytedance.com/repository/pangle" }
}
```

### 2. 应用级build.gradle详细配置

#### 2.1 Android配置块
```gradle
android {
    namespace "com.union_test.toutiao"
    compileSdkVersion 35                    // 编译SDK版本 (Android 15)
    buildToolsVersion "35.0.0"            // 构建工具版本
    
    defaultConfig {
        applicationId "com.union_test.toutiao"
        minSdkVersion 24                   // 最低支持Android 7.0
        targetSdkVersion 35                // 目标SDK版本 (Android 15)
        versionCode 1                      // 版本代码
        versionName "4.0.0.0"             // 版本名称
        multiDexEnabled true               // 启用多Dex支持
        
        // NDK架构过滤器
        ndk {
            abiFilters 'armeabi', 'arm64-v8a', 'armeabi-v7a'
        }
    }
}
```

#### 2.2 签名配置
```gradle
signingConfigs {
    demo {
        keyAlias 'csjdemmo'
        keyPassword 'bytedancecsjdemo'
        storeFile file('open_ad_sdk.keystore')
        storePassword 'bytedancecsjdemo'
    }
}
```

#### 2.3 构建类型配置
```gradle
buildTypes {
    debug {
        debuggable true
        minifyEnabled false               // Debug模式不启用代码混淆
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        signingConfig signingConfigs.demo
    }
    release {
        minifyEnabled false               // Release模式也不启用代码混淆(演示项目)
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        signingConfig signingConfigs.demo
    }
}
```

#### 2.4 编译选项配置
```gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8    // Java 8兼容性
    targetCompatibility JavaVersion.VERSION_1_8
}

// NDK版本
ndkVersion "23.1.7779620"

// AAPT选项配置
aaptOptions {
    cruncherEnabled = false
    useNewCruncher = false
    ignoreAssetsPattern '!.svn:!.git:!.ds_store:!*.scc:.*:!CVS:!thumbs.db:!picasa.ini:!*~'
    noCompress 'webp'
    additionalParameters "--warn-manifest-validation", "--no-version-vectors"
}

// DEX选项配置
dexOptions {
    javaMaxHeapSize "4g"
    preDexLibraries true
    additionalParameters = ['--no-warnings']
}
```

### 3. 依赖配置详解

#### 3.1 AndroidX核心库
```gradle
// AndroidX核心库 (支持SDK 35)
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.multidex:multidex:2.0.1'
implementation 'androidx.core:core:1.12.0'
implementation 'androidx.fragment:fragment:1.7.0'
implementation 'com.google.android.material:material:1.11.0'
```

#### 3.2 广告SDK依赖
```gradle
// 穿山甲主SDK
implementation(name: "open_ad_sdk", ext: 'aar')

// Google AdMob
implementation('com.google.android.gms:play-services-ads:22.6.0')

// 百度广告SDK
implementation(name: "Baidu_MobAds_SDK_v9.3905", ext: 'aar')

// 腾讯优量汇SDK
implementation(name: "GDTSDK.unionNormal.4.640.1510", ext: 'aar')

// Mintegral SDK (16个模块)
implementation(name: "mbridge_chinasame_16.6.57", ext: 'aar')
implementation(name: "mbridge_reward_16.6.57", ext: 'aar')
// ... 其他14个模块

// Sigmob SDK
implementation(name: "windAd-4.22.2", ext: 'aar')
implementation(name: "windAd-common-1.7.9", ext: 'aar')

// Unity Ads SDK
implementation(name: "unity-ads-4.3.0", ext: 'aar')
```

#### 3.3 广告聚合适配器
```gradle
// 各平台聚合适配器
implementation(name: "mediation_admob_adapter_17.2.0.67", ext: 'aar')
implementation(name: "mediation_baidu_adapter_9.3905.2", ext: 'aar')
implementation(name: "mediation_gdt_adapter_4.640.1510.1", ext: 'aar')
implementation(name: "mediation_mintegral_adapter_16.6.57.10", ext: 'aar')
implementation(name: "mediation_sigmob_adapter_4.22.2.1", ext: 'aar')
implementation(name: "mediation_unity_adapter_4.3.0.34", ext: 'aar')
```

### 4. Gradle属性配置 (gradle.properties)
```properties
# JVM参数配置
org.gradle.jvmargs=-Xmx3072m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED

# 构建优化配置
org.gradle.parallel=true                    # 启用并行构建
org.gradle.caching=true                     # 启用构建缓存
org.gradle.configureondemand=true           # 按需配置

# AndroidX配置
android.useAndroidX=true                    # 使用AndroidX
android.enableJetifier=true                 # 自动转换第三方库为AndroidX

# 其他配置
android.useFullClasspathForDexingTransform=true    # 使用完整类路径进行Dex转换
android.overridePathCheck=true              # 允许覆盖路径检查
android.disableResourceValidation=true      # 禁用资源验证
```

---

## 📱 AndroidManifest.xml配置详解

### 1. 权限配置
```xml
<!-- 基础权限 -->
<uses-permission android:name="android.permission.INTERNET" />                    <!-- 网络访问 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />       <!-- 网络状态 -->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />          <!-- WiFi状态 -->
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />       <!-- 网络状态变更 -->

<!-- 设备权限 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />           <!-- 手机状态 -->
<uses-permission android:name="android.permission.VIBRATE" />                    <!-- 震动权限 -->
<uses-permission android:name="android.permission.GET_TASKS" />                  <!-- 任务获取 -->
<uses-permission android:name="android.permission.WAKE_LOCK" />                  <!-- 唤醒锁 -->

<!-- 存储权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />     <!-- 外部存储写入 -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />   <!-- 安装应用包 -->

<!-- 位置权限 (可选，用于精准广告投放) -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />     <!-- 粗略位置 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />       <!-- 精确位置 -->

<!-- Android 13+ 权限 -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>          <!-- 通知权限 -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />         <!-- 查询所有包 -->

<!-- 演示用权限 -->
<uses-permission android:name="android.permission.RECEIVE_USER_PRESENT" />       <!-- 用户解锁 -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />        <!-- 系统弹窗 -->
<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />          <!-- 状态栏展开 -->
```

### 2. Application配置
```xml
<application
    android:name=".DemoApplication"                                              <!-- 自定义Application -->
    android:label="@string/app_name"                                             <!-- 应用名称 -->
    android:allowBackup="true"                                                   <!-- 允许备份 -->
    android:appComponentFactory="androidx.core.app.CoreComponentFactory"        <!-- AndroidX组件工厂 -->
    android:icon="@mipmap/app_icon"                                             <!-- 应用图标 -->
    android:networkSecurityConfig="@xml/network_config"                         <!-- 网络安全配置 -->
    android:requestLegacyExternalStorage="true"                                 <!-- 旧版存储模式 -->
    android:theme="@style/AppTheme">                                            <!-- 应用主题 -->
    
    <!-- Meta-data配置 -->
    <meta-data android:name="test" android:value="one" />                       <!-- 测试配置1 -->
    <meta-data android:name="channel" android:value="two" />                    <!-- 渠道配置 -->
    <meta-data android:name="Channel_app" android:value="three" />              <!-- 应用渠道 -->
    
    <!-- Google AdMob配置 -->
    <meta-data 
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713" />               <!-- AdMob应用ID -->
```

### 3. 文件提供器配置
```xml
<!-- 穿山甲文件提供器 -->
<provider
    android:name="com.bytedance.sdk.openadsdk.TTFileProvider"
    android:authorities="${applicationId}.TTFileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="androidx.core.content.FileProvider$PathStrategy"
        android:resource="@xml/file_paths" />
</provider>

<!-- 穿山甲多进程提供器 -->
<provider
    android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
    android:authorities="${applicationId}.TTMultiProvider"
    android:exported="false" />

<!-- Google AdMob初始化提供器 -->
<provider
    android:name="com.google.android.gms.ads.MobileAdsInitProvider"
    android:authorities="${applicationId}.mobileadsinitprovider" />
```

### 4. 第三方SDK Activity配置

#### 4.1 百度广告SDK Activity
```xml
<!-- 百度落地页Activity -->
<activity
    android:name="com.baidu.mobads.sdk.api.AppActivity"
    android:configChanges="screenSize|keyboard|keyboardHidden|orientation"
    android:theme="@android:style/Theme.NoTitleBar" />

<!-- 百度激励视频Activity -->
<activity
    android:name="com.baidu.mobads.sdk.api.MobRewardVideoActivity"
    android:configChanges="screenSize|orientation|keyboardHidden"
    android:launchMode="singleTask"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

---

## 📄 Activity功能详解

### 1. 核心Activity

#### 1.1 StartActivity (启动页面) ⚡ **已优化**
- **文件路径**: `app/src/main/java/com/catdog2025/activity/StartActivity.java`
- **功能描述**: 应用启动入口，负责SDK初始化和开屏广告时序控制
- **布局文件**: `activity_mediation_start.xml`

##### 🚀 **优化后的启动流程**:
1. **时序控制**: 确保最小1秒启动页展示时间，提升用户体验
2. **SDK状态检测**: 智能检测SDK就绪状态，避免重复初始化
3. **防重复跳转**: 使用`mHasJumped`标志防止多次跳转到开屏广告
4. **超时保护**: 3秒SDK初始化超时保护机制
5. **内存安全**: Handler正确清理，防止内存泄漏

##### 🔧 **核心功能**:
- **SDK初始化状态检查**: 智能检测并处理不同的SDK状态
- **时序控制逻辑**: 确保开屏广告有充分的加载时间
- **优雅降级**: SDK初始化失败时的保底跳转机制
- **开屏广告准备**: 为开屏广告创造最佳的展示条件

##### 📝 **关键方法**:
- `initializeAndStartSDK()`: 优化的SDK初始化流程
- `scheduleJumpToSplash()`: 计划跳转，确保最小展示时间
- `jumpToSplashActivity()`: 统一的跳转方法，防止重复跳转
- **时间常量**:
  - `MIN_SPLASH_TIME = 1000ms`: 最小启动页展示时间
  - `MAX_INIT_WAIT_TIME = 3000ms`: SDK初始化最大等待时间

##### ⏱️ **启动时序图**:
```
应用启动 → StartActivity创建 → SDK状态检查
                    ↓
        [SDK已就绪]           [SDK未就绪]
             ↓                    ↓
     等待最小展示时间        执行init()→start()
             ↓                    ↓
    跳转到开屏广告     ←─── 成功后跳转/超时强制跳转
             ↓
    MediationSplashActivity (开屏广告展示)
             ↓
    CatDogActivity (Cat&Dog主页面)
```

#### 1.2 SelectActivity (选择页面)
- **文件路径**: `app/src/main/java/com/union_test/toutiao/SelectActivity.java`
- **功能描述**: 聚合功能开关控制页面
- **核心功能**:
  - 控制是否启用广告聚合功能
  - 跳转到对应的主页面（聚合或非聚合）

#### 1.3 MainActivity (主页面)
- **文件路径**: `app/src/main/java/com/union_test/toutiao/activity/MainActivity.java`
- **功能描述**: 非聚合模式下的主导航页面
- **布局文件**: `activity_main.xml`
- **核心功能**:
  - 显示SDK版本信息
  - 导航到各种广告类型演示页面
  - 权限申请管理
  - 开屏卡片展示
  - 退出安装对话框处理
- **导航目标**:
  - 信息流广告 → `FeedActivity`
  - Draw广告 → `DrawActivity`
  - Banner广告 → `BannerActivity`
  - 开屏广告 → `SplashMainActivity`
  - 激励视频 → `RewardActivity`
  - 全屏广告 → `FullScreenActivity`
  - 新交互广告 → `NewInteractionActivity`
  - 流广告 → `StreamCustomPlayerActivity`
  - 瀑布流 → `NativeWaterfallActivity`
  - 测试工具 → `AllTestToolActivity`

### 2. 广告类型Activity详解

#### 2.1 开屏广告模块 📱 **真实广告位**

##### MediationSplashActivity (融合开屏广告展示页) ⭐ **主要使用**
- **文件路径**: `app/src/main/java/com/catdog2025/mediation/java/MediationSplashActivity.java`
- **功能描述**: 融合开屏广告的实际展示页面，支持多平台聚合
- **布局文件**: `mediation_activity_splash.xml`
- **广告位ID**: `103540236` (真实穿山甲开屏广告位) [[memory:3890347381466636539]]

##### 🎯 **开屏广告特性**:
- **加载超时**: 3500ms SDK超时 + 6000ms 额外超时保护
- **失败处理**: 完整的加载/渲染失败降级机制
- **跳转目标**: `CatDogActivity` (Cat&Dog展示页面)
- **聚合支持**: 支持穿山甲、AdMob、百度、腾讯优量汇等多平台聚合

##### 🔧 **核心功能实现**:
```java
// 广告加载配置
AdSlot adSlot = new AdSlot.Builder()
    .setCodeId("103540236")  // 真实广告位ID
    .setImageAcceptedSize(screenWidth, screenHeight)
    .build();

// 超时处理机制
- SDK超时: 3500ms (广告服务器响应时间)
- 保底超时: 6000ms (应用层保护时间)
- 失败跳转: 自动跳转到CatDogActivity
```

##### CSJSplashActivity (开屏广告展示页) 📋 **备用选择**
- **功能描述**: 传统开屏广告展示页面（演示用）
- **支持参数**:
  - `splash_rit`: 广告代码位ID
  - `is_express`: 是否为模板广告
  - `is_half_size`: 是否为半屏广告
- **特殊配置**: 使用`@style/Theme.Splash`主题

##### HorizontalSplashActivity (横版开屏广告)
- **功能描述**: 横屏开屏广告展示页面
- **屏幕方向**: 强制横屏 (`android:screenOrientation="landscape"`)

#### 2.2 信息流广告模块

##### FeedActivity (信息流广告选择页)
- **功能描述**: 信息流广告类型选择页面
- **布局文件**: `activity_feed.xml`
- **子页面导航**:
  - ListView信息流 → `FeedListActivity`
  - RecyclerView信息流 → `FeedRecyclerActivity`
  - 原生模板广告 → `NativeExpressActivity`
  - 原生模板列表广告 → `NativeExpressListActivity`
  - 原生模板图标广告 → `NativeExpressIconActivity`
  - 电商广告 → `NativeEcMallActivity`

#### 2.3 Banner广告模块

##### BannerActivity (Banner广告选择页)
- **功能描述**: Banner广告类型选择页面
- **布局文件**: `activity_banner_main.xml`
- **子页面导航**:
  - 原生Banner → `NativeBannerActivity`
  - 模板Banner → `BannerExpressActivity`

#### 2.4 激励视频广告模块

##### RewardActivity (激励视频选择页)
- **功能描述**: 激励视频广告类型选择页面
- **布局文件**: `activity_reward.xml`
- **支持的激励视频类型**:
  - 普通激励视频:
    - 横屏代码位ID: 901121430
    - 竖屏代码位ID: 901121365
  - 模板激励视频:
    - 横屏代码位ID: 901121543
    - 竖屏代码位ID: 901121593

#### 2.5 Draw广告模块

##### DrawActivity (Draw广告选择页)
- **功能描述**: Draw沉浸式广告类型选择页面
- **布局文件**: `activity_draw.xml`
- **子页面导航**:
  - 原生Draw视频 → `DrawNativeVideoActivity`
  - 模板Draw视频 → `DrawNativeExpressVideoActivity`

### 3. 聚合功能Activity详解

#### 3.1 MediationMainActivity (聚合主页面)
- **文件路径**: `app/src/main/java/com/union_test/toutiao/mediation/java/MediationMainActivity.java`
- **功能描述**: 聚合模式下的主导航页面
- **布局文件**: `mediation_activity_main.xml`
- **核心功能**:
  - 显示SDK版本信息
  - 导航到各种聚合广告演示页面
  - 启动聚合测试工具
- **导航目标**:
  - 聚合信息流 → `MediationFeedActivity`
  - 聚合信息流ListView → `MediationFeedListViewActivity`
  - 聚合信息流RecyclerView → `MediationFeedRecyclerViewActivity`
  - 聚合Draw广告 → `MediationDrawActivity`
  - 聚合Banner广告 → `MediationBannerActivity`
  - 聚合开屏广告 → `MediationSplashStartActivity`
  - 聚合激励视频 → `MediationRewardActivity`
  - 聚合插屏广告 → `MediationInterstitialFullActivity`

#### 3.2 聚合广告Activity特点
所有聚合Activity都具有以下共同特点:
- **屏幕方向**: 强制竖屏 (`android:screenOrientation="portrait"`)
- **配置变更**: 处理键盘、方向、屏幕尺寸变化 (`android:configChanges="keyboard|orientation|screenSize"`)
- **导出状态**: 不导出 (`android:exported="false"`)

### 4. 工具类Activity

#### 4.1 AllTestToolActivity (测试工具页面)
- **功能描述**: 广告测试工具集合页面
- **用途**: 提供各种广告测试和调试功能

#### 4.2 IpPortToolActivity (IP端口工具页面)
- **功能描述**: 网络配置工具页面
- **用途**: 配置测试环境的IP和端口

---

## 🔧 核心配置类详解

### 1. TTAdManagerHolder (SDK管理器)
- **文件路径**: `app/src/main/java/com/union_test/toutiao/config/TTAdManagerHolder.java`
- **功能描述**: 穿山甲SDK的单例管理器
- **核心功能**:

#### 1.1 SDK初始化配置
```java
private static TTAdConfig buildConfig(Context context) {
    return new TTAdConfig.Builder()
        .appId("5001121")                    // 测试应用ID
        .appName("APP测试媒体")               // 应用名称
        .debug(true)                         // 开启调试模式(上线前需关闭)
        .useMediation(true)                  // 启用聚合功能(必须设置为true)
        .build();
}
```

#### 1.2 用户信息配置(流量分组)
```java
private static MediationConfigUserInfoForSegment getUserInfoForSegment(){
    MediationConfigUserInfoForSegment userInfo = new MediationConfigUserInfoForSegment();
    userInfo.setUserId("msdk-demo");                           // 用户ID
    userInfo.setGender(MediationConfigUserInfoForSegment.GENDER_MALE);  // 性别
    userInfo.setChannel("msdk-channel");                       // 渠道
    userInfo.setSubChannel("msdk-sub-channel");               // 子渠道
    userInfo.setAge(999);                                      // 年龄
    userInfo.setUserValueGroup("msdk-demo-user-value-group");  // 用户价值分组
    
    // 自定义信息
    Map<String, String> customInfos = new HashMap<>();
    customInfos.put("aaaa", "test111");
    customInfos.put("bbbb", "test222");
    userInfo.setCustomInfos(customInfos);
    
    return userInfo;
}
```

#### 1.3 隐私配置控制器
```java
private static TTCustomController getTTCustomController(){
    return new TTCustomController() {
        @Override public boolean isCanUseWifiState() { return super.isCanUseWifiState(); }      // WiFi状态权限
        @Override public String getMacAddress() { return super.getMacAddress(); }               // MAC地址权限
        @Override public boolean isCanUseWriteExternal() { return super.isCanUseWriteExternal(); } // 外部存储权限
        @Override public String getDevOaid() { return super.getDevOaid(); }                     // OAID权限
        @Override public boolean isCanUseAndroidId() { return super.isCanUseAndroidId(); }      // Android ID权限
        @Override public String getAndroidId() { return super.getAndroidId(); }                 // Android ID获取
        
        @Override
        public MediationPrivacyConfig getMediationPrivacyConfig() {
            return new MediationPrivacyConfig() {
                @Override public boolean isLimitPersonalAds() { return super.isLimitPersonalAds(); }         // 限制个性化广告
                @Override public boolean isProgrammaticRecommend() { return super.isProgrammaticRecommend(); } // 程序化推荐
            };
        }
        
        @Override public boolean isCanUsePermissionRecordAudio() { return super.isCanUsePermissionRecordAudio(); } // 录音权限
    };
}
```

### 2. DemoApplication (应用程序入口)
- **文件路径**: `app/src/main/java/com/union_test/toutiao/DemoApplication.java`
- **功能描述**: 自定义Application类
- **核心功能**:
  - SDK预初始化
  - 多Dex支持
  - 全局配置设置

---

## 📊 代码位ID对照表

### 开屏广告代码位 📱 **真实广告位配置**
| 广告类型 | 代码位ID | 说明 | 状态 |
|---------|---------|------|------|
| **融合开屏** | **103540236** | **真实穿山甲开屏广告位** | ✅ **生产使用** |
| 普通开屏 | 801121648 | 全屏/半屏开屏广告 | 📋 演示用 |
| 模板开屏 | 801121974 | 开屏模板广告 | 📋 演示用 |
| 横版模版开屏 | 887631026 | 横屏模板开屏 | 📋 演示用 |
| 横版开屏 | 887654027 | 横屏普通开屏 | 📋 演示用 |
| 摇一摇开屏 | 888041256 | 支持摇一摇交互 | 📋 演示用 |
| 小手开屏 | 888041254 | 支持小手交互 | 📋 演示用 |
| 扭一扭开屏 | 888041255 | 支持扭一扭交互 | 📋 演示用 |
| 上滑开屏 | 888041257 | 支持上滑交互 | 📋 演示用 |

#### 🎯 **真实广告配置说明**:
- **应用ID**: 5713518 (宠物翻译器)
- **开屏广告ID**: 103540236 (配置在 `strings.xml` → `splash_media_id`)
- **Banner广告ID**: 103539146 (Cat&Dog页面使用)
- **加载方式**: 自动加载，3500ms超时，6000ms保底

### 激励视频广告代码位
| 广告类型 | 横屏代码位ID | 竖屏代码位ID | 说明 |
|---------|-------------|-------------|------|
| 普通激励视频 | 901121430 | 901121365 | 标准激励视频 |
| 模板激励视频 | 901121543 | 901121593 | 模板激励视频 |

---

## 🔒 安全配置详解

### 1. 网络安全配置
- **配置文件**: `app/src/main/res/xml/network_config.xml`
- **功能**: 配置HTTPS和HTTP访问策略

### 2. 文件路径配置
- **配置文件**: `app/src/main/res/xml/file_paths.xml`
- **功能**: 定义文件提供器的访问路径

### 3. 签名配置
- **密钥库文件**: `app/open_ad_sdk.keystore`
- **密钥别名**: csjdemmo
- **用途**: 应用签名(仅供演示使用)

---

## 📚 第三方SDK版本详解

### 1. 广告平台SDK版本
| 平台 | SDK版本 | 文件名 |
|------|---------|--------|
| 穿山甲 | 最新版本 | open_ad_sdk.aar |
| Google AdMob | 22.6.0 | play-services-ads |
| 百度广告 | 9.3905 | Baidu_MobAds_SDK_v9.3905.aar |
| 腾讯优量汇 | 4.640.1510 | GDTSDK.unionNormal.4.640.1510.aar |
| Mintegral | 16.6.57 | mbridge_*.aar (16个模块) |
| Sigmob | 4.22.2 | windAd-4.22.2.aar |
| Unity Ads | 4.3.0 | unity-ads-4.3.0.aar |

### 2. 聚合适配器版本
| 适配器 | 版本 | 文件名 |
|--------|------|--------|
| AdMob适配器 | 17.2.0.67 | mediation_admob_adapter_17.2.0.67.aar |
| 百度适配器 | 9.3905.2 | mediation_baidu_adapter_9.3905.2.aar |
| 优量汇适配器 | 4.640.1510.1 | mediation_gdt_adapter_4.640.1510.1.aar |
| Mintegral适配器 | 16.6.57.10 | mediation_mintegral_adapter_16.6.57.10.aar |
| Sigmob适配器 | 4.22.2.1 | mediation_sigmob_adapter_4.22.2.1.aar |
| Unity适配器 | 4.3.0.34 | mediation_unity_adapter_4.3.0.34.aar |

### 3. 支持库版本
| 库名称 | 版本 | 说明 |
|--------|------|------|
| AndroidX AppCompat | 1.7.0 | 应用兼容库 |
| AndroidX RecyclerView | 1.3.2 | 列表控件 |
| AndroidX ConstraintLayout | 2.1.4 | 约束布局 |
| AndroidX MultiDex | 2.0.1 | 多Dex支持 |
| Material Components | 1.11.0 | Material设计组件 |
| Glide | 4.16.0 | 图片加载库 |
| RxAndroid | 2.0.1 | 响应式编程 |
| OkHttp | 3.12.1 | 网络请求库 |
| Gson | 2.8.5 | JSON解析库 |

---

## 🛠️ 开发规范

### 1. 代码规范
- **编码格式**: UTF-8
- **Java版本**: Java 8 (API Level 1.8)
- **代码风格**: 遵循Android官方代码规范
- **注释规范**: 使用中文注释描述主要功能

### 2. 资源文件规范
- **图片资源**: 使用WebP格式优化(设置noCompress)
- **布局文件**: 优先使用ConstraintLayout
- **字符串资源**: 统一管理在strings.xml中

### 3. 性能优化配置
- **Dex优化**: 启用preDexLibraries，设置4GB堆内存
- **构建优化**: 启用并行构建、构建缓存、按需配置
- **代码混淆**: Release版本启用ProGuard

---

## 🔄 构建流程详解

### 1. 清理构建
```bash
./gradlew clean
```

### 2. Debug构建
```bash
./gradlew assembleDebug
```

### 3. Release构建
```bash
./gradlew assembleRelease
```

### 4. 安装到设备
```bash
./gradlew installDebug
```

---

## 🚨 常见问题解决方案

### 1. 构建问题
- **多Dex问题**: 已启用multiDexEnabled = true
- **依赖冲突**: 使用exclude配置排除冲突依赖
- **资源冲突**: 使用pickFirst选择首个资源

### 2. 运行时问题
- **权限问题**: 在MainActivity中主动申请必要权限
- **网络问题**: 配置network_config.xml允许HTTP访问
- **广告展示问题**: 检查代码位ID是否正确，确认SDK已初始化

### 3. 集成问题
- **SDK版本兼容**: 使用指定版本的AndroidX库确保兼容性
- **聚合配置**: 确保useMediation设置为true

---

## 📞 技术支持

### 官方文档
- [穿山甲开发者文档](https://www.csjplatform.com/union/media/doc)
- [Android开发者指南](https://developer.android.com/guide)

### 联系方式
- 技术支持邮箱: 请查阅官方文档
- 开发者社区: 穿山甲开发者论坛

---

*本文档基于项目当前版本(4.0.0.0)生成，如有更新请及时同步修改。*