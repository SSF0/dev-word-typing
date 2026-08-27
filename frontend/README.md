# 前端

程序员单词打字通前端，技术栈为 Vue 3 + TypeScript + Vite + Pinia + Tailwind/daisyUI。

## 开发

```bash
pnpm install
VITE_API_BASE=http://localhost:8080 pnpm dev
```

开发地址：`http://localhost:3002/course-pack`。

## 验证

```bash
pnpm test
pnpm type-check
VITE_API_BASE=http://localhost:8080 pnpm build
```

## 生产构建预览

```bash
pnpm preview --host 0.0.0.0
```

预览端口为 `3003`，静态产物位于 `dist/`。正式部署时需要为 Vue Router history 模式配置 SPA fallback，把未知前端路由回退到 `index.html`；`vite preview` 只用于本地验收。
