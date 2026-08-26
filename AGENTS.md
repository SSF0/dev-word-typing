# 项目：程序员单词打字通（dev-word-typing）

> 接手的 AI / 开发者，**先读本文件获取入口**。核心与技术细节见 `docs/`。

## 一句话定位
给程序员**反复练单词**的工具：技术栈 → 知识点节点（注解/配置）→ 中文释义打**英文单词**，侧栏看该节点源码 + 写个人笔记。

## 核心思想（别做偏）
- 核心是**练单词/技术术语**，不是「整句造句」——决定前先看 `practiceType`。
- `practiceType=WORD`：节点内 statements 为**多个待打单词**，`english=单词`、`chinese=中文释义`，用「中译英」模式（不是听写）。
- 每个节点 = 一份**注解/源码实现**（`annotationCode`），侧栏展示；节点可写 **note 笔记**入库。

## 📄 详细请看 docs/
- **[核心思想 / 技术栈 / 数据模型 / API / 当前进度 / 注意事项](./docs/project-overview.md)**
- **[需求大纲 / 路线图 / 设计原则](./docs/roadmap.md)**

## 关键技术/部署要点
- 前端 **Nuxt3 (Vue3) + Pinia + Tailwind/daisyUI**（照搬 earthworm`apps/client`再裁剪）；后端 **Spring Boot3 (Java17) + JPA**；DB **MySQL 8.4**（Docker `springboot-learn-mysql`，root 空密码，库 `word_typing`）。
- Java17 路径：`/Users/mac/解释器/java/Contents/Home`；前端版本锁定 `nuxt 3.12.1` / `vue 3.4.29`。
- 端口：本前端 `3002/3003`，后端 `8080`（**别撞 earthworm 的 3000/3001/3010**）。
- 数据库改结构后 `DROP DATABASE word_typing` 重 seed（`DataSeeder` count>0 不重跑）。
- 已 push GitHub 公开仓库 `SSF0/dev-word-typing`（main）。

## ⚙️ 常用命令
```bash
# 后端
cd backend && JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw spring-boot:run
# 前端生产构建预览
cd frontend && API_BASE=http://localhost:8080 pnpm build && PORT=3003 node .output/server/index.mjs
```