# 项目说明 — 程序员单词打字通（dev-word-typing）

> ⚠️ 任何接手本项目的 AI / 开发者，**务必先读本文件**。
> 这里写了「这个项目要做什么、核心思想、当前进度、下一步」，避免重新理解偏方向。

---

## 一、一句话定位

**一套面向程序员的「技术栈单词练习」工具**：以「技术栈 → 知识点(注解/配置)节点」为纲，中文释义 → 打英文单词/句子的打字练习，练习页展示节点源码，支持个人笔记。

## 二、核心思想（最重要，别做偏）

用户最初想把 earthworm 的「打字练习核心」抽出来，做一个**给程序员反复练单词**的东西。关键点：

- **练的是「单词 / 技术术语」，不是「整句造句」**。
  - Java 技术栈：每个节点 = 一个注解（如 `@Controller`），点进去是**反复打该注解相关的英文单词**（controller、receive、request...）。
  - 部分包（如 Vite 配置、通用词汇）会有多词短语，一个 input 打一个目标项。
- **练习交互 = 中文释义提示 → 打英文单词**，用「中译英」模式（不是听写）。
- **每个节点 = 一份注解源码实现**，练习页右侧展示源码 + 使用场景解释，方便对照学习。
- **每个节点可写「个人见解/笔记」**，存后端数据库，随源码一起展示。
- 节点卡片/CAS塑性显示「单词 / 整句」模式徽标。

## 三、技术栈

| 端 | 技术 | 说明 |
|---|---|---|
| 前端 | **Nuxt 3 (Vue 3) + Pinia + Tailwind/daisyUI** | 从 `earthwork` 的 `apps/client` **照搬**页面流程/样式，再裁剪 |
| 后端 | **Spring Boot 3 (Java 17) + Spring Data JPA** | RESTful API |
| 数据库 | **MySQL 8.4**（Docker，容器 `springboot-learn-mysql`，root 空密码，库名 `word_typing`） | 首次启动自动建表 + 写种子 |

- Java 17 路径：`/Users/mac/解释器/java/Contents/Home`
- 前端框架版本已锁定：`nuxt 3.12.1` / `vue 3.4.29`（与 earthworm 原工程一致，保证「流程样式相同」）

## 四、目录结构

```
dev-word-typing/
├── frontend/   # Nuxt 3，照搬并裁剪 earthworm 前端 + 单词模式/笔记/源码侧栏
└── backend/    # Spring Boot 3 + JPA + MySQL
```

## 五、数据模型（后端 JPA）

```
tech_stack（技术栈 = 前端 course-pack 列表项）
  └── node（知识点节点 = 前端 course）
        ├─ title / description / sortOrder
        ├─ annotationCode     # 注解/知识点源码实现（侧栏展示）
        ├─ annotationExplain  # 源码/用法注释
        ├─ practiceType       # WORD(单词练习) / SENTENCE(整句练习) ← 核心字段
        ├─ note               # 个人笔记/见解（可编辑入库）
        └── statement（练习项 = 前端 statement）
              ├─ english    单词（如 controller）或整句
              └─ chinese    中文释义（练习提示）
```

- 关键：`practiceType=WORD` 时，每个 `statement.english` 就是**一个待打单词**，`chinese` 是它的中文释义。
- `practiceType=SENTENCE` 保留整句模式（你提到过想加「常用词生成的句子去练习」）。

## 六、API（对齐前端 fetch）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/course-pack` | 技术栈列表 |
| GET | `/course-pack/{stackId}` | 技术栈详情（含 nodes）|
| GET | `/course-pack/{stackId}/courses/{nodeId}` | 单个知识点（含 statements + 源码 + note）|
| POST | `/course-pack/{stackId}/courses/{nodeId}/complete` | 完成，返回下一知识点 |
| PUT | `/course-pack/{stackId}/courses/{nodeId}` | 保存个人笔记 note |

## 七、前端页面流程（照搬 earthwork）

```
/course-pack            技术栈列表（包）
  → /course-pack/{id}   知识点节点列表（每个节点=注解，卡片有「单词/整句」徽标 + 单词数）
  → /game/{pack}/{node} 打字练习：左上中文释义，逐词打英文，右侧注解源码+笔记
```

- 打字引擎（`frontend/components/main/QuestionInput/`）直接复用 earthworm 的 `useInput` 状态机：逐词对照、Enter 提交、Fix 修复错词。
- `pages/game/[coursePackId]/[id].vue`：WORD 节点进入时强制「中译英」模式（`toggleGamePlayMode(ChineseToEnglish)`），不用听写。
- `components/main/AnnotationPanel.vue`：右侧侧栏 = 注解源码 + 使用场景 + 笔记编辑（PUT 入库）。

## 八、当前进度（已完成 ✅）

- [x] 后端 Spring Boot 3 + MySQL，三表 + WORD seed（Java：@Controller/@RestController/@Autowired，各 6 单词）
- [x] 前端照搬 earthwork 流程（列表→详情→练习），去登录/会员/分享/掌握
- [x] 练习页侧栏：注解源码 + 我的笔记（可编辑入库）
- [x] WORD 模式（中文释义→打单词）跑通；节点卡片显示模式徽标
- [x] 已推送 GitHub 公开仓库 `SSF0/dev-word-typing`（main 分支）

## 九、路线图 / 你要的东西（也 todo，后续做）

- 加更多技术栈包（Vite、React、Go 等）及各注解 `@Configuration`、`@` Bean、`@Transactional` 节点 + 单词
- `SENTENCE` 整句模式：把你常用短语/句子生成语料练句
- 单词/节点级「错误次数」**错词加权**（写库，随机里的错词出现概率提高 / 取多个练）
- 大纲/流程图节点（如 controller→service→repository）可视化，点击对应节点词进入练习
- 随机模式、练习统计、学习时间

## 十、运行

```bash
# 后端（Java 17）
cd backend
export JAVA_HOME="/Users/mac/解释器/java/Contents/Home"
./mvnw spring-boot:run          # 端口 8080

# 前端
cd frontend
pnpm install
API_BASE=http://localhost:8080 pnpm dev   # dev 用 3000/或用 --port 3002
# 或生产构建预览
API_BASE=http://localhost:8080 pnpm build && PORT=3000 node .output/server/index.mjs
```

## ⚠️ 给 AI 的注意事项

1. **核心是练单词，不是练句子** —— 不要又被「整句话」带偏。做 pitch 提前看 `practiceType`。
2. **端口不要撞 earthworm**：earthworm 前端占 `3000`、后端 `3001/3010`；本前端建议用 `3002/3003`，后端用 `8080`。
3. 前端是从 earthworm 照搬的，改动作必须保留「流程样式相同」；改 `Nuxt`/`Vue` 版本要谨慎。
4. 数据库改动后建议 `DROP DATABASE word_typing` 重新 seed，否则 `DataSeeder` 不会重跑（count>0）。
5. 别提交 `node_modules/.nuxt/.output/backend/target`（已 gitignore）。