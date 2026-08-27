<template>
  <template v-if="isDictationMode()">
    <ModeDictationMode />
  </template>
  <template v-else-if="isChineseToEnglishMode()">
    <ModeChineseToEnglishMode />
  </template>

  <MainLearningTimer v-if="isAuthenticated()"></MainLearningTimer>
  <MainTips />
  <MainSummary />
  <GamePauseModal />
  <MainGameSettingModal />
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from "vue";

import GamePauseModal from "~/components/main/GamePauseModal.vue";
import MainGameSettingModal from "~/components/main/GameSettingModal.vue";
import MainLearningTimer from "~/components/main/LearningTimer.vue";
import MainSummary from "~/components/main/Summary.vue";
import MainTips from "~/components/main/Tips.vue";
import ModeChineseToEnglishMode from "~/components/mode/chineseToEnglish/ChineseToEnglishMode.vue";
import ModeDictationMode from "~/components/mode/dictation/DictationMode.vue";
import { courseTimer } from "~/composables/courses/courseTimer";
import { useGamePlayMode } from "~/composables/user/gamePlayMode";
import { isAuthenticated } from "~/services/auth";
import { useGameStore } from "~/store/game";

const { isChineseToEnglishMode, isDictationMode } = useGamePlayMode();
const gameStore = useGameStore();

onMounted(() => {
  courseTimer.reset();
  gameStore.startGame();
});

onUnmounted(() => {
  gameStore.exitGame();
});
</script>
