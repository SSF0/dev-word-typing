<template>
  <div
    class="practice-workspace flex min-h-0 flex-1 flex-col"
    :class="{ 'is-detail-open': detailOpen }"
    :data-state="detailOpen ? 'open' : 'closed'"
    data-test="practice-workspace"
  >
    <div class="practice-track min-h-0 flex-1">
      <MainPracticeWordRail class="practice-word-column" />

      <section class="practice-pane flex h-full min-h-0 flex-col">
        <MainTool>
          <template #actions>
            <button
              class="btn btn-outline btn-xs whitespace-nowrap"
              type="button"
              aria-controls="practice-detail-panel"
              :aria-expanded="detailOpen"
              data-test="detail-toggle"
              @click="toggleDetail"
            >
              {{ detailOpen ? "收起详情" : "查看详情" }}
            </button>
          </template>
        </MainTool>

        <MainGame />
      </section>

      <aside
        id="practice-detail-panel"
        class="detail-panel"
        aria-label="知识点详情"
        :aria-hidden="!detailOpen"
        :inert="!detailOpen"
        data-test="detail-panel"
      >
        <MainAnnotationPanel @close="closeDetail" />
      </aside>
    </div>

    <Transition name="detail-backdrop">
      <button
        v-if="detailOpen"
        class="detail-backdrop"
        type="button"
        aria-label="关闭知识点详情"
        data-test="detail-backdrop"
        @click="closeDetail"
      ></button>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";

import MainAnnotationPanel from "~/components/main/AnnotationPanel.vue";
import MainGame from "~/components/main/Game.vue";
import MainPracticeWordRail from "~/components/main/PracticeWordRail.vue";
import MainTool from "~/components/main/Tool.vue";

const detailOpen = ref(false);

function toggleDetail() {
  detailOpen.value = !detailOpen.value;
}

function closeDetail() {
  detailOpen.value = false;
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === "Escape" && detailOpen.value) {
    closeDetail();
  }
}

onMounted(() => {
  window.addEventListener("keydown", handleKeydown);
});

onBeforeUnmount(() => {
  window.removeEventListener("keydown", handleKeydown);
});
</script>

<style scoped>
.practice-workspace {
  --practice-width: 56rem;
  --word-rail-width: 12rem;
  --detail-width: 32rem;
  --workspace-gap: 1rem;
  --closed-workspace-width: 69rem;
  --open-workspace-width: 102rem;

  position: relative;
  width: 100%;
  overflow-x: clip;
}

.practice-track {
  display: grid;
  grid-template-columns: minmax(7.75rem, 10rem) minmax(0, 1fr);
  align-items: stretch;
  gap: 0.75rem;
  width: 100%;
  margin-inline: auto;
}

.practice-pane {
  width: 100%;
}

.practice-word-column {
  align-self: stretch;
  min-width: 0;
}

.detail-panel {
  position: fixed;
  z-index: 40;
  top: 4rem;
  right: 0;
  bottom: 0;
  width: min(var(--detail-width), calc(100vw - 1rem));
  padding: 0.75rem;
  background: white;
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  transform: translateX(calc(100% + 1rem));
  transition:
    transform 0.35s cubic-bezier(0.16, 1, 0.3, 1),
    opacity 0.2s ease,
    visibility 0s linear 0.35s;
}

:global(.dark) .detail-panel {
  background: rgb(17 24 39);
}

.is-detail-open .detail-panel {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  transform: translateX(0);
  transition-delay: 0s;
}

.detail-backdrop {
  position: fixed;
  z-index: 30;
  top: 4rem;
  right: 0;
  bottom: 0;
  left: 0;
  border: 0;
  background: rgb(15 23 42 / 35%);
  cursor: default;
}

.detail-backdrop-enter-active,
.detail-backdrop-leave-active {
  transition: opacity 0.2s ease;
}

.detail-backdrop-enter-from,
.detail-backdrop-leave-to {
  opacity: 0;
}

@media (min-width: 1280px) {
  .practice-track {
    grid-template-columns: var(--word-rail-width) var(--practice-width);
    gap: var(--workspace-gap);
    width: var(--closed-workspace-width);
  }

  .practice-pane {
    width: var(--practice-width);
  }

  .practice-word-column {
    width: var(--word-rail-width);
  }
}

@media (min-width: 1700px) {
  .practice-workspace {
    overflow-x: visible;
  }

  .practice-track {
    position: relative;
    left: 50%;
    margin-inline: 0;
    transform: translateX(-50%);
    transition: width 0.35s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .detail-panel {
    transform: none;
  }

  .practice-word-column {
    align-self: start;
    height: calc(100dvh - 8rem);
    max-height: calc(100dvh - 8rem);
  }

  .is-detail-open .practice-track {
    grid-template-columns: var(--word-rail-width) var(--practice-width) var(--detail-width);
    width: var(--open-workspace-width);
  }

  .is-detail-open .detail-panel {
    position: relative;
    z-index: auto;
    top: auto;
    right: auto;
    bottom: auto;
    grid-column: 3;
    width: var(--detail-width);
    height: calc(100dvh - 8rem);
    max-height: calc(100dvh - 8rem);
    padding: 0;
  }

  .detail-backdrop {
    display: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .practice-track,
  .detail-panel,
  .detail-backdrop-enter-active,
  .detail-backdrop-leave-active {
    transition-duration: 0.01ms;
  }
}
</style>
