<template>
  <div class="flex w-full flex-col pt-2">
    <template v-if="isLoading">
      <Loading></Loading>
    </template>
    <template v-else>
      <PracticeWorkspace />
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { toast } from "vue-sonner";

import PracticeWorkspace from "~/components/main/PracticeWorkspace.vue";
import { useGameMode } from "~/composables/main/game";
import { useNavigation } from "~/composables/useNavigation";
import { GamePlayMode, useGamePlayMode } from "~/composables/user/gamePlayMode";
import { isAuthenticated } from "~/services/auth";
import { useCourseStore } from "~/store/course";
import { useCoursePackStore } from "~/store/coursePack";
import { useMasteredElementsStore } from "~/store/masteredElements";

const isLoading = ref(true);
const route = useRoute();
const coursePackStore = useCoursePackStore();
const courseStore = useCourseStore();
const masteredElementsStore = useMasteredElementsStore();
const { gotoCourseList } = useNavigation();
const { showQuestion } = useGameMode();
const { toggleGamePlayMode } = useGamePlayMode();

showQuestion();

onMounted(async () => {
  const { coursePackId, id } = route.params;
  if (isAuthenticated()) {
    await masteredElementsStore.setup();
  }
  await courseStore.setup(coursePackId as string, id as string);
  await coursePackStore.setupCoursePack(coursePackId as string);

  // 单词模式固定用「中译英」：中文释义 -> 打英文单词，不用听写
  if (courseStore.currentCourse?.practiceType === "WORD") {
    toggleGamePlayMode(GamePlayMode.ChineseToEnglish);
  }

  if (courseStore.isAllMastered()) {
    toast.info("你已经全部都掌握 自动帮你跳转到课程列表啦", {
      duration: 1500,
      onAutoClose: () => {
        gotoCourseList(coursePackId as string);
      },
    });
    return;
  }
  isLoading.value = false;
});
</script>
