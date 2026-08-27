<template>
  <AppModal v-model="showGameSettingModal">
    <div class="flex h-[50vh] max-h-[580px] w-[70vw] max-w-[680px] flex-col p-5">
      <CommonModalHeader
        title="游戏设置"
        @close="closeGameSettingModal"
      />
      <div class="mt-6 px-4">
        <div class="flex flex-col space-y-4">
          <template
            v-for="option in TOOLBAR_LIST"
            :key="option.key"
          >
            <div class="flex items-center justify-between">
              <span class="mr-2 text-sm font-medium dark:text-gray-200">{{ option.label }}：</span>
              <AppSelect
                v-model="toolBarData[option.key]"
                :options="option.options"
                size="sm"
                class="w-32"
              />
            </div>
          </template>
        </div>
        <div class="mt-6 flex justify-end">
          <button
            @click="handleReset"
            class="btn btn-primary"
          >
            重置
          </button>
        </div>
      </div>
    </div>
  </AppModal>
</template>

<script setup lang="ts">
import { onMounted, watch } from "vue";

import CommonModalHeader from "~/components/common/ModalHeader.vue";
import AppModal from "~/components/ui/AppModal.vue";
import AppSelect from "~/components/ui/AppSelect.vue";
import { useToolbar } from "~/composables/main/dictation";
import { useGameSetting } from "~/composables/main/useGameSetting";

const { showGameSettingModal, closeGameSettingModal } = useGameSetting();

const { toolBarData, recoverToolBarData, saveToolBarData, resetToolBarData } = useToolbar();

onMounted(() => {
  recoverToolBarData();
});

watch(toolBarData, () => {
  saveToolBarData();
});

function handleReset() {
  resetToolBarData();
}

const TOOLBAR_LIST = [
  {
    label: "倍速",
    key: "rate",
    options: [
      {
        label: "2.0X",
        value: 2,
      },
      {
        label: "1.5X",
        value: 1.5,
      },
      {
        label: "1.0X",
        value: 1,
      },
      {
        label: "0.5X",
        value: 0.5,
      },
    ],
  },
  {
    label: "播放次数",
    key: "times",
    options: [
      {
        label: "4",
        value: 4,
      },
      {
        label: "3",
        value: 3,
      },
      {
        label: "2",
        value: 2,
      },
      {
        label: "1",
        value: 1,
      },
    ],
  },
  {
    label: "播放间隔",
    key: "interval",
    options: [
      {
        label: "10s",
        value: 10000,
      },
      {
        label: "5s",
        value: 5000,
      },
      {
        label: "3s",
        value: 3000,
      },
      {
        label: "1s",
        value: 1000,
      },
    ],
  },
];
</script>

<style scoped></style>
