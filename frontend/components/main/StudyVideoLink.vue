<template>
  <a
    v-if="video"
    target="_blank"
    class="flex cursor-pointer items-center fill-current text-xl hover:text-fuchsia-500"
    :href="video"
  >
    <AppTooltip text="边看边练">
      <AppIcon
        name="bilibili"
        class="h-5 w-5"
      />
    </AppTooltip>
  </a>
</template>

<script setup lang="ts">
import AppIcon from "~/components/ui/AppIcon.vue";
import AppTooltip from "~/components/ui/AppTooltip.vue";

defineProps<{
  video?: string;
}>();
</script>
