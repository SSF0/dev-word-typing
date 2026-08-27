<template>
  <div class="flex h-full items-center justify-center">
    <template v-if="isQuestion()">
      <ModeChineseToEnglishQuestion />
      <template v-if="isAnswerTip()">
        <MainAnswerTip />
      </template>
    </template>
    <template v-else-if="isAnswer()">
      <MainAnswer />
    </template>
  </div>
</template>

<script setup lang="ts">
import MainAnswer from "~/components/main/Answer.vue";
import MainAnswerTip from "~/components/main/AnswerTip.vue";
import ModeChineseToEnglishQuestion from "~/components/mode/chineseToEnglish/Question.vue";
import { useAnswerTip } from "~/composables/main/answerTip";
import { useGameMode } from "~/composables/main/game";

const { isAnswer, isQuestion } = useGameMode();
const { isAnswerTip } = useAnswerTip();
</script>
