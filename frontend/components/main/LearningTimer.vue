<template>
  <div class="flex items-center font-sans text-gray-300 dark:text-gray-500">
    <div
      ref="clockIcon"
      class="mr-1 flex items-center justify-center"
    >
      <AppIcon
        name="alarm"
        class="h-8 w-8"
      />
    </div>
    <p class="text-lg font-bold">{{ formattedTime }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from "vue";

import AppIcon from "~/components/ui/AppIcon.vue";
import { useLearningTimeTracker } from "~/composables/main/learningTimeTracker";
import { useGamePause } from "~/composables/main/useGamePause";
import { useGameStore } from "~/store/game";

const gameStore = useGameStore();

const { pauseGame, enableAutoPauseCheck, disableAutoPauseCheck } = useGamePause();

const { totalSeconds, stopTracking } = useLearningTimeTracker();
const clockIcon = ref<HTMLElement | null>(null);

const formattedTime = computed(() => {
  const hours = Math.floor(totalSeconds.value / 3600);
  const minutes = Math.floor((totalSeconds.value % 3600) / 60);
  const seconds = totalSeconds.value % 60;
  return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
});

function animateClock() {
  clockIcon.value?.animate(
    [
      { transform: "translateY(0) rotate(0) scale(1)" },
      { transform: "translateY(-4px) rotate(-5deg) scale(1.05)" },
      { transform: "translateY(4px) rotate(5deg) scale(1.1)" },
      { transform: "translateY(-4px) rotate(-5deg) scale(1.05)" },
      { transform: "translateY(4px) rotate(5deg) scale(1)" },
      { transform: "translateY(0) rotate(0) scale(1)" },
    ],
    { duration: 800, easing: "ease-in-out" },
  );
}

watch(totalSeconds, (newValue) => {
  if (newValue % 60 === 0 && newValue !== 0) {
    animateClock();
  }
});

function handleVisibilityChange() {
  if (gameStore.isGamePaused()) {
    return;
  }

  if (document.hidden) {
    stopTracking();
    pauseGame();
  }
}

function handleBeforeunload() {
  if (gameStore.isGamePaused()) {
    return;
  }

  stopTracking();
}

onMounted(() => {
  document.addEventListener("visibilitychange", handleVisibilityChange);
  window.addEventListener("beforeunload", handleBeforeunload);
  enableAutoPauseCheck();
});

onUnmounted(() => {
  document.removeEventListener("visibilitychange", handleVisibilityChange);
  window.removeEventListener("beforeunload", handleBeforeunload);
  disableAutoPauseCheck();
});
</script>
