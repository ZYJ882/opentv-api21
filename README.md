# OpenTV API21

> **独立适配声明**：本项目参考了 [mytv-android/mytv-android](https://github.com/mytv-android/mytv-android) 公开 README 所描述的电视直播交互与功能方向，并为 **Android 5.0（API 21）及以上**设备重新设计、独立编写。它不隶属于原项目；原项目名称、商标及其相关权利归各自权利人所有。

OpenTV API21 是一个面向旧版 Android TV、电视盒子与平板的原生 Java IPTV 播放器骨架。项目不包含、不分发也不推荐任何频道列表或受版权保护的视频内容；用户只能导入自己拥有使用权的 M3U 播放列表。

| 项目 | 说明 |
|---|---|
| 最低系统 | **Android 5.0 / API 21** |
| 开发语言 | Java + Android View XML |
| 播放内核 | AndroidX Media3 ExoPlayer |
| 输入方式 | 触屏、遥控器 D-pad、数字键 |
| 数据保存 | 本地 SharedPreferences，仅保存收藏频道 URL |

## 已实现功能

应用提供合法 M3U URL 导入、频道列表、频道搜索、长按收藏、HLS 等由 Media3 支持的媒体播放和基础遥控器操作。上/下方向键切换频道，数字键直接选择前九个频道；频道列表中的长按操作可切换收藏状态。

## 构建

请使用 Android Studio Ladybug 或更高版本打开本项目，并安装 Android SDK Platform 35。同步 Gradle 后即可安装到 Android 5.0 或更高版本设备。命令行构建可执行：

```bash
./gradlew assembleDebug
```

## 兼容性取舍

原项目公开说明中标注 Android 6.0+，并使用较新的 Material 3 Expressive 方向。本项目为保持 API 21 支持，采用 Java、传统 View XML、AppCompat、RecyclerView 与 Media3；不依赖 Jetpack Compose、动态配色或 Android 6.0 后新增的系统能力。

## 许可证与致谢

本仓库中的新代码以 MIT License 发布。原项目的公开 README 说明其基于 MIT 许可组件进行迭代，但其当前公开仓库不提供应用源码。因此本项目不复制、移植或再发布原项目的未公开实现，仅保留透明的功能来源说明。请分别遵守你导入的播放列表、频道、图标和流媒体内容的许可与适用法律。

## 安全与内容声明

OpenTV API21 不内置远程订阅、广告、跟踪或账号系统。请只导入可信且合法的 HTTPS/HTTP M3U 地址；应用会直接请求你输入的地址并将频道流交由系统网络栈播放。
