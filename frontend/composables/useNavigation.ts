import { useRouter } from "vue-router";

export function useNavigation() {
  const router = useRouter();

  function gotoCourseList(coursePackId: string) {
    return router.push(`/course-pack/${coursePackId}`);
  }

  function gotoGame(coursePackId: string, courseId: string) {
    return router.push(`/game/${coursePackId}/${courseId}`);
  }

  return {
    gotoCourseList,
    gotoGame,
  };
}
