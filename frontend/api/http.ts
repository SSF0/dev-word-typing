import type { $Fetch } from "ofetch";

import { useRuntimeConfig } from "#app";
import { ofetch } from "ofetch";

let http: $Fetch;
export function setupHttp() {
  if (http) return http;

  const config = useRuntimeConfig();
  const baseURL = (config.public.apiBase as string) || "";

  http = ofetch.create({
    baseURL,
    headers: { "Content-Type": "application/json" },
    async onResponseError({ response }) {
      // Spring Boot 错误体形如 { timestamp, status, error, path }；
      // 这里仅透传，具体提示由页面 toast 处理。
      return Promise.reject(response._data);
    },
    retry: 2,
    retryDelay: 600,
  });
}

export function injectHttpStatusErrorHandler() {}

export function getHttp() {
  if (!http) {
    throw new Error("HTTP client not initialized. Call setupHttp first.");
  }
  return http;
}