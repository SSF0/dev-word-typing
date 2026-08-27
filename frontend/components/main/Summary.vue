<template>
  <AppModal
    v-model="showModal"
    prevent-close
  >
    <div class="w-[90vw] max-w-[780px] p-5">
      <div class="flex justify-between">
        <h3 class="mb-4 text-lg font-bold">🎉 恭喜!</h3>
      </div>

      <div class="flex flex-col gap-1.5">
        <p class="pl-2 text-xs leading-loose text-gray-600 sm:pl-4 sm:text-sm lg:pl-14 lg:text-base">
          {{
            `恭喜您一共完成 ${courseTimer.totalRecordNumber()} 道题，用时 ${formatSecondsToTime(
              courseTimer.calculateTotalTime(),
            )}`
          }}
        </p>
      </div>

      <div class="modal-action flex flex-col justify-center gap-2 sm:flex-row sm:justify-end">
        <button
          class="btn w-full sm:w-auto"
          @click="handleDoAgain"
        >
          再来一次
        </button>
        <button
          class="btn w-full sm:w-auto"
          @click="handleGoToCourseList"
        >
          课程列表
        </button>
        <button
          class="btn btn-primary w-full sm:w-auto"
          @click="goToNextCourse"
        >
          下一课
          <kbd class="kbd kbd-sm"> ↵ </kbd>
        </button>
      </div>
    </div>
  </AppModal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { toast } from "vue-sonner";

import AppModal from "~/components/ui/AppModal.vue";
import { courseTimer } from "~/composables/courses/courseTimer";
import { useGameMode } from "~/composables/main/game";
import { useSummary } from "~/composables/main/summary";
import { useNavigation } from "~/composables/useNavigation";
import { useCourseStore } from "~/store/course";
import { formatSecondsToTime } from "~/utils/date";
import { cancelShortcut, registerShortcut } from "~/utils/keyboardShortcuts";

const courseStore = useCourseStore();
const { gotoCourseList, gotoGame } = useNavigation();
const { showQuestion } = useGameMode();
const { showModal, hideSummary } = useSummary();

const nextNodeId = ref<string | undefined>();
const haveNextNode = computed(() => !!nextNodeId.value);

watch(showModal, (val) => {
  if (val) {
    registerShortcut("enter", goToNextCourse);
    loadNextNode();
  } else {
    cancelShortcut("enter", goToNextCourse);
  }
});

async function loadNextNode() {
  try {
    const { nextCourse } = await courseStore.completeCourse();
    nextNodeId.value = nextCourse?.id;
  } catch {
    nextNodeId.value = undefined;
  }
}

function handleDoAgain() {
  courseStore.doAgain();
  hideSummary();
  showQuestion();
  courseTimer.reset();
}

function handleGoToCourseList() {
  hideSummary();
  if (courseStore.currentCourse) {
    gotoCourseList(courseStore.currentCourse.coursePackId);
  }
}

function goToNextCourse() {
  hideSummary();
  if (!haveNextNode.value) {
    toast.info("已经是最后一个知识点，自动跳转到课程列表啦", { duration: 1500 });
    handleGoToCourseList();
    return;
  }
  if (courseStore.currentCourse) {
    gotoGame(courseStore.currentCourse.coursePackId, nextNodeId.value!);
  }
}
</script>
