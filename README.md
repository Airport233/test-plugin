# test-plugin

最小可发布的 IntelliJ 插件，仅用于验证 JetBrains Marketplace 提交流程。

功能：Tools 菜单加一个 `Test Plugin: Say Hello` 动作，点击弹通知。

## 构建

```bash
./gradlew buildPlugin
```

产物：`build/distributions/test-plugin-0.1.0.zip`（即上传到 Marketplace 的文件）。

## 沙盒运行

```bash
./gradlew runIde
```
