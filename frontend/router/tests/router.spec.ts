import { describe, expect, it } from "vitest";

import router from "../index";

describe("application routes", () => {
  it("preserves the public URL and route-name contract", () => {
    const routes = router.getRoutes();
    const routeNamesByPath = Object.fromEntries(
      routes.map((route) => [route.path, route.name]),
    );

    expect(routeNamesByPath).toMatchObject({
      "/": "home",
      "/course-pack": "course-pack",
      "/course-pack/:id": "course-pack-id",
      "/game/:coursePackId/:id": "game-coursePackId-id",
    });
  });

  it("redirects the root and unknown paths to the course list", () => {
    const routes = router.getRoutes();

    expect(routes.find((route) => route.path === "/")?.redirect).toBe("/course-pack");
    expect(routes.find((route) => route.path === "/:pathMatch(.*)*")?.redirect).toBe(
      "/course-pack",
    );
  });
});
