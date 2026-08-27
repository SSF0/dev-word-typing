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
        <MainTool />
        <MainGame />
      </section>
    </div>

    <aside
      id="practice-detail-panel"
      class="detail-panel xl:border-l xl:border-gray-200 xl:dark:border-gray-700"
      aria-label="知识点详情"
      :aria-hidden="!detailOpen"
      :inert="!detailOpen"
      data-test="detail-panel"
    >
      <MainAnnotationPanel />
    </aside>

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
import { onBeforeUnmount, onMounted } from "vue";

import MainAnnotationPanel from "~/components/main/AnnotationPanel.vue";
import MainGame from "~/components/main/Game.vue";
import MainPracticeWordRail from "~/components/main/PracticeWordRail.vue";
import MainTool from "~/components/main/Tool.vue";
import { usePracticeDetail } from "~/composables/main/usePracticeDetail";

const { detailOpen, closeDetail } = usePracticeDetail();

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
  --detail-width: 40rem;
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
  height: 100%;
  margin-inline: auto;
}

.practice-pane {
  width: 100%;
}

.practice-word-column {
  align-self: start;
  min-width: 0;
  height: 100%;
  max-height: 100%;
}

.detail-panel {
  position: fixed;
  z-index: 40;
  top: 3rem;
  right: 0;
  bottom: 0;
  width: min(var(--detail-width), calc(100vw - 1rem));
  padding: 0.75rem;
  background: white;
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  clip-path: inset(0 0 0 100%);
  transition:
    clip-path 0.35s cubic-bezier(0.16, 1, 0.3, 1),
    opacity 0.2s ease,
    visibility 0s linear 0.35s;
}

:global(.dark) .detail-panel {
  background: rgb(17 24 39);
}

.is-detail-open .detail-panel {
  animation: detail-panel-enter 0.35s cubic-bezier(0.16, 1, 0.3, 1);
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  clip-path: inset(0);
  transition-delay: 0s;
}

.detail-backdrop {
  position: fixed;
  z-index: 30;
  top: 3rem;
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
  .practice-workspace {
    overflow-x: visible;
  }

  .practice-track {
    position: relative;
    left: 50%;
    grid-template-columns: var(--word-rail-width) var(--workspace-gap) minmax(0, 1fr) 0 minmax(0, 0fr);
    gap: 0;
    width: var(--closed-workspace-width);
    margin-inline: 0;
    transform: translateX(-50%);
    transition:
      width 0.35s cubic-bezier(0.16, 1, 0.3, 1),
      grid-template-columns 0.35s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .practice-pane {
    grid-column: 3;
    width: var(--practice-width);
  }

  .practice-word-column {
    grid-column: 1;
    width: var(--word-rail-width);
  }

  .is-detail-open .practice-track {
    grid-template-columns: var(--word-rail-width) var(--workspace-gap) minmax(0, 1fr) var(--workspace-gap) minmax(0, 1fr);
    width: calc(100% - 2rem);
  }

  .is-detail-open .practice-pane {
    width: 100%;
  }

  .is-detail-open .practice-word-column {
    width: var(--word-rail-width);
  }

  .is-detail-open .detail-panel {
    position: absolute;
    top: 0;
    right: 1rem;
    bottom: auto;
    width: calc(50% - 8rem);
    height: 100%;
    max-height: 100%;
    padding: 0;
    clip-path: none;
  }

  .detail-backdrop {
    display: none;
  }
}

@media (min-width: 1664px) {
  .is-detail-open .practice-track {
    width: var(--open-workspace-width);
  }

  .is-detail-open .detail-panel {
    right: calc((100% - var(--open-workspace-width)) / 2);
    width: 44rem;
  }
}

@keyframes detail-panel-enter {
  from {
    opacity: 0;
    transform: translateX(2rem);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (prefers-reduced-motion: reduce) {
  .practice-track,
  .detail-panel,
  .detail-backdrop-enter-active,
  .detail-backdrop-leave-active {
    transition-duration: 0.01ms;
  }

  .is-detail-open .detail-panel {
    animation-duration: 0.01ms;
  }
}
</style>
