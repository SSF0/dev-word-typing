<template>
  <select
    class="select select-bordered"
    :value="modelValue"
    @change="handleChange"
  >
    <option
      v-for="option in options"
      :key="String(option.value)"
      :value="option.value"
    >
      {{ option.label }}
    </option>
  </select>
</template>

<script setup lang="ts">
interface SelectOption {
  label: string;
  value: string | number;
}

defineProps<{
  modelValue: string | number;
  options: SelectOption[];
}>();

const emit = defineEmits<{
  "update:modelValue": [value: string | number];
}>();

function handleChange(event: Event) {
  const select = event.target as HTMLSelectElement;
  const option = select.options[select.selectedIndex] as HTMLOptionElement & {
    _value?: string | number;
  };
  emit("update:modelValue", option._value ?? option.value);
}
</script>
