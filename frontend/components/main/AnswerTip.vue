<template>
  <div
    class="absolute left-1/2 top-36 flex w-3/4 translate-x-[-50%] items-center justify-center text-xl dark:text-gray-50"
  >
    <div class="card bg-base-100 shadow-xl">
      <div class="card-body relative">
        <div class="absolute right-2 top-1 mt-0">
          <button
            type="button"
            class="btn btn-circle btn-ghost btn-sm dark:hover:bg-gray-600"
            @click="hiddenAnswerTip"
            tabindex="-1"
            aria-label="关闭答案提示"
          >
            <AppIcon
              name="close"
              class="h-5 w-5"
            />
          </button>
        </div>

        <div class="text-3xl">
          {{ courseStore.currentStatement?.prefix }}{{ courseStore.currentStatement?.english }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppIcon from "~/components/ui/AppIcon.vue";
import { useAnswerTip } from "~/composables/main/answerTip";
import { useCourseStore } from "~/store/course";

const courseStore = useCourseStore();
const { hiddenAnswerTip } = useAnswerTip();
</script>
