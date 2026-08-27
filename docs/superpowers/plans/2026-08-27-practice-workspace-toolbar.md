# Practice Workspace Toolbar Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move the lesson toolbar above the complete practice workspace and expose the useful reference-page controls for word practice.

**Architecture:** `PracticeWorkspace.vue` will own one full-width toolbar followed by a body that contains the word rail, practice pane, and optional detail panel. `Tool.vue` remains responsible for lesson navigation and global practice actions; its settings and pause actions become available in word mode and for guest users. The existing sound settings will be applied consistently to word pronunciation.

**Tech Stack:** Nuxt 3, Vue 3, Tailwind CSS, Nuxt UI, Vitest, Vue Test Utils

---

### Task 1: Specify the three-column toolbar layout

**Files:**
- Modify: `frontend/pages/game/tests/practice-layout.spec.ts`
- Test: `frontend/pages/game/tests/practice-layout.spec.ts`

- [x] **Step 1: Write the failing tests**

Add assertions that the workspace toolbar precedes a dedicated practice body, that the toolbar is outside the middle practice pane, and that the detail toggle remains visible with `aria-expanded="true"` while the panel is open.

- [x] **Step 2: Run test to verify it fails**

Run: `pnpm vitest run --config vitest.config.ts pages/game/tests/practice-layout.spec.ts`

Expected: FAIL because `workspace-toolbar` and `practice-body` do not exist and the detail toggle disappears when opened.

- [x] **Step 3: Implement the workspace layout**

Move `<MainTool>` above the content body, keep the detail toggle in its action slot for both states, wrap the rail/practice/detail content in `.practice-body`, and make `.workspace-toolbar` use the same closed/open width transitions as `.practice-track`.

- [x] **Step 4: Run test to verify it passes**

Run: `pnpm vitest run --config vitest.config.ts pages/game/tests/practice-layout.spec.ts`

Expected: PASS.

### Task 2: Expose functional top-right controls in word mode

**Files:**
- Create: `frontend/components/main/tests/tool.spec.ts`
- Modify: `frontend/components/main/Tool.vue`
- Modify: `frontend/components/main/Game.vue`
- Modify: `frontend/composables/main/englishSound/tests/index.spec.ts`
- Modify: `frontend/composables/main/englishSound/index.ts`

- [x] **Step 1: Write the failing toolbar test**

Mount `Tool.vue` with guest and Chinese-to-English mocks, assert that `game-settings`, `pause-game`, and `reset-course` controls render, and click the first two to assert their composable actions run.

- [x] **Step 2: Write the failing sound-settings test**

Set non-default toolbar options in Chinese-to-English mode, call `playSound`, and expect the configured `times`, `rate`, and `interval` to reach the audio helper.

- [x] **Step 3: Run tests to verify they fail**

Run: `pnpm vitest run --config vitest.config.ts components/main/tests/tool.spec.ts composables/main/englishSound/tests/index.spec.ts`

Expected: FAIL because settings and pause are gated, and word-mode pronunciation ignores toolbar options.

- [x] **Step 4: Implement the controls**

Render settings and pause as accessible buttons in all practice modes, mount the pause modal for guests, keep the learning timer authentication gate, and pass toolbar playback options in every mode.

- [x] **Step 5: Run tests to verify they pass**

Run: `pnpm vitest run --config vitest.config.ts components/main/tests/tool.spec.ts composables/main/englishSound/tests/index.spec.ts`

Expected: PASS.

### Task 3: Remove the duplicate detail close action and verify

**Files:**
- Modify: `frontend/components/main/AnnotationPanel.vue`
- Modify: `frontend/components/main/tests/annotation-panel.spec.ts`

- [x] **Step 1: Write the failing test**

Assert that the detail panel header has no internal “收起详情” control because the persistent top toolbar owns the open/close action.

- [x] **Step 2: Run test to verify it fails**

Run: `pnpm vitest run --config vitest.config.ts components/main/tests/annotation-panel.spec.ts`

Expected: FAIL while the internal close button remains.

- [x] **Step 3: Remove the duplicate control**

Delete the internal close button and the unused `close` emit from `AnnotationPanel.vue`.

- [ ] **Step 4: Run focused tests and full verification**

Run: `pnpm vitest run --config vitest.config.ts pages/game/tests/practice-layout.spec.ts components/main/tests/tool.spec.ts components/main/tests/annotation-panel.spec.ts composables/main/englishSound/tests/index.spec.ts`

Run: `pnpm run type-check`

Run: `pnpm run build`

Expected: all commands exit successfully.

Verification note: focused feature tests and the production build pass. Full-suite verification is partially blocked by pre-existing failures in `services/tests/auth.spec.ts` and unrelated type errors in the user/Summary files.
