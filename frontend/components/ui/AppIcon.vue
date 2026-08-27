<template>
  <svg
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.8"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <template v-if="name === 'sun'">
      <circle cx="12" cy="12" r="4" />
      <path d="M12 2v2M12 20v2M4.93 4.93l1.42 1.42M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.42-1.42M17.66 6.34l1.41-1.41" />
    </template>
    <path
      v-else-if="name === 'moon'"
      d="M20.5 14.4A8.5 8.5 0 0 1 9.6 3.5 8.5 8.5 0 1 0 20.5 14.4Z"
    />
    <template v-else-if="name === 'speaker'">
      <path d="M11 5 6.5 9H3v6h3.5L11 19z" />
      <path d="M15 9.5a4 4 0 0 1 0 5M17.5 7a7.5 7.5 0 0 1 0 10" />
    </template>
    <template v-else-if="name === 'alarm'">
      <circle cx="12" cy="13" r="7" />
      <path d="M12 10v4l2.5 1.5M5 3 2.5 5.5M19 3l2.5 2.5M7 20l-1 2M17 20l1 2" />
    </template>
    <path
      v-else-if="name === 'caret-left'"
      d="m15 18-6-6 6-6"
    />
    <path
      v-else-if="name === 'caret-right'"
      d="m9 18 6-6-6-6"
    />
    <path
      v-else-if="name === 'arrow-up'"
      d="m6 10 6-6 6 6M12 4v16"
    />
    <path
      v-else-if="name === 'check'"
      d="m5 12 4 4L19 6"
    />
    <template v-else-if="name === 'bilibili'">
      <rect x="3" y="7" width="18" height="13" rx="3" />
      <path d="m8 3 4 4 4-4M8 12v3M16 12v3" />
    </template>
    <path
      v-else-if="name === 'close'"
      d="M6 6l12 12M18 6 6 18"
    />
  </svg>
</template>

<script setup lang="ts">
defineProps<{
  name:
    | "sun"
    | "moon"
    | "speaker"
    | "alarm"
    | "caret-left"
    | "caret-right"
    | "arrow-up"
    | "check"
    | "bilibili"
    | "close";
}>();
</script>
