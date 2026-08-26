# 项目概述 — 程序员单词打字通（dev-word-typing）

> 本文件是项目的「核心思想与技术设计」权威说明。
> 任何接手本项目的开发者 / AI，请从这里开始理解。

## 一、一句话定位

**一套面向程序员的「技术栈单词练习」工具**：以「技术栈 → 知识点(注解/配置)节点」为纲，中文释义 → 打英文单词/句子的打字练习，练习页展示节点源码，支持个人笔记。

## 二、核心思想（最重要，别做偏）

地址：承接自 earthworm 的「打字练习核心」，做给程序员**反复练单词**：

- **练的是「单词 / 技术术语」，不是「整句造句」**。
  - Java 技术栈：每个节点 = 一个注解（如 `@Controller`），点进去是**反复打该注解相关的英文单词**（controller、receive、request...）。
  - 部分包（Vite 配置、通用词汇）会有多词短语，一个 input 打一个目标项。
- **练习交互 = 中文释义提示 → 打英文单词**，用「中译英」模式（**不是听写**）。
- **每个节点 = 一份注解源码实现**，练习页右侧展示源码 + 使用场景解释。
- **每个节点可写「个人见解/笔记」**，存后端数据库，随源码一起展示。
- 节点卡片/列表展示「单词 / 整句」模式徽标。

## 三、技术栈

| 端 | 技术 | 说明 |
|---|---|---|
| 前端 | **Nuxt 3 (Vue 3) + Pinia + Tailwind/daisyUI** | 从 earthworm 的 `apps/client` **照搬**页面流程/样式，再裁剪 |
| 后端 | **Spring Boot 3 (Java 17) + Spring Data JPA** | RESTful API |
| 数据库 | **MySQL 8.4**（Docker 容器 `springboot-learn-mysql`，root 空密码，库名 `word_typing`） | 首启自动建表 + 种子 |

- Java 17：`/Users/mac/解释器/java/Contents/Home`
- 前端版本锁定 `nuxt 3.12.1` / `vue 3.4.29`（与 earthworm 原工程一致，保证「流程样式相同」）

## 四、目录结构

```
dev-word-typing/
├── docs/       # 项目文档（本目录），规则/规范后续沉淀于此
├── AGENTS.md   # AI 入口：核心思想速览 + 指向 docs
├── frontend/   # Nuxt 3（照搬并裁剪 earthworm 前端 + 单词模式/笔记/源码侧栏）
└── backend/    # Spring Boot 3 + JPA + MySQL
```

## 五、数据模型（后端 JPA）

```
tech_stack（技术栈 = course-pack 列表项）
  └── node（知识点节点 = 前端 course）
        ├─ title / description / sortOrder
        ├─ annotationCode     # 注解/知识点源码（侧栏展示）
        ├─ annotationExplain  # 源码/用法注释
        ├─ practiceType       # WORD(单词) / SENTENCE(整句) ← 核心字段
        ├─ note               # 个人笔记（可编辑入库）
        └── statement（练习项）
              ├─ english    单词（如 controller）或整句
              └─ chinese    中文释义（练习提示）

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

## 七、前端页面流程（复用 earthwork）

```
/course-pack            技术栈列表（包）
  → /course-pack/{id}   知识点节点列表（节点卡片，显示「单词/整句」徽标 + 单词数）
  → /game/{pack}/{node} 打字练习：左上中文释义，逐字母打英文，右侧源码+笔记
```

- 打字引擎 `frontend/components/main/QuestionInput/` 复用 earthworm 的 `useInput`：逐词对照、Enter 提交、Fix 修复错词。
- `pages/game/[coursePackId]/[id].vue`：WORD 节点进入强制「中译英」模式（不用听写）。
- `components/main/AnnotationPanel.vue`：右侧侧栏 = 注解源码 + 使用场景 + 笔记编辑（PUT 入库）。

## 八、当前进度（✅）

- [x] 后端 Spring Boot 3 + MySQL，三表 + Java 种子（@Controller/@RestController/@Autowired，各 4-6 单词）
- [x] 前端照搬 earthworm 流程（列表→详情→练习），去登录/会员/分享
- [x] WORD 模式（中文释义→打单词）+ 节点卡片模式徽标
- [x] 侧栏注解源码 + 笔记（可编辑入库）
- [x] 已 push GitHub 公开仓库 `SSF0/dev-word-typing`（main）

## 九、🏁 给开发者的注意事项

1. 核心是**练单词，不是练句子** → 做决定前先看 `practiceType`。
2. **端口别撞 earthworm**：earthworm 前端 3000、后端 3001/3010；本前端建议 3002/3003，后端 8080。
3. 前端从 earthworm 照搬，改动必须保留「流程样式相同」，改 Nuxt/Vue 版本要谨慎。
4. 数据库改结构后建议 `DROP DATABASE word_typing` 重新 seed（`DataSeeder` count>0 不重跑）。
5. 别提交 `node_modules/.nuxt/.output/backend/target`（已 gitignore）。