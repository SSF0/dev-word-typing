<template>
  <div class="mb-4">
    <h1 class="text-center text-2xl font-bold text-purple-800 dark:text-white">{{ title }}</h1>
    <div class="absolute right-2 top-2">
      <button
        type="button"
        class="btn btn-circle btn-ghost btn-sm dark:hover:bg-gray-600"
        @click="$emit('close')"
        tabindex="-1"
        aria-label="关闭"
      >
        <AppIcon
          name="close"
          class="h-5 w-5"
        />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppIcon from "~/components/ui/AppIcon.vue";

defineProps({
  title: {
    type: String,
    required: true,
  },
});
</script>
