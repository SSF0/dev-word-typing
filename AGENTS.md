# 项目：程序员单词打字通（dev-word-typing）

> 接手的 AI / 开发者，**先读本文件获取入口**。核心与技术细节见 `docs/`。

## 一句话定位
给程序员**反复练单词**的工具：技术栈 → 小节（注解/MyBatis/项目结构）→ 关键术语，依据中文释义打**英文单词**，侧栏查看当前术语的用法并写个人笔记。

## 核心思想（别做偏）
- 核心是**练单词/技术术语**，不是「整句造句」——决定前先看 `practiceType`。
- `practiceType=WORD`：小节内 statements 为**多个待打术语**，`english=实际输入和发音的英文`、`prefix=固定展示但不参与输入的前缀（可选）`、`chinese=中文释义`，用「中译英」模式（不是听写）。
- 每个 statement = 一个可复习知识项，独立携带 `explanation`、`usageExample`、`referenceCode` 和 `note`；侧栏始终展示当前 statement 的详情。

## 📄 详细请看 docs/
- **[核心思想 / 技术栈 / 数据模型 / API / 当前进度 / 注意事项](./docs/project-overview.md)**
- **[需求大纲 / 路线图 / 设计原则](./docs/roadmap.md)**

## 关键技术/部署要点
- 前端 **Vue3 + TypeScript + Vite + Pinia + Tailwind/daisyUI**（从 earthworm`apps/client`裁剪并移除 Nuxt 壳）；后端 **Spring Boot3 (Java17) + JPA**；DB **MySQL 8.4**（Docker `springboot-learn-mysql`，root 空密码，库 `word_typing`）。
- Java17 路径：`/Users/mac/解释器/java/Contents/Home`；前端版本锁定 `vue 3.4.29` / `vite 5.3.1` / `typescript 5.4.5`。
- 端口：本前端 `3002/3003`，后端 `8080`（**别撞 earthworm 的 3000/3001/3010**）。
- `DataSeeder` 会幂等刷新内置 Java 小节；JPA `ddl-auto=update` 自动补充新增字段。
- 已 push GitHub 公开仓库 `SSF0/dev-word-typing`（main）。

## ⚙️ 常用命令
```bash
# 后端
cd backend && LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw spring-boot:run
# 前端生产构建预览（Vite 静态产物 dist/）
cd frontend && VITE_API_BASE=http://localhost:8080 pnpm build && pnpm preview --host 0.0.0.0
```
