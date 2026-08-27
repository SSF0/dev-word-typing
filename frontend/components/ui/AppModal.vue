<template>
  <Teleport to="body">
    <Transition name="app-modal">
      <div
        v-if="modelValue"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4"
        @mousedown.self="requestClose"
      >
        <div
          ref="panel"
          class="relative max-h-[calc(100dvh-2rem)] overflow-auto rounded-lg bg-white shadow-2xl outline-none dark:bg-gray-800"
          :class="panelClass"
          role="dialog"
          aria-modal="true"
          tabindex="-1"
        >
          <slot />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";

const props = withDefaults(
  defineProps<{
    modelValue: boolean;
    preventClose?: boolean;
    panelClass?: string;
  }>(),
  {
    preventClose: false,
    panelClass: "w-auto",
  },
);

const emit = defineEmits<{
  "update:modelValue": [value: boolean];
  close: [];
}>();

const panel = ref<HTMLElement | null>(null);
let previousBodyOverflow = "";
let previouslyFocused: HTMLElement | null = null;

function requestClose() {
  if (props.preventClose) return;
  emit("update:modelValue", false);
  emit("close");
}

function handleKeydown(event: KeyboardEvent) {
  if (!props.modelValue) return;

  if (event.key === "Escape") {
    event.preventDefault();
    requestClose();
    return;
  }

  if (event.key !== "Tab" || !panel.value) return;

  const focusable = Array.from(
    panel.value.querySelectorAll<HTMLElement>(
      'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])',
    ),
  );

  if (focusable.length === 0) {
    event.preventDefault();
    panel.value.focus();
    return;
  }

  const first = focusable[0];
  const last = focusable[focusable.length - 1];
  if (event.shiftKey && document.activeElement === first) {
    event.preventDefault();
    last.focus();
  } else if (!event.shiftKey && document.activeElement === last) {
    event.preventDefault();
    first.focus();
  }
}

function restorePageState() {
  document.body.style.overflow = previousBodyOverflow;
  previouslyFocused?.focus();
  previouslyFocused = null;
}

watch(
  () => props.modelValue,
  async (isOpen) => {
    if (isOpen) {
      previouslyFocused = document.activeElement as HTMLElement | null;
      previousBodyOverflow = document.body.style.overflow;
      document.body.style.overflow = "hidden";
      await nextTick();
      const firstFocusable = panel.value?.querySelector<HTMLElement>(
        'button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), a[href]',
      );
      (firstFocusable || panel.value)?.focus();
    } else {
      restorePageState();
    }
  },
  { immediate: true },
);

onMounted(() => window.addEventListener("keydown", handleKeydown));
onBeforeUnmount(() => {
  window.removeEventListener("keydown", handleKeydown);
  if (props.modelValue) restorePageState();
});
</script>

<style scoped>
.app-modal-enter-active,
.app-modal-leave-active {
  transition: opacity 0.18s ease;
}

.app-modal-enter-from,
.app-modal-leave-to {
  opacity: 0;
}
</style>
