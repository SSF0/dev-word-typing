import { createPinia } from "pinia";
import { createApp } from "vue";

import App from "./app.vue";
import { setupHttp } from "./api/http";
import "./assets/css/globals.css";
import router from "./router";

const apiBase = import.meta.env.VITE_API_BASE || "http://localhost:8080";

setupHttp(apiBase);

createApp(App).use(createPinia()).use(router).mount("#app");
