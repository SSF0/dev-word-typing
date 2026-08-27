# Nuxt 3 → Vue 3 + TypeScript + Vite 迁移实施计划

> 日期：2026-08-27
>
> 状态：已完成（2026-08-27）
>
> 目标：在不改变“技术术语中译英打字练习”产品行为的前提下，把 `frontend` 从 Nuxt 3 SPA 迁移为纯 Vue 3 + TypeScript + Vite SPA。

## 一、结论

**可以迁移，而且适合迁移。**

迁移前的前端并不是 Next.js，而是 **Nuxt 3**；Nuxt 3 的底层本来就是 Vue 3 + TypeScript。项目当时已经设置 `ssr: false`，没有 Nuxt 服务端路由、SSR 数据获取、SEO 渲染或服务端中间件，实际运行方式已经很接近普通 Vue SPA。因此业务组件、Pinia store、composables、API 类型和 Tailwind 样式都得以继续使用，没有重写打字练习核心。

不过，这不是简单地把依赖名从 `nuxt` 改成 `vue`。迁移前 Nuxt 仍隐式提供了文件路由、应用启动、全局组件自动导入、运行时配置、Nuxt UI、图片组件、动画注入和测试环境。因此实施时先逐项接管这些能力，再删除 Nuxt。

推荐在现有 `frontend` 目录内渐进迁移，先保留现有 Vue 3.4.29、Vue Router 4.3.3、Pinia 2.1.7、Vite 5.3.1 和 TypeScript 5.4.5 版本，避免把“换框架壳”和“升级依赖”混成一件事。

实施结果：已按 Phase 0 → Phase 6 完成迁移。最终 `pnpm test` 为 38 个测试文件、283 个测试全通过，`pnpm type-check` 与 `VITE_API_BASE=http://localhost:8080 pnpm build` 通过；浏览器验证了列表、小节、练习、动态路由刷新、详情侧栏、设置/暂停/重置弹窗、390×844 布局，并确认新页面控制台无 error/warn。

## 二、现状证据与迁移边界

### 2.1 Nuxt 使用范围

| 能力 | 迁移前情况 | 迁移判断 |
|---|---|---|
| 渲染模式 | `nuxt.config.ts` 中 `ssr: false` | 可直接转为 Vue SPA |
| 页面 | 仅 3 个业务页面 | 手工 Vue Router 配置成本低 |
| 服务端能力 | 没有 `server/api`、server middleware、SSR 数据逻辑 | 不需要替代服务端框架 |
| 数据获取 | 没有 `useFetch` / `useAsyncData` | 现有 `ofetch` API 层可保留 |
| 状态 | Pinia store | 原样保留，仅调整初始化入口 |
| 页面跳转 | 少量 `navigateTo`，其余已直接使用 `vue-router` | 改为 `router.push` |
| 运行时配置 | API Base 为主，遗留用户菜单另有帮助地址 | 改为 `import.meta.env`，并收敛遗留配置 |
| Nuxt UI | 10 类组件标签，共约 42 处使用 | 需要本地 Vue UI 组件或原生/daisyUI 替代 |
| Nuxt Image | 仅 1 个 `NuxtImg` | 改为原生 `<img>` |
| Nuxt Anime | 仅 1 个 `$anime` 调用 | 改为直接调用 `animejs` 或 CSS 动画 |
| 自动导入 | 多个路径前缀组件，如 `MainTool`、`CoursesCourseCard` | 改为显式 import |
| 测试 | Vitest 依赖 `@nuxt/test-utils`，1 处 `mockNuxtImport` | 改为普通 Vitest + Vue Test Utils |
| 构建产物 | Nitro Node 服务 `.output/server` | 改为 Vite 静态产物 `dist/` |

### 2.2 需要保持的路由契约

Vue Router 必须显式保留以下路径和 route name，因为布局逻辑依赖 route name：

| URL | route name | 页面 |
|---|---|---|
| `/` | `home` | 重定向到 `/course-pack` |
| `/course-pack` | `course-pack` | 技术栈列表 |
| `/course-pack/:id` | `course-pack-id` | 小节列表 |
| `/game/:coursePackId/:id` | `game-coursePackId-id` | 打字练习 |

### 2.3 不在本次迁移范围内

- 不改后端接口、JPA 模型、MySQL 或 CORS 策略。
- 不改 `practiceType=WORD` 的中译英逻辑、判题、发音、笔记保存和侧栏行为。
- 不进行 UI 重新设计；替换 Nuxt UI 时保持现有视觉和交互。
- 不同时升级 Vue/Vite/TypeScript 大版本。
- 不恢复已经裁掉的登录、会员、编辑器等 earthworm 功能。
- 不在迁移过程中顺便重组整个前端为 `src/` 目录；先减少文件移动，迁移稳定后再单独整理。

## 三、目标结构

第一轮迁移保持现有业务目录，新增最小 Vite 壳：

```text
frontend/
├── index.html                 # Vite HTML 入口、标题、favicon
├── main.ts                    # createApp / Pinia / Router / HTTP 初始化
├── app.vue                    # 根组件
├── router/
│   └── index.ts               # 显式路由表
├── components/
│   └── ui/                    # Nuxt UI 的本地替代组件
├── vite.config.ts             # Vue 插件、~ / @ alias、开发端口
├── postcss.config.*           # Tailwind PostCSS 接入
├── tailwind.config.*          # 显式 content 扫描范围
├── env.d.ts                   # ImportMetaEnv 类型
├── pages/                     # 现有页面，保留目录但不再自动生成路由
├── layouts/                   # 现有默认布局
├── components/ composables/ store/ api/ ...
└── dist/                      # Vite 构建产物
```

目标启动链：

```text
index.html
  → main.ts
      → setupHttp(import.meta.env.VITE_API_BASE)
      → app.use(pinia)
      → app.use(router)
      → app.mount('#app')
          → app.vue
              → DefaultLayout
                  → RouterView
```

## 四、分阶段实施

每个 Phase 独立提交、独立验收。上一阶段没有通过门禁，不进入下一阶段。

### Phase 0：修绿现有 Nuxt 基线

**目的：** 先得到可信的迁移对照物，避免把既有失败误判为迁移回归。

当前实测基线（2026-08-27）：

- `pnpm build`：通过。
- `pnpm test`：257/258 通过；`services/tests/auth.spec.ts` 有 1 个既有失败。
- `pnpm type-check`：失败；`api/user.ts`、`components/main/Summary.vue`、`components/UserMenu.vue`、`store/user.ts` 共 10 个既有 TypeScript 错误。

任务：

1. 列出从 3 个业务路由可达的组件/模块，区分当前产品代码与 earthworm 遗留死代码。
2. 对已经明确裁掉的登录、会员、无路由菜单功能，删除不可达代码和对应失效测试；仍可能使用的模块则修正类型，不用 `any` 掩盖。
3. 修正 `auth.spec.ts` 与当前“访客模式”实现不一致的问题。
4. 给 3 条业务路由、API Base 初始化和根布局增加最小特征测试。
5. 记录 Nuxt 构建产物和三条页面的截图，作为迁移后的视觉对照。

门禁：

```bash
cd frontend
pnpm test
pnpm type-check
pnpm build
```

三条命令必须全部通过。

### Phase 1：在 Nuxt 仍可运行时解除业务代码耦合

**目的：** 先让业务代码不再依赖 Nuxt API，仍使用 Nuxt 构建验证行为。

任务：

1. **导航适配**
   - 把 `navigateTo` 改为 Vue Router 的 `router.push`。
   - 把 `NuxtLink` 改为 `RouterLink`。
   - 保留原 route name，避免导航栏和布局判断失效。
2. **HTTP 配置适配**
   - 把 `api/http.ts` 改成显式接收 `baseURL`，内部不再调用 `useRuntimeConfig`。
   - Nuxt 过渡期由现有插件传入 runtime config；切 Vite 后由 `main.ts` 传入 `import.meta.env.VITE_API_BASE`。
   - 为未配置、空字符串和末尾斜杠建立测试。
3. **图片与动画适配**
   - 单个 `NuxtImg` 改为原生 `<img>`，保留宽高、懒加载、占位背景和 object-fit 行为。
   - `LearningTimer.vue` 不再使用 `useNuxtApp().$anime`，直接使用 `animejs`；如果同等效果可由 CSS keyframes 完成，优先删掉动画运行时依赖。
4. **组件导入显式化**
   - 给所有业务 SFC 补齐本地组件 import。
   - 保留当前组件名，尤其是 `Main*`、`Common*`、`Courses*`、`Mode*` 前缀，降低模板变更量。
   - 不引入新的自动导入插件，避免用另一套隐式机制替换 Nuxt 隐式机制。

门禁：Nuxt 下测试、类型检查、生产构建仍全部通过，三条业务路径行为不变。

### Phase 2：替换 Nuxt UI 与动态弹窗服务

**目的：** 去掉迁移中最大的框架绑定，同时保持页面视觉稳定。

现有 Nuxt UI 标签包括：

- `UModal`、`UModals`、`USlideover`
- `UButton`、`USelect`、`UContainer`、`UKbd`
- `UIcon`、`UTooltip`、`UAvatar`

实施策略：

1. 在 `components/ui/` 建立小型本地组件，只覆盖项目实际使用的 props/slots，不复刻整个 Nuxt UI。
2. 普通按钮、容器、键帽、选择框和头像优先使用原生 HTML + Tailwind/daisyUI。
3. 图标使用本地 SVG/离线图标映射，禁止依赖运行时网络请求。
4. Modal/Slideover 必须保留：
   - `v-model` 开关；
   - Esc 关闭；
   - 点击遮罩关闭策略；
   - 打开后的焦点处理；
   - 背景滚动锁定；
   - 合理的 dialog/aria 语义；
   - 深色模式样式。
5. 把 `useModal().open(Dialog, props)` 收敛为项目自己的确认弹窗服务，例如 `useConfirmDialog()` + 根级 `DialogHost`，不要把业务逻辑绑回某个框架插件。
6. 每替换一类组件就运行相关组件测试；Modal、设置弹窗、课程目录、结算页和暂停页单独做交互验收。

门禁：源码中不再出现 `U*` Nuxt UI 标签、`useModal`、`#imports`；Nuxt 构建仍可暂时保留作对照。

### Phase 3：并行建立 Vite 应用壳

**目的：** 在保留 Nuxt 构建回退能力的同时，让同一套业务代码可由 Vite 启动。

新增/修改：

1. 新增 `index.html`，迁移页面 title 和 favicon。
2. 新增 `main.ts`：
   - 导入全局 CSS；
   - 创建 Vue app；
   - 创建并安装 Pinia；
   - 安装 Router；
   - 初始化 HTTP；
   - 挂载根组件。
3. 新增 `router/index.ts`，显式声明第 2.2 节的 4 条路由。
4. 把 `app.vue` / `layouts/default.vue` 中的 `NuxtLayout`、`NuxtPage` 改为默认布局和 `RouterView`，确保练习页仍占满单个 viewport。
5. 新增 `vite.config.ts`：
   - Vue SFC 插件；
   - `~`、`@` 指向 `frontend` 根目录；
   - 开发端口默认 `3002`；
   - 必要时添加 `/api` proxy，但默认仍沿用直连 Spring Boot 的 API Base。
6. 新增 `env.d.ts`，把环境变量改为：

   ```env
   VITE_API_BASE="http://localhost:8080"
   ```

7. 显式接入 Tailwind：
   - 新增 PostCSS 配置；
   - 把当前 `tailwind.config.js` 的空 `content: []` 改为覆盖 `index.html`、`app.vue`、`layouts/`、`pages/`、`components/` 的明确路径；
   - 保留 daisyUI、暗色模式、自定义字体、动画和滚动条扩展。
8. 在 `package.json` 临时增加并行命令，先不要覆盖 Nuxt 默认命令：

   ```text
   dev:vite / build:vite / preview:vite / type-check:vite
   ```

门禁：Nuxt 与 Vite 两套入口同时可构建；Vite 下三条业务路由可导航、刷新和直接打开。

### Phase 4：把测试与类型检查切到 Vite

**目的：** 去掉测试层对 Nuxt 的依赖，建立最终 CI 门禁。

任务：

1. `vitest.config.ts` 改用普通 `vitest/config` + Vue SFC 插件 + alias。
2. 删除 `mockNuxtImport`，改为对本地 composable/service 的常规 `vi.mock`。
3. 把 `tests/config/nuxt-dependency-baseline.test.ts` 替换为 Vite/Vue 依赖边界测试：
   - 核心版本保持锁定；
   - `package.json` 不得再含 Nuxt/Nitro 依赖；
   - 源码不得导入 `#app`、`#imports`、`nuxt/app`。
4. `type-check` 改为 `vue-tsc --noEmit`，补齐独立 `tsconfig`，不再 extends `.nuxt/tsconfig.json`。
5. 保留现有业务测试，并为 Router、根启动和本地弹窗补测试。

门禁：

```bash
pnpm test
pnpm type-check:vite
pnpm build:vite
```

全部通过，且没有依赖 Nuxt 测试环境才能运行的用例。

### Phase 5：切换默认命令并删除 Nuxt

**目的：** 完成最终切换，清除双构建状态。

任务：

1. 把默认脚本切换为 Vite：

   ```text
   dev       → vite --port 3002
   build     → vite build
   preview   → vite preview --port 3003
   type-check→ vue-tsc --noEmit
   ```

2. 删除不再需要的依赖和 overrides：
   - `nuxt`
   - `@nuxt/ui`
   - `@nuxt/image`
   - `@nuxt/kit`
   - `@nuxt/schema`
   - `@nuxt/test-utils`
   - `@vueuse/nuxt`
   - `@hypernym/nuxt-anime`
   - `nitropack` 及 Nuxt/Nitro 专用 overrides
3. 删除 Nuxt 专属文件：
   - `nuxt.config.ts`
   - `app.config.ts`
   - `plugins/http.ts`
   - `plugins/pinia.ts`
   - `server/tsconfig.json`
4. 重新执行 `pnpm install`，提交新的 `pnpm-lock.yaml`。
5. 删除本地生成的 `.nuxt/`、`.output/` 后重跑全量验证，确保构建没有偷用旧产物。
6. 把临时 `*:vite` 命令提升为默认命令后，删除 Nuxt 过渡命令。

静态清理检查：

```bash
rg -n "#imports|nuxt/app|NuxtLayout|NuxtPage|NuxtLink|NuxtImg|defineNuxt|useNuxtApp|useRuntimeConfig|navigateTo" \
  frontend/{api,components,composables,layouts,pages,router,store,main.ts,app.vue} \
  --glob '!node_modules/**' --glob '!dist/**'
```

预期无结果。

### Phase 6：端到端验收与文档收口

自动验证：

```bash
cd frontend
pnpm test
pnpm type-check
VITE_API_BASE=http://localhost:8080 pnpm build
pnpm preview --host 0.0.0.0 --port 3003
```

配合后端逐项验收：

1. `/` 正确跳到 `/course-pack`。
2. `/course-pack` 能读取并显示技术栈。
3. `/course-pack/:id` 能显示小节、模式徽标和术语数量。
4. `/game/:coursePackId/:id` 直接打开与刷新都成功，不出现静态服务器 404。
5. WORD 模式仍是中文释义 → 输入英文；`prefix` 不参与输入和发音。
6. Enter 提交、错词修复、上一词/下一词、暂停、重置、设置和结算行为正常。
7. 当前 statement 的解释、示例、源码和个人笔记正确切换并可保存。
8. 发音、按键音、正确/错误音效和计时器动画正常。
9. 深色模式、桌面宽屏、移动端和练习页单 viewport 布局与迁移前截图一致。
10. 浏览器控制台没有 Vue warning、路由 warning、未注册组件或 404 资源。

文档更新：

- `AGENTS.md`
- 根 `README.md`
- `frontend/README.md`
- `docs/project-overview.md`
- `docs/roadmap.md`
- `.env.example`
- 必要的部署说明

把所有 `Nuxt 3`、`.output/server`、`API_BASE` 和旧端口命令更新为 Vue 3 + Vite、`dist/`、`VITE_API_BASE` 和 3002/3003 新命令。

## 五、关键风险与控制措施

| 风险 | 级别 | 控制措施 |
|---|---:|---|
| Nuxt UI 替换后弹窗焦点、Esc、遮罩或滚动锁退化 | 高 | 建本地组件测试 + 浏览器逐项验收，不只比外观 |
| Tailwind 在 Vite 下因 `content: []` 产出空样式 | 高 | Phase 3 显式配置扫描路径，并比对关键页面截图 |
| 静态部署直接刷新动态路由 404 | 高 | 部署服务器必须配置 SPA fallback 到 `index.html` |
| 环境变量从 `API_BASE` 改为 `VITE_API_BASE` 后接口指向错误 | 高 | 启动时显式注入，增加配置测试和 `.env.example` |
| Nuxt 自动导入消失导致运行时未注册组件 | 中 | 全部显式 import，`vue-tsc` 和控制台零 warning 作为门禁 |
| route name 变化导致练习页布局/导航栏分支错误 | 中 | 显式固定原 route name，并加 Router 测试 |
| 迁移中混入遗留登录/会员修复，扩大范围 | 中 | Phase 0 只做“删除不可达”或“修绿现状”，不恢复功能 |
| 一次性删除 Nuxt 后回退困难 | 中 | Phase 3 保留双入口，Vite 达标后才执行 Phase 5 |

## 六、部署契约变化

迁移前的 `nuxt build` 会生成可由 Node 启动的 `.output/server/index.mjs`；迁移后的 `vite build` 生成纯静态 `dist/`。

- 本地/验收预览：`pnpm preview --host 0.0.0.0 --port 3003`。
- 正式部署：用 Nginx、静态托管平台或等价静态服务器发布 `dist/`。
- 静态服务器必须把未知前端路由回退到 `index.html`，否则直接访问 `/game/...` 会 404。
- `vite preview` 只用于本地验收，不作为正式生产服务器。

如果现有运行环境强依赖“Node 进程启动前端”，需要在 Phase 6 另选一个轻量静态服务器并记录启动命令；这属于部署适配，不需要保留 Nuxt/Nitro。

## 七、完成定义

满足以下条件才算迁移完成：

- [x] `package.json`、源码和测试中没有 Nuxt/Nitro 运行依赖。
- [x] `pnpm test` 全绿。
- [x] `pnpm type-check` 全绿。
- [x] `VITE_API_BASE=http://localhost:8080 pnpm build` 成功并生成 `dist/`。
- [x] 3 条业务页面及 `/` 重定向均支持站内导航、直接访问和刷新。
- [x] 核心打字流程、详情侧栏、笔记、发音、弹窗和暗色模式无回归。
- [x] Tailwind/daisyUI 样式完整，控制台无未注册组件和资源错误。
- [x] AGENTS、README、项目概述、环境变量和部署命令全部更新。
- [x] Nuxt 双入口与过渡文件已删除，没有长期保留“两套壳”。

## 八、建议提交序列

```text
test: establish green Nuxt migration baseline
refactor: decouple frontend services from Nuxt APIs
refactor: replace Nuxt UI with local Vue components
feat: add parallel Vue Vite application shell
test: migrate frontend tests from Nuxt to Vite
build: remove Nuxt and switch default scripts to Vite
docs: update frontend runtime and deployment guide
```

这个顺序保证最难的 UI 与隐式依赖先在原 Nuxt 壳里消化；等 Vite 入口上线时，剩余工作主要是启动、路由、配置和构建切换，而不是同时改业务行为。
