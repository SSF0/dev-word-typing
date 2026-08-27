import { ref } from "vue";

const detailOpen = ref(false);

export function usePracticeDetail() {
  function toggleDetail() {
    detailOpen.value = !detailOpen.value;
  }

  function closeDetail() {
    detailOpen.value = false;
  }

  return {
    detailOpen,
    toggleDetail,
    closeDetail,
  };
}
