# 程序员单词打字通 (dev-word-typing)

> **接手本项目，请先读 [`AGENTS.md`](./AGENTS.md)** —— 包含核心思想、技术栈、数据模型、当前进度、路线图与给 AI 的注意事项，避免理解偏方向。

一套面向程序员的「技术栈单词练习」工具：以**技术栈 → 知识点(注解)节点**为纲，中文提示 → 英文打字练习，
练习页侧栏展示对应**注解/知识点的源码实现**，节点可写个人笔记。前端完整保留 earthworm 的打字流程与样式。

## 目录结构

```
dev-word-typing/
├── frontend/   # Vue 3 + TypeScript + Vite + Pinia + Tailwind/daisyUI
└── backend/    # Spring Boot 3 (Java 17) + Spring Data JPA + MySQL —— 数据与接口
```

## 快速开始

### 1. 启动 MySQL（Docker）

```bash
docker start springboot-learn-mysql   # MySQL 8.4，端口 3306，root 空密码
```

（或任意 MySQL 8，root 空密码，自动建库 `word_typing`。如需改连接，见 `backend/src/main/resources/application.yml`。）

### 2. 启动后端

```bash
cd backend
export LC_ALL="en_US.UTF-8"                              # Java 路径含中文时避免 Maven 子进程乱码
export LANG="en_US.UTF-8"
export JAVA_HOME="/Users/mac/解释器/java/Contents/Home"   # 你的 Java 17 路径
./mvnw spring-boot:run
```

首次启动自动建表并写入种子数据（Java 技术栈：@Controller、@RestController…）。
验证：`curl http://localhost:8080/course-pack`

### 3. 启动前端

```bash
cd frontend
pnpm install
VITE_API_BASE=http://localhost:8080 pnpm dev
```

浏览器打开 `http://localhost:3002/course-pack`。

生产构建与本地预览：

```bash
VITE_API_BASE=http://localhost:8080 pnpm build
pnpm preview --host 0.0.0.0
```

预览地址为 `http://localhost:3003/course-pack`，构建产物位于 `frontend/dist/`。

## 数据模型（后端 JPA）

| 表 | 说明 |
|---|---|
| `tech_stack` | 技术栈 = 前端 `course-pack` 列表项 |
| `node` | 知识点节点 = 前端 `course`（含 `annotation_code` 源码、`annotation_explain` 注释）|
| `statement` | 练习句子（chinese 提示 / english 打字 / soundmark 音标）|

## API（对齐前端 fetch）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/course-pack` | 技术栈列表 |
| GET | `/course-pack/{stackId}` | 技术栈详情（含 nodes/知识点）|
| GET | `/course-pack/{stackId}/courses/{nodeId}` | 单个知识点（含 statements + 注解源码）|
| POST | `/course-pack/{stackId}/courses/{nodeId}/complete` | 完成，返回下一知识点 |

## 功能

- **页面流程**（照搬 earthwork）：技术栈列表 → 知识点节点列表 → 打字练习页
- **打字交互**：中文提示 → 逐词英文打字，Enter 提交，错词 Fix 修复，两种模式（听写 / 中译英）
- **注解源码侧栏**：练习页右侧展示当前知识点的源码实现 + 使用场景注释
- **进度/结算**：进度条、完成统计、下一课 / 再练一次 / 课程列表
- 去除了登录 / 会员 / 分享 / 掌握状态，纯自用

## 种子数据

`backend/.../config/DataSeeder.java`：预置「Java 技术栈」，含 @Controller、@RestController 两个注解节点，
每个节点附注解源码 + 中文练习句 + 英文 + 音标。后续可扩展 React/Go 等技术栈。

## 说明

- 前端保留 earthworm 的 Vue 3 打字流程与样式，运行壳已迁移为纯 Vue 3 + TypeScript + Vite SPA。
- 项目为「可落地使用」而非 demo，前后端分离、数据落库 MySQL。
