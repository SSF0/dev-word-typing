# Java 三小节课程内容重构 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 Java 技术栈重组为“Java 常用注解、MyBatis、Java 项目结构”三个小节，并让每个练习项拥有独立说明、使用示例、参考代码和个人笔记。

**Architecture:** 保留 `TechStack -> Node -> Statement` 三级结构，将 `Node` 重新定义为小节，将 `Statement` 定义为可练习的知识项。详情字段和笔记下沉至 `Statement`，新增按 statement 保存笔记的 API；前端详情面板跟随 `currentStatement`，种子数据负责幂等迁移现有 Java 技术栈。

**Tech Stack:** Java 17、Spring Boot 3、Spring Data JPA、MySQL、Nuxt 3、Vue 3、Pinia、Vitest。

---

### Task 1: 让练习项承载独立详情和笔记

**Files:**
- Modify: `backend/src/main/java/com/wordtyping/entity/Statement.java`
- Modify: `backend/src/main/java/com/wordtyping/dto/StatementDto.java`
- Modify: `backend/src/main/java/com/wordtyping/service/Assemblers.java`
- Modify: `backend/src/main/java/com/wordtyping/controller/CoursePackController.java`
- Modify: `backend/src/main/java/com/wordtyping/service/TechStackService.java`
- Create: `backend/src/main/java/com/wordtyping/dto/UpdateStatementNoteRequest.java`
- Create: `backend/src/test/java/com/wordtyping/service/TechStackServiceTest.java`

- [ ] **Step 1: Write the failing service and assembler tests**

```java
assertThat(Assemblers.toStatement(statement).explanation()).isEqualTo("说明");
assertThat(Assemblers.toStatement(statement).usageExample()).isEqualTo("示例");
assertThat(Assemblers.toStatement(statement).referenceCode()).isEqualTo("源码");
assertThat(Assemblers.toStatement(statement).note()).isEqualTo("笔记");

var updated = service.updateStatementNote(1L, 2L, 3L, "新的理解");
assertThat(updated.note()).isEqualTo("新的理解");
```

- [ ] **Step 2: Run tests and verify RED**

Run: `JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw -DforkCount=0 -Dtest=TechStackServiceTest test`

Expected: FAIL because statement detail fields and `updateStatementNote` do not exist.

- [ ] **Step 3: Add statement fields and note endpoint**

Add nullable TEXT columns `explanation`, `usageExample`, `referenceCode`, and `note` to `Statement`; expose them from `StatementDto`. Add:

```java
@PutMapping("/{stackId}/courses/{nodeId}/statements/{statementId}")
public StatementDto updateStatementNote(..., UpdateStatementNoteRequest req)
```

The service must verify both `statement.nodeId == nodeId` and `node.stackId == stackId` before saving.

- [ ] **Step 4: Run tests and verify GREEN**

Run: `JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw -DforkCount=0 -Dtest=TechStackServiceTest test`

Expected: PASS.

### Task 2: 把 Java 技术栈重组为三个高频小节

**Files:**
- Modify: `backend/src/main/java/com/wordtyping/config/DataSeeder.java`
- Modify: `backend/src/test/java/com/wordtyping/config/DataSeederTest.java`

- [ ] **Step 1: Write failing seed tests for the exact hierarchy**

```java
assertThat(javaStack.getNodes()).extracting(Node::getTitle)
    .containsExactly("Java 常用注解", "MyBatis", "Java 项目结构");
assertThat(javaStack.getNodes().get(0).getStatements()).extracting(Statement::getEnglish)
    .startsWith("@RestController", "@Controller", "@Service", "@GetMapping", "@PostMapping");
assertThat(javaStack.getNodes().get(1).getStatements()).extracting(Statement::getEnglish)
    .startsWith("mapper", "@Mapper", "@MapperScan", "@Select", "@Insert");
assertThat(javaStack.getNodes().get(2).getStatements()).extracting(Statement::getEnglish)
    .startsWith("controller", "service", "mapper", "entity", "dto", "config");
```

Also assert every item has a nonblank explanation and usage example, and project-structure usage examples begin with `/Users/mac/project/java/learn/`.

- [ ] **Step 2: Run the seed test and verify RED**

Run: `JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw -DforkCount=0 -Dtest=DataSeederTest test`

Expected: FAIL because the existing seed still creates three annotation nodes.

- [ ] **Step 3: Implement exact ordered content**

Create the sections in this order:

1. `Java 常用注解`: `@RestController`, `@Controller`, `@Service`, `@GetMapping`, `@PostMapping`, `@RequestMapping`, `@RequestBody`, `@PathVariable`, `@Configuration`, `@Bean`, `@Component`, `@Repository`, `@Autowired`, `@Valid`, `@ExceptionHandler`, `@ConfigurationProperties`, `@SpringBootApplication`, `@Scheduled`.
2. `MyBatis`: `mapper`, `@Mapper`, `@MapperScan`, `@Select`, `@Insert`, `@Update`, `@Delete`, `@Param`, `resultMap`, `resultType`, `parameterType`, `SqlSessionFactory`, `SqlSessionTemplate`, `BaseMapper`, `lambdaQuery`.
3. `Java 项目结构`: `controller`, `service`, `mapper`, `entity`, `dto`, `config`, `repository`, `exception`, `common`, `resources`, `test`, `properties`, `annotation`, `aspect`, `filter`, `interceptor`, `event`, `listener`, `task`, `application.yaml`, `pom.xml`.

The project-structure examples must reference real locations under `/Users/mac/project/java/learn`, such as `controller/UserController.java`, `service/UserService.java`, `mapper/UserMapper.java`, `entity/User.java`, and `dto/request/UserCreateRequest.java`.

The migration must be idempotent: reuse matching sections/statements, preserve mastered state and statement notes, migrate old node notes for `@Controller`, `@RestController`, and `@Autowired`, and remove obsolete built-in nodes.

- [ ] **Step 4: Run seed tests and verify GREEN**

Run: `JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw -DforkCount=0 -Dtest=DataSeederTest test`

Expected: PASS.

### Task 3: 详情与笔记跟随当前练习项

**Files:**
- Modify: `frontend/api/course.ts`
- Modify: `frontend/components/main/AnnotationPanel.vue`
- Modify: `frontend/components/main/PracticeWordRail.vue`
- Modify: `frontend/components/main/tests/annotation-panel.spec.ts`
- Modify: `frontend/components/main/tests/practice-word-rail.spec.ts`

- [ ] **Step 1: Write failing component tests**

```ts
expect(wrapper.get('[data-test="detail-term"]').text()).toContain("@RestController");
expect(wrapper.get('[data-test="learning-guide"]').text()).toContain("直接返回 JSON");
expect(wrapper.get('[data-test="usage-example"]').text()).toContain("UserController.java");
expect(wrapper.get("textarea").element.value).toBe("当前注解笔记");
expect(wrapper.get('[data-test="practice-word-rail"]').text()).toContain("本节内容");
```

The save test must assert `updateStatementNote(packId, courseId, statementId, note)` is called.

- [ ] **Step 2: Run tests and verify RED**

Run: `pnpm vitest run components/main/tests/annotation-panel.spec.ts components/main/tests/practice-word-rail.spec.ts --config vitest.config.ts`

Expected: FAIL because the panel still reads node-level fields and the rail says “相关词”.

- [ ] **Step 3: Implement statement-driven detail UI**

Extend `StatementApiResponse` with optional `explanation`, `usageExample`, `referenceCode`, and `note`. Add:

```ts
updateStatementNote(coursePackId, courseId, statementId, note)
```

Render the current statement term and explanation in the panel. Keep the essential explanation visible; keep usage example and reference code in collapsed `<details>`. Watch `currentStatement.id` and refresh the note draft without remounting the panel. Change rail labels to `本节内容` and `答对解锁 · 点击可看`.

- [ ] **Step 4: Run component tests and verify GREEN**

Run: `pnpm vitest run components/main/tests/annotation-panel.spec.ts components/main/tests/practice-word-rail.spec.ts --config vitest.config.ts`

Expected: PASS.

### Task 4: Verification

**Files:**
- Modify: `docs/project-overview.md`
- Modify: `AGENTS.md`

- [ ] **Step 1: Update the authoritative hierarchy documentation**

Document `TechStack -> Node(section) -> Statement(knowledge item)` and note that explanation/example/reference/note now belong to each statement.

- [ ] **Step 2: Run backend tests**

Run: `JAVA_HOME=/Users/mac/解释器/java/Contents/Home ./mvnw -DforkCount=0 test`

Expected: PASS.

- [ ] **Step 3: Run frontend tests**

Run: `pnpm vitest run components/main/tests/practice-word-rail.spec.ts components/main/tests/annotation-panel.spec.ts components/main/QuestionInput/tests/unlock.spec.ts pages/game/tests/practice-layout.spec.ts store/tests/course.spec.ts --config vitest.config.ts`

Expected: PASS.

- [ ] **Step 4: Run production build**

Run: `API_BASE=http://localhost:8080 pnpm build`

Expected: Nuxt production build succeeds.

- [ ] **Step 5: Verify in the browser**

Open all three section cards and verify: exact ordering, center typing prompt, left rail title, detail switching, real learn-project paths, per-item note saving, no horizontal overflow.
