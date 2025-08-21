# CATDOG2025宠物翻译器 程序设计说明书

## 文档信息

| 项目 | 内容 |
|------|------|
| 软件名称 | CATDOG2025宠物翻译器 |
| 软件版本 | V1.0.0 |
| 开发语言 | Java |
| 开发平台 | Android Studio |
| 文档版本 | V1.0 |
| 编写日期 | 2025年1月 |
| 文档类型 | 程序设计说明书 |

---

## 1. 软件概述

### 1.1 软件名称
CATDOG2025宠物翻译器

### 1.2 软件功能
CATDOG2025是一款创新的宠物沟通辅助应用，旨在帮助宠物主人与猫咪、狗狗进行"翻译式"沟通。软件的核心功能是录制主人的人声，然后播放对应的动物叫声（猫叫声或狗叫声）给宠物听，观察宠物的反应，增进人宠之间的互动体验。

### 1.3 主要特点
- **录音转换功能**：录制用户语音，播放对应动物音效
- **双模式支持**：支持Cat模式（播放猫叫声）和Dog模式（播放狗叫声）
- **简洁用户界面**：采用Material Design设计风格，操作简单直观
- **广告变现机制**：集成激励广告解锁功能，实现商业价值
- **多平台广告聚合**：支持穿山甲等主流广告平台

### 1.4 适用范围
- 宠物主人与猫咪、狗狗的日常互动
- 宠物行为观察和研究
- 宠物娱乐和训练辅助

---

## 2. 运行环境

### 2.1 硬件环境
- **处理器**：ARM或x86架构，最低1.2GHz
- **内存**：最低2GB RAM，推荐4GB以上
- **存储空间**：最低100MB可用空间
- **音频设备**：支持录音的麦克风和音频播放设备
- **网络连接**：支持WiFi或移动数据网络（用于广告加载）

### 2.2 软件环境
- **操作系统**：Android 5.0 (API Level 21) 及以上版本
- **Java虚拟机**：Android Runtime (ART)
- **开发框架**：Android SDK 35
- **编译环境**：Android Gradle Plugin 8.9.0
- **第三方SDK**：穿山甲广告SDK 6.9.1.5

---

## 3. 系统设计概要

### 3.1 系统架构
CATDOG2025采用经典的Android应用架构，基于MVC（Model-View-Controller）设计模式，具体分为以下几个层次：

#### 3.1.1 表现层（Presentation Layer）
- **StartActivity**：应用启动页面，处理应用初始化
- **CatDogActivity**：主界面，提供Cat/Dog模式选择
- **RecordPlayActivity**：录音播放页面，实现核心功能

#### 3.1.2 业务逻辑层（Business Logic Layer）
- **TTAdManagerHolder**：广告管理核心类，处理广告SDK初始化和配置
- **MediaRecorder控制**：音频录制逻辑处理
- **MediaPlayer控制**：音频播放逻辑处理

#### 3.1.3 数据访问层（Data Access Layer）
- **本地文件存储**：用户录音文件存储管理
- **资源文件管理**：动物音效资源管理
- **配置文件管理**：应用配置和广告配置

#### 3.1.4 第三方服务层（External Services Layer）
- **穿山甲广告SDK**：提供开屏广告、激励广告、Banner广告
- **Android系统服务**：音频录制播放、文件系统等

### 3.2 模块划分

#### 3.2.1 启动模块
- **功能**：应用启动、开屏广告展示、SDK初始化
- **核心类**：StartActivity.java
- **依赖**：TTAdManagerHolder.java

#### 3.2.2 主界面模块
- **功能**：Cat/Dog模式选择、激励广告触发
- **核心类**：CatDogActivity.java
- **UI布局**：activity_cat_dog.xml

#### 3.2.3 录音播放模块
- **功能**：音频录制、动物音效播放、时长控制
- **核心类**：RecordPlayActivity.java
- **UI布局**：activity_record_play.xml
- **资源文件**：cat.mp3, dog.mp3

#### 3.2.4 广告管理模块
- **功能**：广告SDK管理、广告加载展示、错误处理
- **核心类**：TTAdManagerHolder.java, MediationSplashActivity.java, MediationRewardActivity.java
- **配置文件**：local_ad_config.json

#### 3.2.5 工具类模块
- **功能**：网络检测、文件管理、UI工具等辅助功能
- **核心类**：NetworkUtils.java, TempFileUtils.java, UIUtils.java等

---

## 4. 详细设计

### 4.1 核心功能设计

#### 4.1.1 录音功能设计
```java
// 录音核心逻辑
private void startRecording() {
    // 初始化MediaRecorder
    // 设置音频源、输出格式、编码格式
    // 设置输出文件路径：user_voice.3gp
    // 开始录制
}

private void stopRecording() {
    // 停止录制
    // 释放MediaRecorder资源
    // 记录录音时长
}
```

#### 4.1.2 播放功能设计
```java
// 播放动物音效
private void playAnimalSound(String mode) {
    // 根据mode加载对应音效文件
    // Cat模式：播放cat.mp3
    // Dog模式：播放dog.mp3
    // 播放时长与录音时长保持一致
}
```

#### 4.1.3 广告集成设计
```java
// 激励广告展示
private void showRewardAd() {
    // 检查广告是否准备就绪
    // 展示激励广告
    // 处理广告回调
    // 验证奖励后解锁录音功能
}
```

### 4.2 数据流设计

#### 4.2.1 用户操作流程
1. 用户启动应用 → 开屏广告展示
2. 进入主界面 → 选择Cat或Dog模式
3. 点击选择 → 触发激励广告
4. 观看广告完成 → 进入录音页面
5. 录制人声 → 播放对应动物叫声
6. 完成互动 → 返回主界面

#### 4.2.2 数据存储设计
- **录音文件**：统一保存为user_voice.3gp，覆盖模式节省空间
- **配置数据**：广告配置、应用设置存储在SharedPreferences
- **音效资源**：cat.mp3和dog.mp3存储在assets/raw目录

### 4.3 接口设计

#### 4.3.1 广告SDK接口
```java
// TTAdManagerHolder核心接口
public static void init(Context context)  // SDK初始化
public static boolean isInitSuccess()     // 初始化状态检查
public static String getCurrentSDKVersion() // 获取SDK版本
```

#### 4.3.2 音频处理接口
```java
// 录音接口
interface RecordingCallback {
    void onRecordingStart();
    void onRecordingStop(int duration);
    void onRecordingError(String error);
}

// 播放接口
interface PlaybackCallback {
    void onPlaybackStart();
    void onPlaybackComplete();
    void onPlaybackError(String error);
}
```

---

## 5. 安全性设计

### 5.1 权限管理
- **录音权限**：RECORD_AUDIO，动态申请，确保用户授权
- **存储权限**：WRITE_EXTERNAL_STORAGE，用于音频文件保存
- **网络权限**：INTERNET，用于广告加载

### 5.2 数据安全
- **文件覆盖机制**：录音文件采用固定文件名覆盖，避免隐私数据累积
- **广告配置保护**：使用本地配置文件作为兜底，防止网络异常
- **错误处理机制**：完善的异常捕获和用户友好的错误提示

### 5.3 隐私保护
- **最小权限原则**：只申请必要的系统权限
- **数据本地化**：录音数据仅本地存储，不上传服务器
- **临时文件清理**：应用退出时清理临时文件

---

## 6. 性能优化设计

### 6.1 内存优化
- **音频资源管理**：及时释放MediaRecorder和MediaPlayer资源
- **广告缓存**：合理配置广告缓存策略，避免内存泄漏
- **Activity生命周期**：正确处理Activity生命周期，避免内存泄漏

### 6.2 用户体验优化
- **广告超时机制**：设置6秒超时保护，避免长时间等待
- **降级策略**：广告加载失败时提供功能直接解锁的降级方案
- **UI响应优化**：音频处理放在后台线程，保持UI流畅

---

## 7. 测试方案

### 7.1 功能测试
- **录音功能测试**：验证录音启动、停止、时长记录的正确性
- **播放功能测试**：验证动物音效播放的准确性和时长匹配
- **广告功能测试**：验证各类广告的正常加载和展示

### 7.2 兼容性测试
- **Android版本兼容性**：测试Android 5.0-14的兼容性
- **设备兼容性**：测试不同品牌、型号设备的运行情况
- **网络环境测试**：测试WiFi、4G/5G网络环境下的功能稳定性

### 7.3 性能测试
- **内存使用测试**：监控应用内存占用，确保无内存泄漏
- **启动时间测试**：优化应用启动时间，提升用户体验
- **广告加载测试**：测试广告加载速度和成功率

---

## 8. 部署说明

### 8.1 编译配置
- **编译版本**：Android API Level 35
- **最低支持版本**：Android API Level 21
- **目标版本**：Android API Level 35
- **签名配置**：使用release签名文件进行正式发布

### 8.2 发布渠道
- **Google Play Store**：主要发布渠道
- **国内应用商店**：小米应用商店、华为应用市场等
- **官方网站**：提供APK直接下载

---

## 9. 维护和更新

### 9.1 版本管理
- **版本号规则**：采用语义化版本号（Major.Minor.Patch）
- **更新策略**：定期发布功能更新和bug修复版本
- **向后兼容性**：确保新版本与旧版本数据的兼容性

### 9.2 用户反馈处理
- **错误日志收集**：集成crash日志收集机制
- **用户反馈渠道**：提供应用内反馈和邮件支持
- **问题跟踪**：建立问题跟踪和解决机制

---

## 10. 技术创新点

### 10.1 创新功能
- **宠物沟通翻译**：首创人声转动物叫声的创新交互模式
- **时长匹配机制**：录音时长与播放时长的精确匹配
- **双模式设计**：Cat/Dog两种模式满足不同宠物需求

### 10.2 技术特色
- **广告变现创新**：激励广告解锁功能的创新商业模式
- **多平台聚合**：支持6大主流广告平台的技术整合
- **用户体验优化**：完善的错误处理和降级机制

---

*本文档作为CATDOG2025宠物翻译器的程序设计说明书，详细描述了软件的技术架构、功能设计和实现方案，为软件著作权登记提供技术支撑文档。* 