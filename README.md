# Whack a Mole

一个住在 IDE 工具窗口里的打地鼠小游戏（JetBrains Marketplace 提交流程验证用插件）。

玩法：右侧边栏打开 **Whack a Mole** 工具窗口 → 点 `Start` → 30 秒内点击随机冒出的地鼠（🐹）计分。

## 构建

```bash
./gradlew buildPlugin
```

产物：`build/distributions/whack-a-mole-0.1.0.zip`（即上传到 Marketplace 的文件）。

## 本地验证与沙盒运行

```bash
./gradlew verifyPlugin   # Plugin Verifier 兼容性检查
./gradlew runIde         # 沙盒 IDE 试玩
```
