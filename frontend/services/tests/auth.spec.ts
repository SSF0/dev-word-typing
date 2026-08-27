import { beforeEach, describe, expect, it } from "vitest";

import { getSignInCallback, setupAuth, signIn } from "../auth";

describe("auth", () => {
  beforeEach(() => {
    sessionStorage.clear();
    setupAuth();
  });

  it("should get signIn callback and consume callback", () => {
    signIn("/main/1");

    const callback = getSignInCallback();

    expect(callback).toBe("/main/1");

    const callback2 = getSignInCallback();
    expect(callback2).toBe("/");
  });

  it("should get default callback", () => {
    signIn();

    const callback = getSignInCallback();

    expect(callback).toBe("/");
  });
});
