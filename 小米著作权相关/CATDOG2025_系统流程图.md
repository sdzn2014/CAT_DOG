# CATDOG2025宠物翻译器 系统流程图

## 文档信息

| 项目 | 内容 |
|------|------|
| 软件名称 | CATDOG2025宠物翻译器 |
| 软件版本 | V1.0.0 |
| 文档类型 | 系统流程图 |
| 文档版本 | V1.0 |
| 编写日期 | 2025年1月 |

---

## 1. 软件总体流程图

### 1.1 应用启动与初始化流程

```mermaid
graph TD
    A[用户启动应用] --> B[StartActivity启动]
    B --> C[TTAdManagerHolder初始化]
    C --> D{SDK初始化成功?}
    D -->|是| E[加载开屏广告]
    D -->|否| F[记录错误日志]
    F --> G[进入主界面]
    E --> H{广告加载成功?}
    H -->|是| I[展示开屏广告]
    H -->|否| J[广告超时保护]
    I --> K[广告展示完成]
    J --> G
    K --> G[进入CatDogActivity主界面]
    G --> L[显示Cat/Dog选择界面]
    
    style A fill:#e1f5fe
    style G fill:#c8e6c9
    style L fill:#fff3e0
```

### 1.2 主界面操作流程

```mermaid
graph TD
    A[主界面CatDogActivity] --> B[显示Cat和Dog图片]
    B --> C[用户点击Cat图片]
    B --> D[用户点击Dog图片]
    
    C --> E[弹出确认对话框]
    D --> F[弹出确认对话框]
    
    E --> G["提示: 观看短视频广告解锁Cat沟通功能"]
    F --> H["提示: 观看短视频广告解锁Dog沟通功能"]
    
    G --> I{用户确认观看广告?}
    H --> J{用户确认观看广告?}
    
    I -->|是| K[加载激励广告]
    I -->|否| L[返回主界面]
    J -->|是| M[加载激励广告]
    J -->|否| L
    
    K --> N{广告加载成功?}
    M --> O{广告加载成功?}
    
    N -->|是| P[展示激励广告]
    N -->|否| Q[降级策略: 直接解锁]
    O -->|是| R[展示激励广告]
    O -->|否| S[降级策略: 直接解锁]
    
    P --> T{用户完整观看广告?}
    R --> U{用户完整观看广告?}
    
    T -->|是| V[奖励验证成功]
    T -->|否| W[广告关闭，返回主界面]
    U -->|是| X[奖励验证成功]
    U -->|否| W
    
    V --> Y[跳转到RecordPlayActivity - Cat模式]
    X --> Z[跳转到RecordPlayActivity - Dog模式]
    Q --> Y
    S --> Z
    W --> L
    
    style A fill:#e1f5fe
    style L fill:#ffcdd2
    style Y fill:#c8e6c9
    style Z fill:#c8e6c9
```

---

## 2. 核心功能流程图

### 2.1 录音播放功能流程

```mermaid
graph TD
    A[进入RecordPlayActivity] --> B{传入模式参数}
    B -->|Cat模式| C[设置界面标题: Cat沟通模式]
    B -->|Dog模式| D[设置界面标题: Dog沟通模式]
    
    C --> E[显示录音控制界面]
    D --> E
    
    E --> F[用户点击录音按钮]
    F --> G{检查录音权限}
    
    G -->|无权限| H[申请录音权限]
    G -->|有权限| I[初始化MediaRecorder]
    
    H --> J{权限申请结果}
    J -->|同意| I
    J -->|拒绝| K[显示权限说明对话框]
    K --> L[返回主界面]
    
    I --> M[开始录音]
    M --> N[显示录音时长]
    N --> O[用户点击停止录音]
    
    O --> P[停止MediaRecorder]
    P --> Q[保存录音文件: user_voice.3gp]
    Q --> R[记录录音时长]
    
    R --> S[用户点击播放按钮]
    S --> T{检查录音文件}
    
    T -->|文件存在| U[初始化MediaPlayer]
    T -->|文件不存在| V[提示用户先录音]
    V --> F
    
    U --> W{判断模式}
    W -->|Cat模式| X[加载cat.mp3音效]
    W -->|Dog模式| Y[加载dog.mp3音效]
    
    X --> Z[播放猫叫声]
    Y --> AA[播放狗叫声]
    
    Z --> BB[按录音时长播放]
    AA --> BB
    
    BB --> CC[播放完成]
    CC --> DD[释放MediaPlayer资源]
    DD --> EE[返回录音界面]
    
    EE --> FF{用户继续操作?}
    FF -->|继续录音| F
    FF -->|返回主界面| GG[清理临时文件]
    GG --> HH[返回CatDogActivity]
    
    style A fill:#e1f5fe
    style M fill:#fff3e0
    style Z fill:#f3e5f5
    style AA fill:#f3e5f5
    style HH fill:#c8e6c9
```

### 2.2 广告管理流程

```mermaid
graph TD
    A[TTAdManagerHolder初始化] --> B[检查网络状态]
    B --> C{网络连接正常?}
    
    C -->|是| D[在线获取广告配置]
    C -->|否| E[加载本地配置文件]
    
    D --> F{配置获取成功?}
    F -->|是| G[缓存广告配置]
    F -->|否| E
    
    E --> H[解析local_ad_config.json]
    G --> I[广告配置就绪]
    H --> I
    
    I --> J[广告请求触发]
    J --> K{广告类型判断}
    
    K -->|开屏广告| L[请求开屏广告103540236]
    K -->|激励广告| M[请求激励广告103540351]
    K -->|Banner广告| N[请求Banner广告]
    
    L --> O{广告加载结果}
    M --> P{广告加载结果}
    N --> Q{广告加载结果}
    
    O -->|成功| R[展示开屏广告]
    O -->|失败| S[超时保护机制]
    
    P -->|成功| T[展示激励广告]
    P -->|失败| U[降级策略: 直接解锁]
    
    Q -->|成功| V[展示Banner广告]
    Q -->|失败| W[隐藏广告位]
    
    R --> X[广告展示完成]
    S --> Y[跳过广告，进入主界面]
    T --> Z{用户观看完成?}
    U --> AA[功能直接可用]
    V --> BB[广告正常展示]
    W --> CC[用户界面正常显示]
    
    Z -->|是| DD[奖励验证通过]
    Z -->|否| EE[返回主界面]
    
    DD --> FF[解锁对应功能]
    EE --> GG[功能保持锁定]
    
    X --> Y
    AA --> FF
    
    style A fill:#e1f5fe
    style I fill:#c8e6c9
    style FF fill:#4caf50
    style GG fill:#ffcdd2
```

---

## 3. 用户交互流程图

### 3.1 完整用户体验流程

```mermaid
graph TD
    A[用户打开应用] --> B[开屏广告展示]
    B --> C[进入主界面]
    C --> D[看到Cat和Dog两个选项]
    
    D --> E{用户选择}
    E -->|选择Cat| F[弹出Cat功能解锁提示]
    E -->|选择Dog| G[弹出Dog功能解锁提示]
    
    F --> H["提示: 观看广告解锁Cat沟通功能"]
    G --> I["提示: 观看广告解锁Dog沟通功能"]
    
    H --> J{用户决定}
    I --> K{用户决定}
    
    J -->|同意观看| L[播放激励广告]
    J -->|拒绝观看| M[返回主界面]
    K -->|同意观看| N[播放激励广告]
    K -->|拒绝观看| M
    
    L --> O{广告观看完整?}
    N --> P{广告观看完整?}
    
    O -->|是| Q[解锁Cat沟通功能]
    O -->|否| M
    P -->|是| R[解锁Dog沟通功能]
    P -->|否| M
    
    Q --> S[进入Cat录音界面]
    R --> T[进入Dog录音界面]
    
    S --> U[用户点击录音按钮]
    T --> U
    
    U --> V[开始录制人声]
    V --> W[用户说话录音]
    W --> X[用户停止录音]
    
    X --> Y[用户点击播放按钮]
    Y --> Z{判断模式}
    
    Z -->|Cat模式| AA[播放猫叫声给猫听]
    Z -->|Dog模式| BB[播放狗叫声给狗听]
    
    AA --> CC[观察宠物反应]
    BB --> CC
    
    CC --> DD{用户继续使用?}
    DD -->|是| UU[继续录音]
    DD -->|否| EE[返回主界面]
    
    UU --> U
    EE --> D
    M --> D
    
    style A fill:#e3f2fd
    style Q fill:#c8e6c9
    style R fill:#c8e6c9
    style AA fill:#f3e5f5
    style BB fill:#f3e5f5
    style CC fill:#fff9c4
```

### 3.2 错误处理流程

```mermaid
graph TD
    A[系统运行中] --> B{遇到错误}
    
    B -->|网络错误| C[网络异常处理]
    B -->|权限错误| D[权限异常处理]
    B -->|广告错误| E[广告异常处理]
    B -->|音频错误| F[音频异常处理]
    
    C --> G[切换到本地配置]
    G --> H[继续功能运行]
    
    D --> I[弹出权限说明对话框]
    I --> J[引导用户手动授权]
    J --> K{用户是否授权?}
    K -->|是| L[功能正常使用]
    K -->|否| M[功能限制使用]
    
    E --> N[记录广告错误]
    N --> O[启动降级策略]
    O --> P[直接解锁功能]
    
    F --> Q[显示音频错误提示]
    Q --> R[重新初始化音频组件]
    R --> S{重新初始化成功?}
    S -->|是| T[功能恢复正常]
    S -->|否| U[建议重启应用]
    
    H --> V[错误处理完成]
    L --> V
    M --> V
    P --> V
    T --> V
    U --> V
    
    V --> W[继续应用运行]
    
    style A fill:#e1f5fe
    style V fill:#c8e6c9
    style W fill:#4caf50
```

---

## 4. 数据流向图

### 4.1 音频数据流向

```mermaid
graph LR
    A[用户语音输入] --> B[麦克风捕获]
    B --> C[MediaRecorder编码]
    C --> D[保存为user_voice.3gp]
    
    E[cat.mp3音效文件] --> F[MediaPlayer加载]
    G[dog.mp3音效文件] --> F
    
    F --> H[音频解码]
    H --> I[扬声器播放]
    I --> J[宠物听到动物叫声]
    
    D --> K[记录录音时长]
    K --> L[控制播放时长]
    L --> H
    
    style A fill:#e3f2fd
    style D fill:#fff3e0
    style J fill:#f3e5f5
```

### 4.2 广告数据流向

```mermaid
graph LR
    A[穿山甲广告服务器] --> B[广告请求]
    B --> C[TTAdManagerHolder]
    C --> D[广告配置管理]
    
    E[local_ad_config.json] --> D
    
    D --> F[开屏广告加载]
    D --> G[激励广告加载]
    D --> H[Banner广告加载]
    
    F --> I[StartActivity展示]
    G --> J[CatDogActivity触发]
    H --> K[主界面显示]
    
    J --> L[用户观看完成]
    L --> M[奖励验证]
    M --> N[功能解锁]
    
    style A fill:#e3f2fd
    style E fill:#fff3e0
    style N fill:#c8e6c9
```

---

## 5. 系统状态转换图

### 5.1 应用状态转换

```mermaid
stateDiagram-v2
    [*] --> 启动状态
    启动状态 --> SDK初始化中
    SDK初始化中 --> 开屏广告中 : 初始化成功
    SDK初始化中 --> 主界面 : 初始化失败
    
    开屏广告中 --> 主界面 : 广告完成/超时
    
    主界面 --> 广告确认中 : 用户点击Cat/Dog
    广告确认中 --> 激励广告中 : 用户同意观看
    广告确认中 --> 主界面 : 用户拒绝观看
    
    激励广告中 --> 录音界面 : 广告观看完成
    激励广告中 --> 主界面 : 广告关闭/失败
    
    录音界面 --> 录音中 : 开始录音
    录音中 --> 录音界面 : 停止录音
    录音界面 --> 播放中 : 开始播放
    播放中 --> 录音界面 : 播放完成
    
    录音界面 --> 主界面 : 返回主界面
    主界面 --> [*] : 退出应用
```

### 5.2 广告状态转换

```mermaid
stateDiagram-v2
    [*] --> 广告未初始化
    广告未初始化 --> 广告初始化中
    广告初始化中 --> 广告就绪 : 初始化成功
    广告初始化中 --> 广告失败 : 初始化失败
    
    广告就绪 --> 广告加载中 : 触发广告请求
    广告加载中 --> 广告展示中 : 加载成功
    广告加载中 --> 广告失败 : 加载失败
    
    广告展示中 --> 广告完成 : 用户观看完整
    广告展示中 --> 广告跳过 : 用户跳过/关闭
    
    广告完成 --> 奖励发放 : 激励广告
    广告完成 --> 广告就绪 : 其他广告
    广告跳过 --> 广告就绪
    广告失败 --> 降级处理
    
    奖励发放 --> 功能解锁
    降级处理 --> 功能解锁 : 直接解锁策略
    
    功能解锁 --> [*]
    广告就绪 --> [*] : 应用退出
```

---

## 6. 技术架构流程图

### 6.1 MVC架构流程

```mermaid
graph TD
    A[用户操作 User Interaction] --> B[View层 Activity/Fragment]
    B --> C[Controller层 Business Logic]
    C --> D[Model层 Data Management]
    
    B1[StartActivity] --> C1[启动控制逻辑]
    B2[CatDogActivity] --> C2[主界面控制逻辑]
    B3[RecordPlayActivity] --> C3[录音播放控制逻辑]
    
    C1 --> D1[广告配置数据]
    C2 --> D2[用户选择数据]
    C3 --> D3[音频文件数据]
    
    D1 --> E1[TTAdManagerHolder]
    D2 --> E2[SharedPreferences]
    D3 --> E3[文件系统]
    
    E1 --> F1[穿山甲SDK]
    E2 --> F2[Android系统存储]
    E3 --> F3[Android媒体框架]
    
    F1 --> G[广告服务器]
    F3 --> H[音频硬件]
    
    style A fill:#e3f2fd
    style B fill:#fff3e0
    style C fill:#f3e5f5
    style D fill:#e8f5e8
```

---

*本文档提供了CATDOG2025宠物翻译器的完整系统流程图，涵盖用户操作、系统处理、数据流向等各个方面，为软件著作权登记提供流程图支撑文档。* 