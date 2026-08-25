<template>
  <header
    class="w-full px-5 font-customFont transition-all duration-300 ease-linear"
    :class="{
      'sticky top-0 z-10': isStickyNavBar,
      'glass bg-gradient-to-r from-transparent via-white/10 to-transparent shadow-md':
        isStickyNavBar && isScrolled,
    }"
  >
    <div class="mx-auto max-w-screen-xl">
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
            <h1 class="text-wrap text-2xl font-extrabold leading-normal dark:text-white">
              单词打字通
            </h1>
          </div>
        </NuxtLink>

        <div class="flex items-center gap-2">
          <button
            aria-label="Dark mode"
            class="btn btn-sm btn-ghost"
            @click="toggleDarkMode"
          >
            <UIcon
              :name="darkMode === Theme.DARK ? 'i-ph-sun' : 'i-ph-moon'"
              class="h-5 w-5"
            />
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useWindowScroll } from "@vueuse/core";
import { computed } from "vue";
import { useRoute } from "vue-router";

import { Theme, useDarkMode } from "~/composables/darkMode";

const { darkMode, toggleDarkMode } = useDarkMode();
const route = useRoute();
const { y } = useWindowScroll();

const SCROLL_THRESHOLD = 8;
const isStickyNavBar = computed(() =>
  ["course-pack-id", "game-coursePackId-id"].includes(route.name as string),
);
const isScrolled = computed(() => y.value >= SCROLL_THRESHOLD);
</script>