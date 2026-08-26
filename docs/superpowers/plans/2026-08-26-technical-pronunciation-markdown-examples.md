# Technical Pronunciation and Markdown Usage Examples Implementation Plan

> **For Codex:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Make compound Java terms pronounce reliably as separate words and present every learning item usage example as a Markdown code block whose code lines carry concise Chinese explanations.

**Architecture:** Keep Youdao as the pronunciation source, but split identifier-shaped terms in the frontend before playback and sequence their audio URLs through the existing shared audio element. Keep `usageExample` as a plain database string, format seeded examples as fenced Markdown on the backend, and render the supported fenced-code subset with Vue nodes rather than raw HTML.

**Tech Stack:** Nuxt 3, Vue 3, Vitest, Spring Boot 3, Java 17, JUnit 5, AssertJ.

---

### Task 1: Split and sequence technical-term pronunciation

**Files:**
- Modify: `frontend/composables/main/englishSound/index.ts`
- Modify: `frontend/composables/main/englishSound/audio.ts`
- Test: `frontend/composables/main/englishSound/tests/index.spec.ts`

- [ ] Add failing tests for `RestController`, acronym-containing identifiers, plain words, and sequential playback URLs.
- [ ] Run the focused Vitest file and confirm the new assertions fail for the missing behavior.
- [ ] Add a pure technical-term splitter and a cancellable sequential audio player.
- [ ] Route current-statement and manually clicked technical terms through the sequence while leaving sentence reading unchanged.
- [ ] Re-run the focused tests and confirm they pass.

### Task 2: Render fenced Markdown examples safely

**Files:**
- Create: `frontend/components/main/UsageExampleMarkdown.vue`
- Modify: `frontend/components/main/AnnotationPanel.vue`
- Modify: `frontend/components/main/tests/annotation-panel.spec.ts`

- [ ] Change the component fixture to a fenced Java example and add failing assertions that fences are removed, the language is exposed, and line comments remain visible.
- [ ] Run the focused component test and confirm the new assertions fail.
- [ ] Implement a small renderer for explanatory text and fenced code blocks without `v-html` or a new package.
- [ ] Replace the raw usage-example `<pre>` with the renderer and preserve the existing collapsed details interaction.
- [ ] Re-run the focused component tests and confirm they pass.

### Task 3: Seed commented Markdown usage examples

**Files:**
- Modify: `backend/src/main/java/com/wordtyping/config/JavaLearningCatalog.java`
- Modify: `backend/src/test/java/com/wordtyping/config/DataSeederTest.java`

- [ ] Add failing seed tests requiring fenced Markdown for every usage example and a Chinese explanation on every non-empty code line.
- [ ] Add a focused assertion preserving the `@RequestBody` DTO and `@Valid` usage guidance.
- [ ] Run `DataSeederTest` and confirm the new format assertions fail.
- [ ] Centralize example formatting by language and annotate every displayed code line with concise context-aware Chinese guidance.
- [ ] Make the `@RequestBody` sample explicitly show DTO binding and validation on the relevant line.
- [ ] Re-run backend tests and confirm they pass.

### Task 4: Verify integration and regressions

**Files:**
- Verify only; do not modify unrelated dependency, lock, or development-performance files.

- [ ] Run all frontend unit tests.
- [ ] Run frontend type checking and production build.
- [ ] Run all backend tests.
- [ ] Inspect the local practice page to confirm compound playback requests individual words and the expanded usage example shows readable commented code.
- [ ] Review `git diff` and ensure unrelated pre-existing changes remain untouched.
