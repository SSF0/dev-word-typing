# 练习页左右布局改版：默认居中打字 + 详情滑入设计

日期：2026-08-26

## 目标

改造前端练习页 `pages/game/[coursePackId]/[id].vue` 的布局与交互：

- **默认态**：打字区（`MainGame`）**居中**展示，专注限时打字，无右侧干扰。
- **详情开关**：打字区上方（`MainTool` 区域）放「详情」按钮，点击**切换**详情面板。
- **推挤动画**：点开详情时，详情面板从**右侧滑入**，同时主打字区被**向左推动**（带弹性/过渡）；关闭时打字区**弹回居中**、面板滑出。
- **详情面板内容**：直接展示全部（源码 + 注释 + 个人笔记），内容超出则**滚动显示**，不再保留组件内部的「展开/收起」折叠按钮。

## 现状

`pages/game/[coursePackId]/[id].vue` 当前布局：

```html
<MainTool />
<div class="flex w-full flex-col gap-4 lg:flex-row">
  <div class="min-w-0 flex-1"><MainGame /></div>
  <aside class="w-full shrink-0 lg:w-80"><MainAnnotationPanel /></aside>
</div>
```

详情面板 `MainAnnotationPanel`（`components/main/AnnotationPanel.vue`）始终可见，内部还有「展开/收起」折叠；Meizu 该面板需要小幅调整为「不折叠、直接展示」。（Nuxt 组件用目录前缀自动注册，`MainGame` = `components/main/Game.vue`，`MainAnnotationPanel` = `components/main/AnnotationPanel.vue`。）

## 设计

### 布局容器（`[id].vue`）

- 新增状态 `showDetail = ref(false)`。
- 外层行容器用 `<TransitionGroup>`（tag 为 `div`，横向 flex）。
- 两个子节点：
  - 打字区容器：`flex-1`，开详情时字号 content 向左的位移由左侧 padding/位移驱动（详见「动画」）。
  - 详情面板：`<Transition>` 包裹的 `aside`，`v-show="showDetail"`，宽 `w-80`，隐藏时 `translateX(100%)` / 溢出隐藏。
- 详情按钮放在 `MainTool` 行：切换 `showDetail`。

### 2. 动画实现（推挤 - 弹回）

推荐做法：**不切换布局模式，而是对打字区设置过渡的 `padding-right`（或 `translateX`）、详情面板设置 `translateX`**，两处都加 `transition`，从而形成「打字区被推到左边、详情滑入」的效果：

- 详情面板 `aside`：
  - `transition: transform .35s cubic-bezier(.16,1,.3,1)`。
  - 关闭态：`translateX(110%) translateY(0)`（移出屏幕外）、`opacity:0`、隐藏（避免占位）。
  - 打开态：`translateX(0)`、`opacity:1`。
- 打字区容器：
  - `transition: margin-right .35s cubic-bezier(.16,1,.3,1)`。
  - 关闭态：`margin-right: 0`（居中）。
  - 打开态：`margin-right: 20rem`（等价 w-80 面板宽度），配合面板位移形成“被推向左”之感。

用 flex 布局时直接操作 `margin` / `transform`，最稳妥、不和现有 `min-w-0 flex-1` 冲突。用 `<Transition>`，但为避免 JS 组件卸载影响「推挤」，采用更可控的方案：**用响应式 class 切换 + CSS transition**（`showDetail` 驱动 class），不依赖组件生命周期。

- 外层：`class="flex w-full gap-4 ..." `，详情 aside 始终在 DOM（`aria-hidden`/`invisible` 控制），用 `transform` 动效出入场，打字区 `transition` margin-right。纯 CSS、无重挂载、动画平滑。

### 3. 详情面板内容改版（`AnnotationPanel.vue`）

- 移除 `collapsed` 逻辑和「展开/收起」按钮（`collapsed`/`collapse` 相关）。
- 面板整体高度外卖过高时内部滚动（`overflow-y-auto`，`max-h-...`）。
- 保留：节点标题徽章、源码 `pre`、注释块、个人笔记（textarea + 保存）。
- 顶部可加个「关闭」按钮（`@click="$emit('close')"`），父组件据此 `showDetail=false`。（可选，若工具栏已有详确定。若不加，则把开关放工具栏按钮。）

### 4. 开关按钮

- 在 `[id].vue` 的 `MainTool` 之后新增一个「详情」按钮（与打字区同容器对齐），`@click="showDetail = !showDetail"`。

## 边界 / 不做

- 不做详情独立路由，保持滑入动画。
- 不在每道题结束后自动弹出。
- 不打字流程内部逻辑（`MainGame`、`composables/main/*`、store）不动。
- 后端接口/数据模型不动。

## 测试

- 无后端改动。
- 前端跑 `pnpm type-check`。
- 现有 vitest 单测（`pnpm test:unit:run`）应保持通过；新增布局状态可补一个轻量单测（可选）。
- 手测：打开详情 → 打字区左移 + 面板滑入；关详情 → 弹回居中；面板内容超高时滚动。

## 涉及文件

- `frontend/pages/game/[coursePackId]/[id].vue` — 布局容器 + showDetail + 动画 + 详情按钮。
- `frontend/components/main/AnnotationPanel.vue` — 去除折叠、面板滚动、关闭入口。