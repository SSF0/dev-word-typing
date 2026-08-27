<template>
  <div
    class="relative flex items-center pb-3 pt-4 text-base"
  >
    <RouterLink
      class="clickable-item flex items-center justify-center"
      :to="`/course-pack/${courseStore.currentCourse?.coursePackId}`"
    >
      <AppTooltip text="课程列表">
        <IconsExpand class="h-7 w-7" />
      </AppTooltip>
    </RouterLink>
    <div
      class="clickable-item ml-4"
      @click="openCourseContents"
    >
      <AppTooltip text="课程题目列表">
        {{ currentCourseInfo }}
      </AppTooltip>
    </div>
    <MainStudyVideoLink :video="courseStore.currentCourse?.video" />

    <MainCourseContents v-model:isOpen="isOpenCourseContents"></MainCourseContents>
  </div>

  <CommonProgressBar
    class="h-6 p-[2px]"
    :percentage="currentPercentage"
  />
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

import CommonProgressBar from "~/components/common/ProgressBar.vue";
import IconsExpand from "~/components/icons/Expand.vue";
import MainCourseContents from "~/components/main/CourseContents.vue";
import MainStudyVideoLink from "~/components/main/StudyVideoLink.vue";
import AppTooltip from "~/components/ui/AppTooltip.vue";
import { useCourseContents } from "~/composables/main/useCourseContents";
import { useCourseStore } from "~/store/course";

const courseStore = useCourseStore();
const { openCourseContents } = useCourseContents();

const currentCourseInfo = computed(() => {
  return `${courseStore.currentCourse?.title}（${currentSchedule.value}/${courseStore.visibleStatementsCount}）`;
});

const currentSchedule = computed(() => {
  return courseStore.visibleStatementIndex + 1;
});

const currentPercentage = computed(() => {
  if (courseStore.isAllDone()) {
    return 100;
  }
  return ((courseStore.visibleStatementIndex / courseStore.visibleStatementsCount) * 100).toFixed(
    2,
  );
});

const isOpenCourseContents = ref(false);
</script>

<style scoped>
.clickable-item {
  @apply cursor-pointer select-none hover:text-fuchsia-500;
}
</style>
