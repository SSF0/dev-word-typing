# Nuxt 开发环境性能修复实施计划

> 目标：修复 `frontend` 开发服务器加载页面/资源时的重复构建、长时间挂起和堆内存溢出，同时保留 Nuxt 3.12.1 / Vue 3.4.29 技术基线。

## 根因与修复边界

当前 `package.json` 虽固定了 Nuxt 和 Vue，但 Nuxt 相关模块仍使用 `^`。重新解析锁文件后，`@nuxt/ui` 从参考项目使用的 2.18.4 漂移到 2.22.3，并带入 Nuxt Kit 4、Tailwind 模块 6.14、Nuxi 3.37 和 Nitro 2.13。该组合与 Nuxt 3.12.1 的开发构建链混用后，会重复注册 `manifest-route-rule`、反复生成 Tailwind expose 模块，并最终触发约 4 GB 的 Node 堆内存溢出。

修复只收敛 Nuxt 构建链版本，不改业务页面。版本基线取自本机同源参考项目 `/Users/mac/project/earthworm` 的已安装锁文件。

## Task 1：建立依赖兼容性回归测试

**文件：**

- 新增 `frontend/tests/config/nuxt-dependency-baseline.test.ts`
- 修改 `frontend/package.json`

测试断言 Nuxt、UI、Image、VueUse、Vite 等直接依赖，以及 Nuxt Kit、Tailwind 模块、Nuxi、Nitro 等关键传递依赖必须保持在同一代基线。先在现有配置上运行测试并确认失败，证明测试能捕获本次版本漂移。

## Task 2：收敛依赖并更新锁文件

**文件：**

- 修改 `frontend/package.json`
- 修改 `frontend/pnpm-lock.yaml`
- 视模块行为修改 `frontend/nuxt.config.ts`

把 Nuxt 构建链的直接依赖改为精确版本，并通过 `pnpm.overrides` 固定关键传递依赖。执行 `pnpm install` 重新生成锁文件。若 `@nuxt/image` 1.7.0 在 `autoImport: false` 下已能正常 prepare，则移除此前为 1.11.0 添加的手工 `useImage` 兼容导入；否则保留。

## Task 3：静态验证

运行以下命令：

```bash
cd frontend
pnpm exec vitest run tests/config/nuxt-dependency-baseline.test.ts
pnpm test
pnpm type-check
pnpm build
```

要求回归测试和现有测试通过，类型检查无新增错误，生产构建成功。

## Task 4：真实开发服务验收

启动 Spring Boot 后端和 `pnpm dev --port 3002`。依次请求 `/course-pack`、`/_nuxt/@vite/client` 与 Nuxt 客户端入口，记录首轮和热请求耗时；观察日志中不再重复出现中间件注册、`useImage` 重复导入、Tailwind expose 无限重编或 OOM。最后通过浏览器确认页面完成挂载和接口返回。
