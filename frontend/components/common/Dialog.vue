<script lang="ts" setup>
import AppModal from "~/components/ui/AppModal.vue";

defineProps({
  title: {
    type: String,
    default: "",
  },
  content: {
    type: String,
    default: "",
  },
  showCancel: {
    type: Boolean,
    default: false,
  },
  cancelText: {
    type: String,
    default: "取消",
  },
  showConfirm: {
    type: Boolean,
    default: false,
  },
  confirmText: {
    type: String,
    default: "确认",
  },
});

const open = defineModel<boolean>({ default: false });
const emit = defineEmits(["cancel", "confirm"]);

function onCancel() {
  open.value = false;
  emit("cancel");
}
function onConfirm() {
  open.value = false;
  emit("confirm");
}
</script>

<template>
  <AppModal
    v-model="open"
    panel-class="w-full sm:max-w-lg"
  >
    <div class="flex h-52 flex-col justify-between p-6 text-gray-900 dark:text-white">
      <h2 class="mb-8 text-2xl font-bold">{{ title }}</h2>
      <p class="mb-8 text-base text-gray-700 dark:text-gray-300">
        {{ content }}
      </p>
      <div class="flex w-full justify-end space-x-4">
        <button
          v-if="showCancel"
          class="btn px-6"
          @click="onCancel"
        >
          {{ cancelText || "取消" }}
        </button>
        <button
          v-if="showConfirm"
          class="btn btn-primary px-6"
          @click="onConfirm"
        >
          {{ confirmText || "确认" }}
        </button>
      </div>
    </div>
  </AppModal>
</template>
