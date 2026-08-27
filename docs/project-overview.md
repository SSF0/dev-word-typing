# 项目概述 — 程序员单词打字通（dev-word-typing）

> 本文件是项目的「核心思想与技术设计」权威说明。
> 任何接手本项目的开发者 / AI，请从这里开始理解。

## 一、一句话定位

**一套面向程序员的「技术栈单词练习」工具**：以「技术栈 → 小节 → 关键术语」为纲，中文释义 → 打英文术语，练习页展示当前术语的解释、示例和源码，支持逐词记录个人笔记。

## 二、核心思想（最重要，别做偏）

地址：承接自 earthworm 的「打字练习核心」，做给程序员**反复练单词**：

- **练的是「单词 / 技术术语」，不是「整句造句」**。
  - Java 技术栈：节点表示小节（如“Java 常用注解”），小节内直接练习 `RestController`、`Service` 等英文术语；`@` 由可配置的固定前缀展示，不参与输入和发音。
  - 部分包（Vite 配置、通用词汇）会有多词短语，一个 input 打一个目标项。
- **练习交互 = 中文释义提示 → 打英文单词**，用「中译英」模式（**不是听写**）。
- **每个练习项 = 一份独立知识详情**，练习页右侧展示当前术语的解释、使用示例和参考源码。
- **每个练习项可写「个人见解/笔记」**，存后端数据库，切换术语时同步切换。
- 节点卡片/列表展示「单词 / 整句」模式徽标。

## 三、技术栈

| 端 | 技术 | 说明 |
|---|---|---|
| 前端 | **Vue 3 + TypeScript + Vite + Pinia + Tailwind/daisyUI** | 保留 earthworm 的页面流程/样式，使用纯 Vue SPA 运行壳 |
| 后端 | **Spring Boot 3 (Java 17) + Spring Data JPA** | RESTful API |
| 数据库 | **MySQL 8.4**（Docker 容器 `springboot-learn-mysql`，root 空密码，库名 `word_typing`） | 首启自动建表 + 种子 |

- Java 17：`/Users/mac/解释器/java/Contents/Home`
- 前端版本锁定 `vue 3.4.29` / `vite 5.3.1` / `typescript 5.4.5`

## 四、目录结构

```
dev-word-typing/
├── docs/       # 项目文档（本目录），规则/规范后续沉淀于此
├── AGENTS.md   # AI 入口：核心思想速览 + 指向 docs
├── frontend/   # Vue 3 + TypeScript + Vite（单词模式/笔记/源码侧栏）
└── backend/    # Spring Boot 3 + JPA + MySQL
```

## 五、数据模型（后端 JPA）

```
tech_stack（技术栈 = course-pack 列表项）
  └── node（小节 = 前端 course）
        ├─ title / description / sortOrder
        ├─ practiceType       # WORD(单词) / SENTENCE(整句) ← 核心字段
        └── statement（练习项）
              ├─ english       实际输入和发音的英文（如 RestController、resultMap、controller）
              ├─ prefix        固定展示前缀（可选，如 @），不参与判题与发音
              ├─ chinese       中文释义（练习提示）
              ├─ explanation   核心作用和使用要点
              ├─ usageExample  可直接理解的完整使用示例
              ├─ referenceCode 参考源码（可选）
              └─ note          当前术语的个人笔记

  practiceType=WORD: statement.english=单个待打单词, chinese=中文释义
  practiceType=SENTENCE: 保留整句练习
```

## 六、API（对齐前端 fetch）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/course-pack` | 技术栈列表 |
| GET | `/course-pack/{stackId}` | 块详情（含 nodes）|
| GET | `/course-pack/{stackId}/courses/{nodeId}` | 单个知识点（statements + 源码 + note）|
| POST | `/course-pack/{stackId}/courses/{nodeId}/complete` | 完成，返回下一知识点 |
| PUT | `/course-pack/{stackId}/courses/{nodeId}` | 保存个人笔记 note |
| PUT | `/course-pack/{stackId}/courses/{nodeId}/statements/{statementId}` | 保存当前练习项的个人笔记 |

## 七、前端页面流程（复用 earthwork）

```
/course-pack            技术栈列表（包）
  → /course-pack/{id}   小节列表（显示「单词/整句」徽标 + 术语数）
  → /game/{pack}/{node} 打字练习：左侧本节术语，中间中译英，右侧当前术语详情+笔记
```

- 打字引擎 `frontend/components/main/QuestionInput/` 复用 earthworm 的 `useInput`：逐词对照、Enter 提交、Fix 修复错词。
- WORD 模式进入练习项时按现有开关自动朗读英文；答错后开始重新输入时再朗读一次。固定 `prefix` 始终可见，但不会进入答案或语音。
- `pages/game/[coursePackId]/[id].vue`：WORD 节点进入强制「中译英」模式（不用听写）。
- `components/main/AnnotationPanel.vue`：右侧侧栏 = 当前练习项解释 + 使用示例 + 参考源码 + 单词笔记。

## 八、当前进度（✅）

- [x] 后端 Spring Boot 3 + MySQL，Java 种子包含“Java 常用注解、MyBatis、Java 项目结构”三个高频小节
- [x] 前端照搬 earthworm 流程（列表→详情→练习），去登录/会员/分享
- [x] WORD 模式（中文释义→打单词）+ 节点卡片模式徽标
- [x] 侧栏注解源码 + 笔记（可编辑入库）
- [x] 前端移除 Nuxt/Nitro，迁移为 Vue 3 + TypeScript + Vite SPA
- [x] 已 push GitHub 公开仓库 `SSF0/dev-word-typing`（main）

## 九、🏁 给开发者的注意事项

1. 核心是**练单词，不是练句子** → 做决定前先看 `practiceType`。
2. **端口别撞 earthworm**：earthworm 前端 3000、后端 3001/3010；本前端建议 3002/3003，后端 8080。
3. 前端保留 earthworm 的核心流程和样式；升级 Vue/Vite/TypeScript 版本时要保持锁定并跑完整门禁。
4. `DataSeeder` 会幂等刷新内置 Java 小节并保留同名练习项的掌握状态和笔记；JPA `ddl-auto=update` 负责补充字段。
5. 别提交 `node_modules/`、`frontend/dist/`、`backend/target/`（已 gitignore）。
