# myDV API21

> **独立兼容实现声明**：本项目以 [`mytv-android/myDV`](https://github.com/mytv-android/myDV) 公开展示的产品功能与电视端交互为参照，为 **Android 5.0（API 21）及以上**设备独立编写。原 myDV 发布 APK 的公开元数据标注最低 API 23，而该仓库未公开 Android 源码；因此本项目不复制、移植或反向工程未公开实现。

`myDV API21` 是用于 Android TV、电视盒子与横屏设备的第三方短视频网页客户端。它通过系统 WebView 打开官方网页，并提供适合遥控器的方向滚动、确认播放/暂停、菜单控制栏、网页全屏与本机会话清除操作。

| 项目 | 说明 |
|---|---|
| 最低系统 | **Android 5.0 / API 21** |
| 语言 | Java + Android View XML |
| 内容路径 | 系统 WebView 加载官方网页，不内置私人接口或视频资源 |
| 登录 | 仅由网页原生登录流程处理；不会读取、显示或上传 Cookie、账号或密码 |
| 输入 | 电视遥控器 D-pad、确认、菜单、返回及媒体播放键 |
| 数据控制 | 可在应用内一键删除本机 WebView Cookie、缓存和历史记录 |

## 遥控器操作

| 按键 | 行为 |
|---|---|
| 上 / 下 | 按页滚动网页内容 |
| 左 / 右 | 小幅横向滚动 |
| 确认 / 播放暂停 | 播放或暂停当前可见 HTML5 视频 |
| 菜单 / 设置 | 显示或隐藏控制栏 |
| 返回 | 退出网页全屏；首次打开控制栏；再次确认退出应用 |

## 构建

需要 JDK 17 与 Android SDK Platform 35。调试构建：

```bash
./gradlew test assembleDebug
```

Release 构建由 GitHub Actions 从仓库 Secrets 中恢复稳定 keystore 后完成签名。请不要替换 `OPENTV_KEYSTORE_BASE64`、`OPENTV_STORE_PASSWORD`、`OPENTV_KEY_ALIAS` 与 `OPENTV_KEY_PASSWORD`，否则新 APK 无法覆盖安装同一发布链中的旧版本。

## 边界与安全

本项目不实现私有 API 签名、设备指纹伪装、验证码绕过、Cookie 自动填充、账号抓取、DRM 规避或视频流抓取。网页内容、登录状态、可用性和适用规则由对应平台负责；请遵守平台条款和当地法律。

## 许可证与致谢

本仓库中新写代码以 MIT License 发布。`myDV`、`抖音`及其相关商标归各自权利人所有；本项目不隶属于或获其背书。
