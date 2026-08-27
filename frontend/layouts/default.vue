<template>
  <div
    class="w-full bg-white text-slate-600 transition-colors dark:bg-theme-dark dark:text-slate-300"
    :class="{
      'h-dvh overflow-hidden': isGameRoute,
      'h-full': !isGameRoute,
    }"
  >
    <div
      class="m-auto flex flex-col items-center"
      :class="{
        'h-full min-h-0': isGameRoute,
        'h-fit min-h-screen': !isGameRoute,
      }"
    >
      <Navbar />
      <!-- 多一层内容的横向内边距是为了和 Navbar 对齐 -->
      <div
        class="flex w-full flex-1 px-5"
        :class="{ 'min-h-0 overflow-hidden': isGameRoute }"
      >
        <div
          class="mx-auto flex w-full max-w-screen-xl flex-1"
          :class="{ 'min-h-0': isGameRoute }"
        >
          <RouterView />
        </div>
      </div>
      <Footer v-if="!isGameRoute"></Footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";

import Footer from "~/components/Footer.vue";
import Navbar from "~/components/Navbar.vue";

const route = useRoute();
const isGameRoute = computed(() => route.name === "game-coursePackId-id");
</script>
