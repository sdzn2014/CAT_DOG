# 标准SDK迁移到融合SDK详细指南

## ⚠️ 重要提醒
**迁移前务必备份整个项目！** 融合SDK的API与标准SDK有差异，需要代码调整。

## 一、迁移前准备

### 1. 项目备份
```bash
# 在项目根目录执行
cd ..
cp -r CATDOG2025 CATDOG2025_backup
```

### 2. 当前项目分析
你的项目使用：
- **标准SDK**：`open_ad_sdk.aar` (12MB)
- **应用ID**：5713518
- **广告位**：开屏(103540236)、激励(103540351)、Cat Banner(103539146)、Dog Banner(103539680)

## 二、融合SDK文件替换

### 1. 替换核心SDK
- **删除**：`app/libs/open_ad_sdk.aar`
- **添加**：融合SDK中的主包AAR文件（通常名为`gromore_xxxxxx.aar`）

### 2. 确认适配器文件
保留现有的适配器文件（已经配置好的）：
```
app/libs/adapter/
├── mediation_admob_adapter_17.2.0.67.aar
├── mediation_baidu_adapter_9.3905.2.aar
├── mediation_gdt_adapter_4.640.1510.1.aar
├── mediation_mintegral_adapter_16.6.57.10.aar
├── mediation_sigmob_adapter_4.22.2.1.aar
└── mediation_unity_adapter_4.3.0.34.aar
```

### 3. 更新ADN SDK版本（如需要）
根据融合SDK要求，可能需要更新第三方ADN SDK版本。

## 三、build.gradle修改

### 1. 更新SDK依赖
将：
```gradle
implementation(name: "open_ad_sdk", ext: 'aar')
```

改为：
```gradle
implementation(name: "gromore_核心包名称", ext: 'aar')  // 替换为实际的融合SDK包名
```

### 2. 添加融合SDK特有依赖
```gradle
dependencies {
    // 融合SDK核心包
    implementation(name: "gromore_核心包名称", ext: 'aar')
    
    // 可能需要额外的依赖
    implementation 'com.bytedance.adsdk:mediation:你的版本号'
    
    // 保持现有的其他依赖...
}
```

## 四、代码API迁移

### 1. 导入包更改
**标准SDK导入：**
```java
import com.bytedance.sdk.openadsdk.*;
```

**融合SDK导入：**
```java
import com.bytedance.adsdk.mediation.*;
```

### 2. TTAdManagerHolder.java修改

#### 2.1 更新导入
```java
// 新增融合SDK导入
import com.bytedance.adsdk.mediation.MediationAdManager;
import com.bytedance.adsdk.mediation.MediationConfig;
import com.bytedance.adsdk.mediation.MediationConfigUserInfoForSegment;
import com.bytedance.adsdk.mediation.MediationCustomController;
import com.bytedance.adsdk.mediation.MediationPrivacyConfig;
```

#### 2.2 初始化方法修改
将：
```java
TTAdSdk.init(context, buildConfig(context), new TTAdSdk.InitCallback() {
    @Override
    public void success() {
        // 成功回调
    }
    
    @Override
    public void fail(int code, String msg) {
        // 失败回调
    }
});
```

改为：
```java
MediationAdManager.initialize(context, buildMediationConfig(context), new MediationAdManager.InitCallback() {
    @Override
    public void success() {
        // 成功回调
    }
    
    @Override
    public void fail(int code, String msg) {
        // 失败回调
    }
});
```

#### 2.3 配置构建方法
将`buildConfig`方法更新为：
```java
private static MediationConfig buildMediationConfig(Context context) {
    return new MediationConfig.Builder()
            .appId("5713518")
            .appName("宠物翻译器")
            .debug(true)
            .build();
}
```

### 3. 广告加载API更改

#### 3.1 开屏广告（MediationSplashActivity.java）
**标准SDK：**
```java
TTAdNative ttAdNative = TTAdSdk.getAdManager().createAdNative(context);
```

**融合SDK：**
```java
MediationAdNative mediationAdNative = MediationAdManager.get().createAdNative(context);
```

#### 3.2 Banner广告（CatDogActivity.java）
**标准SDK：**
```java
TTAdNative ttAdNative = TTAdSdk.getAdManager().createAdNative(this);
ttAdNative.loadNativeExpressAd(adSlot, listener);
```

**融合SDK：**
```java
MediationAdNative mediationAdNative = MediationAdManager.get().createAdNative(this);
mediationAdNative.loadBannerAd(adSlot, listener);
```

#### 3.3 激励广告
**标准SDK：**
```java
TTAdNative.RewardVideoAdListener listener = new TTAdNative.RewardVideoAdListener() { };
```

**融合SDK：**
```java
MediationRewardVideoAdListener listener = new MediationRewardVideoAdListener() { };
```

## 五、配置文件更新

### 1. 应用ID保持不变
融合SDK使用相同的应用ID：`5713518`

### 2. 广告位ID保持不变
所有广告位ID保持不变：
- 开屏：103540236
- 激励：103540351
- Cat Banner：103539146
- Dog Banner：103539680

### 3. strings.xml无需修改
现有的strings.xml配置保持不变。

## 六、新增功能配置

### 1. 瀑布流配置
融合SDK支持在后台配置瀑布流，提升收益：
- 登录穿山甲后台
- 进入GroMore管理
- 配置广告位的瀑布流策略

### 2. 本地配置支持
```java
// 在buildMediationConfig中添加
.localConfigFilePath("local_ad_config.json")
.enableLocalConfig(true)
```

### 3. 自定义兜底
```java
// 添加开屏兜底配置
.splashFallbackAdSlot(createFallbackAdSlot())
```

## 七、测试验证步骤

### 1. 构建测试
```bash
./gradlew assembleDebug
```

### 2. 功能测试清单
- [ ] SDK初始化成功
- [ ] 开屏广告正常展示
- [ ] Cat Banner广告加载
- [ ] Dog Banner广告加载
- [ ] 激励广告播放和奖励验证
- [ ] 错误处理机制
- [ ] 用户体验流程

### 3. 日志验证
查看日志确认：
- SDK版本显示为融合SDK版本
- 瀑布流请求日志
- 多ADN竞价日志

## 八、可能遇到的问题

### 1. 编译错误
- **包名冲突**：删除旧的导入，使用新的API
- **方法不存在**：参考融合SDK文档更新API调用

### 2. 运行时错误
- **初始化失败**：检查应用ID和配置
- **广告加载失败**：确认广告位ID正确

### 3. 性能问题
- **APK变大**：融合SDK包含更多功能
- **内存占用**：多ADN同时运行

## 九、回滚方案

如果迁移失败，可以快速回滚：
```bash
# 恢复备份
rm -rf CATDOG2025
cp -r CATDOG2025_backup CATDOG2025
```

## 十、迁移完成验证

### 成功标志：
1. ✅ 应用正常启动
2. ✅ 开屏广告展示
3. ✅ Banner广告正常加载
4. ✅ 激励广告功能完整
5. ✅ 日志显示融合SDK版本
6. ✅ 错误处理机制正常

### 收益提升预期：
- 📈 多平台竞价提升填充率
- 📈 瀑布流优化提升eCPM
- 📈 智能算法提升整体收益

---

**注意事项：**
1. 迁移过程建议在测试环境先完成
2. 生产环境迁移建议分阶段进行
3. 密切关注迁移后的数据表现
4. 有问题及时联系穿山甲技术支持 