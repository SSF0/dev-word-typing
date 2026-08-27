import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      redirect: "/course-pack",
    },
    {
      path: "/course-pack",
      name: "course-pack",
      component: () => import("~/pages/course-pack/index.vue"),
    },
    {
      path: "/course-pack/:id",
      name: "course-pack-id",
      component: () => import("~/pages/course-pack/[id].vue"),
    },
    {
      path: "/game/:coursePackId/:id",
      name: "game-coursePackId-id",
      component: () => import("~/pages/game/[coursePackId]/[id].vue"),
    },
    {
      path: "/:pathMatch(.*)*",
      redirect: "/course-pack",
    },
  ],
  scrollBehavior: () => ({ top: 0 }),
});

export default router;
