// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  ssr: false,
  telemetry: false,
  imports: {
    autoImport: false,
  },
  devtools: {
    enabled: true,
  },
  app: {
    head: {
      title: "程序员单词打字通",
      link: [{ rel: "icon", href: "/favicon.ico" }],
    },
  },
  css: ["~/assets/css/globals.css"],
  modules: [
    "@nuxt/ui",
    "@vueuse/nuxt",
    "@hypernym/nuxt-anime",
    "@nuxt/image",
  ],
  plugins: ["~/plugins/http.ts", "~/plugins/pinia.ts"],
  runtimeConfig: {
    public: {
      apiBase: process.env.API_BASE || "http://localhost:8080",
    },
  },
  build: {
    transpile: ["vue-sonner"],
  },
});
