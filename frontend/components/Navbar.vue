<template>
  <header
    class="w-full px-5 font-customFont transition-all duration-300 ease-linear"
    :class="{
      'shrink-0 border-b border-gray-200 dark:border-gray-700': isGameRoute,
      'sticky top-0 z-10': isStickyNavBar,
      'glass bg-gradient-to-r from-transparent via-white/10 to-transparent shadow-md':
        isStickyNavBar && isScrolled,
    }"
  >
    <div class="mx-auto max-w-screen-xl">
      <template v-if="isGameRoute">
        <div
          class="flex h-12 items-center justify-between"
          data-test="game-navbar"
        >
          <NuxtLink
            to="/course-pack"
            class="btn btn-ghost btn-sm gap-2 px-2"
            aria-label="返回首页"
            title="返回首页"
            data-test="home-link"
          >
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="h-5 w-5"
              aria-hidden="true"
            >
              <path d="m15 18-6-6 6-6" />
              <path d="M9 12h11" />
            </svg>
            <span class="hidden sm:inline">返回首页</span>
          </NuxtLink>

          <div class="flex items-center gap-1 sm:gap-2">
            <button
              class="btn btn-ghost btn-sm px-2"
              :class="{ 'text-fuchsia-500': detailOpen }"
              type="button"
              aria-controls="practice-detail-panel"
              :aria-expanded="detailOpen"
              :aria-label="detailOpen ? '收起知识点详情' : '查看知识点详情'"
              :title="detailOpen ? '收起知识点详情' : '查看知识点详情'"
              data-test="detail-toggle"
              @click="toggleDetail"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="h-6 w-6"
                aria-hidden="true"
              >
                <path d="M4 5.5A2.5 2.5 0 0 1 6.5 3H11v16H6.5A2.5 2.5 0 0 0 4 21.5z" />
                <path d="M20 5.5A2.5 2.5 0 0 0 17.5 3H13v16h4.5a2.5 2.5 0 0 1 2.5 2.5z" />
              </svg>
            </button>

            <button
              class="btn btn-ghost btn-sm px-2"
              type="button"
              aria-label="游戏设置"
              title="游戏设置"
              data-test="game-settings"
              @click="openGameSettingModal"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="h-6 w-6"
                aria-hidden="true"
              >
                <circle cx="12" cy="12" r="3" />
                <path d="M19.4 15a1.7 1.7 0 0 0 .34 1.88l.06.06-2.86 2.86-.06-.06A1.7 1.7 0 0 0 15 19.4a1.7 1.7 0 0 0-1 .6 1.7 1.7 0 0 0-.4 1.1V21H9.5v-.1A1.7 1.7 0 0 0 8.4 19.4a1.7 1.7 0 0 0-1.88.34l-.06.06-2.86-2.86.06-.06A1.7 1.7 0 0 0 4 15a1.7 1.7 0 0 0-1.6-1H2v-4h.4A1.7 1.7 0 0 0 4 9a1.7 1.7 0 0 0-.34-1.88l-.06-.06L6.46 4.2l.06.06A1.7 1.7 0 0 0 8.4 4a1.7 1.7 0 0 0 1-1.6V2h4.1v.4A1.7 1.7 0 0 0 15 4a1.7 1.7 0 0 0 1.88-.34l.06-.06 2.86 2.86-.06.06A1.7 1.7 0 0 0 19.4 9a1.7 1.7 0 0 0 1.6 1h1v4h-1a1.7 1.7 0 0 0-1.6 1Z" />
              </svg>
            </button>

            <button
              class="btn btn-ghost btn-sm px-2"
              type="button"
              aria-label="暂停游戏"
              title="暂停游戏"
              data-test="pause-game"
              @click="pauseGame"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                class="h-6 w-6"
                aria-hidden="true"
              >
                <path d="M8 5v14M16 5v14" />
              </svg>
            </button>

            <button
              class="btn btn-ghost btn-sm px-2"
              type="button"
              aria-label="重置当前课程进度"
              title="重置当前课程进度"
              data-test="reset-course"
              @click="handleDoAgain"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="h-6 w-6"
                aria-hidden="true"
              >
                <path d="M3 12a9 9 0 1 0 3-6.7L3 8" />
                <path d="M3 3v5h5" />
              </svg>
            </button>
          </div>
        </div>
      </template>

      <template v-else>
        <div class="flex h-16 items-center justify-between">
          <NuxtLink to="/course-pack">
            <div class="logo flex items-center">
              <img
                width="48"
                height="48"
                class="mr-6 hidden overflow-hidden rounded-md md:block"
                src="/logo.png"
                alt="word-typing-logo"
              />
              <h1
                class="text-wrap text-2xl font-extrabold leading-normal dark:text-white"
                data-test="brand-title"
              >
                单词打字通
              </h1>
            </div>
          </NuxtLink>

          <div class="flex items-center gap-2">
            <button
              aria-label="Dark mode"
              class="btn btn-sm btn-ghost"
              data-test="dark-mode"
              @click="toggleDarkMode"
            >
              <UIcon
                :name="darkMode === Theme.DARK ? 'i-ph-sun' : 'i-ph-moon'"
                class="h-5 w-5"
              />
            </button>
          </div>
        </div>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useModal } from "#imports";
import { useWindowScroll } from "@vueuse/core";
import { computed } from "vue";
import { useRoute } from "vue-router";

import Dialog from "~/components/common/Dialog.vue";
import { useQuestionInput } from "~/components/main/QuestionInput/questionInputHelper";
import { courseTimer } from "~/composables/courses/courseTimer";
import { Theme, useDarkMode } from "~/composables/darkMode";
import { useGameMode } from "~/composables/main/game";
import { clearQuestionInput } from "~/composables/main/question";
import { useGamePause } from "~/composables/main/useGamePause";
import { useGameSetting } from "~/composables/main/useGameSetting";
import { usePracticeDetail } from "~/composables/main/usePracticeDetail";
import { useCourseStore } from "~/store/course";

const { darkMode, toggleDarkMode } = useDarkMode();
const route = useRoute();
const { y } = useWindowScroll();
const courseStore = useCourseStore();
const modal = useModal();
const { focusInput } = useQuestionInput();
const { showQuestion } = useGameMode();
const { pauseGame } = useGamePause();
const { openGameSettingModal } = useGameSetting();
const { detailOpen, toggleDetail } = usePracticeDetail();

const SCROLL_THRESHOLD = 8;
const isGameRoute = computed(() => route.name === "game-coursePackId-id");
const isStickyNavBar = computed(() => route.name === "course-pack-id");
const isScrolled = computed(() => y.value >= SCROLL_THRESHOLD);

function handleDoAgain() {
  modal.open(Dialog, {
    title: "重置进度",
    content: "是否确认重置当前课程进度？",
    showCancel: true,
    showConfirm: true,
    async onCancel() {
      setTimeout(focusInput, 300);
    },
    async onConfirm() {
      courseStore.doAgain();
      clearQuestionInput();
      showQuestion();
      courseTimer.reset();
      setTimeout(focusInput, 300);
    },
  });
}
</script>
