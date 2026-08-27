import { beforeEach, describe, expect, it } from "vitest";

import { usePracticeDetail } from "../usePracticeDetail";

describe("usePracticeDetail", () => {
  beforeEach(() => {
    usePracticeDetail().closeDetail();
  });

  it("shares one detail state between the navbar and practice workspace", () => {
    const navbarDetail = usePracticeDetail();
    const workspaceDetail = usePracticeDetail();

    navbarDetail.toggleDetail();

    expect(workspaceDetail.detailOpen.value).toBe(true);
  });
});
