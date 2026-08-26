package com.wordtyping.config;

import java.util.List;

/** Java 技术栈内置课程目录；顺序即练习时的优先顺序。 */
final class JavaLearningCatalog {

    private JavaLearningCatalog() {}

    static List<LearningSectionSeed> sections() {
        return List.of(
                new LearningSectionSeed(
                        "Java 常用注解",
                        "Spring Boot 项目中最常用的注解，按日常开发频率优先练习。",
                        annotationItems()
                ),
                new LearningSectionSeed(
                        "MyBatis",
                        "MyBatis 与 MyBatis-Plus 中最常见的映射、SQL 和会话术语。",
                        mybatisItems()
                ),
                new LearningSectionSeed(
                        "Java 项目结构",
                        "练习 Java 项目的目录和文件命名规范，并通过完整示例理解各层职责。",
                        projectStructureItems()
                )
        );
    }

    private static List<LearningItemSeed> annotationItems() {
        return List.of(
                annotation(
                        "@RestController",
                        "REST 控制器；接收请求并返回 JSON",
                        "把类注册为 Spring MVC 控制器，并让方法返回值直接写入响应体。它等价于 @Controller 与 @ResponseBody 的组合。",
                        "适合 REST API；Controller 只处理参数、校验、调用 Service 和组织响应，不承载复杂业务。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@RestController\n@RequestMapping(\"/users\")\npublic class UserController {\n    private final UserService userService;\n\n    public UserController(UserService userService) {\n        this.userService = userService;\n    }\n\n    @GetMapping(\"/{id}\")\n    public Result<UserResponse> detail(@PathVariable Long id) {\n        return Result.success(userService.findById(id));\n    }\n}",
                        "@Controller\n@ResponseBody\npublic @interface RestController { }"
                ),
                annotation(
                        "@Controller",
                        "MVC 控制器；接收请求并选择视图",
                        "把类注册为 Spring MVC 控制器。方法返回 String 时通常表示视图名，不会默认转换为 JSON。",
                        "服务端渲染页面时使用；如果接口需要返回 JSON，应在方法上加 @ResponseBody 或改用 @RestController。",
                        "@Controller\npublic class PageController {\n    @GetMapping(\"/home\")\n    public String home() { return \"home\"; }\n}",
                        "@Component\npublic @interface Controller { }"
                ),
                annotation(
                        "@Service",
                        "业务服务；承载业务流程",
                        "把类标记为业务层组件并交给 Spring 容器管理。它是 @Component 的语义化变体。",
                        "接口与实现分离时通常标在实现类上；Controller 调用 Service，Service 再编排 Mapper、事件或外部服务。",
                        "src/main/java/com/example/learn/service/impl/UserServiceImpl.java\n\n@Service\npublic class UserServiceImpl implements UserService {\n    private final UserMapper userMapper;\n\n    public UserServiceImpl(UserMapper userMapper) {\n        this.userMapper = userMapper;\n    }\n\n    public UserResponse findById(Long id) {\n        return UserResponse.from(userMapper.findById(id));\n    }\n}",
                        "@Component\npublic @interface Service { }"
                ),
                annotation(
                        "@GetMapping",
                        "GET 路由；读取资源",
                        "把 HTTP GET 请求映射到处理方法，是 @RequestMapping(method = GET) 的快捷写法。",
                        "用于查询详情、列表和不修改服务端状态的读取请求。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@GetMapping(\"/{id}\")\npublic Result<UserResponse> detail(@PathVariable Long id) {\n    return Result.success(userService.findById(id));\n}",
                        null
                ),
                annotation(
                        "@PostMapping",
                        "POST 路由；创建资源或提交操作",
                        "把 HTTP POST 请求映射到处理方法，是 @RequestMapping(method = POST) 的快捷写法。",
                        "常与 @RequestBody、@Valid 配合接收创建请求。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@PostMapping\npublic Result<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {\n    return Result.success(userService.create(request));\n}",
                        null
                ),
                annotation(
                        "@RequestMapping",
                        "通用路由映射；定义基础路径",
                        "可同时约束 URL、HTTP 方法、请求参数、请求头以及内容类型。类上常用于统一接口前缀。",
                        "优先用类级 @RequestMapping 声明模块前缀，方法级使用 @GetMapping、@PostMapping 等快捷注解。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@RestController\n@RequestMapping(\"/users\")\npublic class UserController {\n    @GetMapping\n    public List<UserResponse> list() {\n        return userService.list();\n    }\n}",
                        null
                ),
                annotation(
                        "@RequestBody",
                        "请求体绑定；把 JSON 转成 Java 对象",
                        "让 Spring 通过 HttpMessageConverter 读取请求体，并反序列化为方法参数对象。",
                        "通常接收 DTO 而不是 Entity；与 @Valid 配合执行参数校验。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@PostMapping\npublic Result<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {\n    return Result.success(userService.create(request));\n}",
                        null
                ),
                annotation(
                        "@PathVariable",
                        "路径变量；读取 URL 中的动态片段",
                        "把 /users/{id} 中的 id 绑定到方法参数。参数名不一致时要显式指定 value。",
                        "适合资源标识；筛选和分页条件通常使用查询参数而不是路径变量。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@GetMapping(\"/{id}\")\npublic Result<UserResponse> detail(@PathVariable Long id) {\n    return Result.success(userService.findById(id));\n}",
                        null
                ),
                annotation(
                        "@Configuration",
                        "配置类；集中声明 Bean",
                        "把类标记为 Spring Java 配置，类中的 @Bean 方法会参与容器装配。",
                        "用于数据源、安全、Redis、WebMvc 等基础设施配置，不应混入业务流程。",
                        "src/main/java/com/example/learn/config/MainMybatisConfiguration.java\n\n@Configuration\npublic class MainMybatisConfiguration {\n    @Bean\n    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {\n        return new SqlSessionTemplate(factory);\n    }\n}",
                        "@Component\npublic @interface Configuration { }"
                ),
                annotation(
                        "@Bean",
                        "Bean 工厂方法；把返回对象交给容器",
                        "标在配置方法上，方法返回值会以 Bean 的形式注册到 Spring 容器。",
                        "第三方类型无法加 @Component 时使用；方法参数会由容器自动注入。",
                        "src/main/java/com/example/learn/config/MainMybatisConfiguration.java\n\n@Bean\npublic SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {\n    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();\n    factory.setDataSource(dataSource);\n    return factory.getObject();\n}",
                        null
                ),
                annotation(
                        "@Component",
                        "通用组件；交给 Spring 扫描管理",
                        "最通用的组件注解。被扫描到的类会注册为 Bean，供依赖注入使用。",
                        "没有更明确的 @Service、@Repository、@Controller 语义时再使用 @Component。",
                        "src/main/java/com/example/learn/agent/service/AgentThreadIdFactory.java\n\n@Component\npublic class AgentThreadIdFactory {\n    public String create() {\n        return UUID.randomUUID().toString();\n    }\n}",
                        null
                ),
                annotation(
                        "@Repository",
                        "数据仓储；封装持久化访问",
                        "标记数据访问组件，并表达持久化层语义；部分数据库异常会转换为 Spring 统一异常。",
                        "Repository 只组织数据访问，不放业务规则；MyBatis 项目也可以用 Mapper 作为数据访问边界。",
                        "src/main/java/com/example/learn/agent/knowledge/repository/FeishuDocSyncRecordRepository.java\n\n@Repository\npublic class FeishuDocSyncRecordRepository {\n    private final FeishuDocSyncRecordMapper mapper;\n\n    public Optional<FeishuDocSyncRecord> findByToken(String token) {\n        return Optional.ofNullable(mapper.selectByToken(token));\n    }\n}",
                        null
                ),
                annotation(
                        "@Autowired",
                        "依赖注入；自动装配 Bean",
                        "让 Spring 按类型注入依赖。只有一个构造器时可以省略该注解。",
                        "优先构造器注入，使依赖明确且便于测试；多个同类型 Bean 时结合 @Qualifier。",
                        "public class UserServiceImpl implements UserService {\n    private final UserMapper userMapper;\n\n    @Autowired\n    public UserServiceImpl(UserMapper userMapper) {\n        this.userMapper = userMapper;\n    }\n}",
                        "public @interface Autowired {\n    boolean required() default true;\n}"
                ),
                annotation(
                        "@Valid",
                        "级联校验；触发 DTO 参数约束",
                        "触发 Jakarta Validation，对请求对象及其嵌套字段执行 @NotBlank、@Size 等约束。",
                        "通常放在 Controller 的 @RequestBody 参数前；校验错误交给全局异常处理器统一返回。",
                        "src/main/java/com/example/learn/controller/UserController.java\n\n@PostMapping\npublic Result<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {\n    return Result.success(userService.create(request));\n}",
                        null
                ),
                annotation(
                        "@ExceptionHandler",
                        "异常处理；把异常转换为统一响应",
                        "声明某个方法处理指定异常类型，可放在 Controller 内，也可配合 @RestControllerAdvice 全局处理。",
                        "业务异常集中转换为错误码和 Result，避免 Controller 中重复 try/catch。",
                        "src/main/java/com/example/learn/common/exception/GlobalExceptionHandler.java\n\n@ExceptionHandler(BusinessException.class)\npublic Result<Void> handle(BusinessException exception) {\n    return Result.failure(exception.getMessage());\n}",
                        null
                ),
                annotation(
                        "@ConfigurationProperties",
                        "配置绑定；把 YAML 映射为类型安全对象",
                        "按照 prefix 把 application.yaml 中的一组配置绑定到 Java 属性类。",
                        "适合多个相关配置；比散落的 @Value 更容易校验、复用和测试。",
                        "src/main/java/com/example/learn/properties/JwtProperties.java\n\n@ConfigurationProperties(prefix = \"jwt\")\npublic class JwtProperties {\n    private String secret;\n    private Duration expiration;\n\n    public String getSecret() { return secret; }\n    public void setSecret(String secret) { this.secret = secret; }\n}",
                        null
                ),
                annotation(
                        "@SpringBootApplication",
                        "Spring Boot 入口；启动自动配置与组件扫描",
                        "组合了 @Configuration、@EnableAutoConfiguration 和 @ComponentScan，是应用启动类的核心注解。",
                        "放在根包入口类上，让组件扫描覆盖其子包。",
                        "src/main/java/com/example/learn/LearnApplication.java\n\n@SpringBootApplication\npublic class LearnApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(LearnApplication.class, args);\n    }\n}",
                        "@Configuration\n@EnableAutoConfiguration\n@ComponentScan"
                ),
                annotation(
                        "@Scheduled",
                        "定时任务；按时间规则执行方法",
                        "让 Spring 按 fixedDelay、fixedRate 或 cron 表达式周期调用方法。",
                        "任务方法应短小、可重入；耗时任务考虑异步执行、分布式锁和失败重试。",
                        "src/main/java/com/example/learn/task/OrderTimeoutTask.java\n\n@Component\npublic class OrderTimeoutTask {\n    private final OrderService orderService;\n\n    @Scheduled(cron = \"0 */5 * * * *\")\n    public void closeTimeoutOrders() {\n        orderService.closeTimeoutOrders();\n    }\n}",
                        null
                )
        );
    }

    private static List<LearningItemSeed> mybatisItems() {
        return List.of(
                item("mapper", "映射器；连接 Java 方法与 SQL", "Mapper 通常是接口，MyBatis 在运行时生成代理实现，并把方法调用映射到注解 SQL 或 XML SQL。", "src/main/java/com/example/learn/mapper/UserMapper.java\n\n@Mapper\npublic interface UserMapper {\n    @Select(\"SELECT * FROM user WHERE id = #{id}\")\n    User findById(Long id);\n}"),
                item("@Mapper", "Mapper 注解；注册数据访问接口", "让 Spring/MyBatis 扫描并为接口创建代理 Bean。使用 @MapperScan 后可以不在每个接口上重复标注。", "src/main/java/com/example/learn/mapper/UserMapper.java\n\n@Mapper\npublic interface UserMapper {\n    @Select(\"SELECT * FROM user WHERE id = #{id}\")\n    User findById(Long id);\n}"),
                item("@MapperScan", "Mapper 扫描；批量注册接口", "配置要扫描的 Mapper 包，并可指定使用哪个 SqlSessionFactory，适合多数据源项目。", "src/main/java/com/example/learn/config/MainMybatisConfiguration.java\n\n@Configuration\n@MapperScan(\n    basePackages = \"com.example.learn.mapper\",\n    sqlSessionFactoryRef = \"sqlSessionFactory\"\n)\npublic class MainMybatisConfiguration { }"),
                item("@Select", "查询 SQL 注解", "把 SELECT SQL 直接绑定到 Mapper 方法，适合短小、稳定且易读的查询。复杂动态 SQL 更适合 XML。", "src/main/java/com/example/learn/mapper/UserMapper.java\n\n@Select(\"SELECT * FROM user WHERE id = #{id}\")\nUser findById(Long id);"),
                item("@Insert", "新增 SQL 注解", "把 INSERT SQL 绑定到 Mapper 方法；需要回填自增主键时结合 @Options(useGeneratedKeys = true)。", "src/main/java/com/example/learn/mapper/UserMapper.java\n\n@Insert(\"INSERT INTO user(username, email) VALUES(#{username}, #{email})\")\nint insert(User user);"),
                item("@Update", "更新 SQL 注解", "把 UPDATE SQL 绑定到 Mapper 方法。可选字段较多时优先使用 XML <set> 生成动态更新。", "src/main/java/com/example/learn/mapper/UserMapper.java\n\n@Update(\"UPDATE user SET email=#{email} WHERE id=#{id}\")\nint update(User user);"),
                item("@Delete", "删除 SQL 注解", "把 DELETE SQL 绑定到 Mapper 方法。真实业务中应先确认是物理删除还是逻辑删除。", "src/main/java/com/example/learn/mapper/UserMapper.java\n\n@Delete(\"DELETE FROM user WHERE id = #{id}\")\nint delete(Long id);"),
                item("@Param", "参数命名；供 SQL 引用多个参数", "为 Mapper 方法参数指定稳定名称，让 SQL 可以用 #{name} 引用。单个简单参数通常可以省略。", "src/main/java/com/example/learn/agent/conversation/mapper/AgentConversationMessageMapper.java\n\nint deleteByThread(@Param(\"threadId\") String threadId);"),
                item("resultMap", "结果映射；把列映射为对象字段", "XML 中显式描述数据库列与 Java 属性的对应关系，适合字段名不同、嵌套对象或复杂关联。", "src/main/resources/mapper/UserXmlMapper.xml\n\n<resultMap id=\"UserResultMap\" type=\"User\">\n  <id column=\"id\" property=\"id\"/>\n</resultMap>"),
                item("resultType", "结果类型；直接指定返回 Java 类型", "列名与属性名能自动对应时，可在 select 上直接写 resultType；复杂映射改用 resultMap。", "src/main/resources/mapper/UserXmlMapper.xml\n\n<select id=\"countUsers\" resultType=\"long\">SELECT COUNT(*) FROM user</select>"),
                item("parameterType", "参数类型；声明 SQL 入参类型", "XML 语句可用 parameterType 说明入参类型。MyBatis 通常能推断，因此它更多用于提高可读性。", "src/main/resources/mapper/UserXmlMapper.xml\n\n<select id=\"findById\" parameterType=\"long\" resultMap=\"UserResultMap\">\n  SELECT id, username, email FROM user WHERE id = #{id}\n</select>"),
                item("SqlSessionFactory", "会话工厂；创建 MyBatis SqlSession", "持有 MyBatis Configuration 和 Mapper 映射，是数据库会话基础设施；多数据源时每套数据源对应一套工厂。", "src/main/java/com/example/learn/config/MainMybatisConfiguration.java\n\n@Bean\npublic SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {\n    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();\n    factory.setDataSource(dataSource);\n    return factory.getObject();\n}"),
                item("SqlSessionTemplate", "线程安全会话模板；连接 Mapper 与事务", "Spring 提供的线程安全 SqlSession 包装，负责把 Mapper 调用绑定到当前事务。", "src/main/java/com/example/learn/config/MainMybatisConfiguration.java\n\n@Bean\npublic SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {\n    return new SqlSessionTemplate(factory);\n}"),
                item("BaseMapper", "MyBatis-Plus 基础 Mapper；提供通用 CRUD", "接口继承 BaseMapper<Entity> 后获得 selectById、insert、updateById 等通用方法，减少重复 SQL。", "src/main/java/com/example/learn/agent/knowledge/mapper/FeishuDocSyncRecordMapper.java\n\npublic interface FeishuDocSyncRecordMapper extends BaseMapper<FeishuDocSyncRecord> { }"),
                item("lambdaQuery", "Lambda 条件构造器；类型安全拼接条件", "MyBatis-Plus 通过方法引用选择字段，重构属性名时比字符串列名更安全。", "src/main/java/com/example/learn/agent/knowledge/repository/FeishuDocSyncRecordRepository.java\n\nWrappers.lambdaQuery(FeishuDocSyncRecord.class)\n    .eq(FeishuDocSyncRecord::getWikiToken, wikiToken)")
        );
    }

    private static List<LearningItemSeed> projectStructureItems() {
        return List.of(
                pathItem("controller", "控制器目录；定义 HTTP 接口", "只处理路由、参数校验、调用 Service 和返回统一结果，不写复杂业务逻辑。", "src/main/java/com/example/learn/controller/UserController.java"),
                pathItem("service", "业务服务目录；编排业务流程", "接口放 service，实现类放 service/impl；负责事务边界、业务规则和 Entity/DTO 转换。", "src/main/java/com/example/learn/service/UserService.java"),
                pathItem("mapper", "MyBatis 数据访问目录", "放 Mapper 接口，方法对应 SQL；XML 文件放 resources/mapper，不在 Mapper 中写业务判断。", "src/main/java/com/example/learn/mapper/UserMapper.java"),
                pathItem("entity", "实体目录；映射数据库数据", "一个实体通常对应一张表或持久化记录；不要直接把 Entity 当作外部 API 响应。", "src/main/java/com/example/learn/entity/User.java"),
                pathItem("dto", "数据传输对象目录", "请求对象放 dto/request，响应对象放 dto/response；明确接口边界并避免暴露内部字段。", "src/main/java/com/example/learn/dto/request/UserCreateRequest.java"),
                pathItem("config", "配置目录；声明基础设施 Bean", "放 Security、Redis、WebMvc、MyBatis 等项目级配置；配置类只负责装配，不承载业务逻辑。", "src/main/java/com/example/learn/config/MainMybatisConfiguration.java"),
                pathItem("repository", "仓储目录；封装复杂数据访问", "当 Mapper 之上需要组合查询、隐藏 MyBatis-Plus 细节时使用 Repository，仍然不放业务规则。", "src/main/java/com/example/learn/agent/knowledge/repository/FeishuDocSyncRecordRepository.java"),
                pathItem("exception", "异常目录；定义和转换业务错误", "业务异常集中定义并由全局处理器转换为统一错误响应，避免 Controller 重复 try/catch。", "src/main/java/com/example/learn/common/exception/BusinessException.java"),
                pathItem("common", "公共基础目录；放跨模块共享能力", "只放真正跨模块复用的结果封装、错误码、常量和异常；领域逻辑应留在自己的模块。", "src/main/java/com/example/learn/common/result/Result.java"),
                pathItem("resources", "资源目录；放配置、XML 和静态资源", "application.yaml、MyBatis XML、JSON 目录等非 Java 资源放在 src/main/resources。", "src/main/resources/application.yaml"),
                pathItem("test", "测试目录；镜像生产包结构", "测试包路径应与 src/main/java 对应，类名通常以 Test 或 Tests 结尾，测试资源放 src/test/resources。", "src/test/java/com/example/learn/LearnApplicationTests.java"),
                pathItem("properties", "配置属性目录；承载类型安全配置", "用 @ConfigurationProperties 将一组 YAML 配置绑定成对象，名称通常以 Properties 结尾。", "src/main/java/com/example/learn/properties/JwtProperties.java"),
                pathItem("annotation", "自定义注解目录", "只声明注解契约；实际处理逻辑通常位于 aspect 或 interceptor，命名表达业务意图。", "src/main/java/com/example/learn/annotation/LoginRequired.java"),
                pathItem("aspect", "切面目录；处理横切逻辑", "围绕注解或方法执行日志、限流等逻辑；切面不应隐藏核心业务流程。", "src/main/java/com/example/learn/aspect/RateLimitAspect.java"),
                pathItem("filter", "Servlet 过滤器目录；最早处理请求", "适合 TraceId、跨域或底层请求包装；执行层级早于 Spring MVC Interceptor。", "src/main/java/com/example/learn/filter/TraceIdFilter.java"),
                pathItem("interceptor", "MVC 拦截器目录", "在 Controller 前后执行登录校验等 MVC 逻辑，并通过 WebMvcConfigurer 注册路径规则。", "src/main/java/com/example/learn/interceptor/AuthInterceptor.java"),
                pathItem("event", "领域事件目录；表达已经发生的事实", "事件对象只携带必要数据，用过去式语义命名，发布者不直接依赖后续处理者。", "src/main/java/com/example/learn/event/UserRegisterEvent.java"),
                pathItem("listener", "事件监听器目录", "订阅事件并处理解耦后的动作；需要异步时明确事务提交时机和失败策略。", "src/main/java/com/example/learn/listener/UserRegisterListener.java"),
                pathItem("task", "定时任务目录", "放 @Scheduled 任务入口；复杂任务继续委托 Service，避免定时类本身膨胀。", "src/main/java/com/example/learn/task/OrderTimeoutTask.java"),
                pathItem("application.yaml", "Spring Boot 主配置文件", "存放可公开的默认配置并按环境覆盖；密码、Token 等敏感信息不能提交。", "src/main/resources/application.yaml"),
                pathItem("pom.xml", "Maven 项目描述与依赖文件", "声明项目信息、Java 版本、依赖和构建插件；新增依赖前先确认是否已有 starter 提供。", "pom.xml")
        );
    }

    private static LearningItemSeed annotation(
            String english,
            String chinese,
            String core,
            String keyPoint,
            String usageExample,
            String referenceCode
    ) {
        return learningItem(
                "@",
                withoutAt(english),
                chinese,
                "## 核心作用\n" + core + "\n\n## 使用要点\n" + keyPoint,
                usageExample,
                referenceCode
        );
    }

    private static LearningItemSeed item(
            String english,
            String chinese,
            String explanation,
            String usageExample
    ) {
        var prefix = english.startsWith("@") ? "@" : null;
        return learningItem(
                prefix,
                withoutAt(english),
                chinese,
                "## 核心作用\n" + explanation,
                usageExample,
                null
        );
    }

    private static LearningItemSeed pathItem(
            String english,
            String chinese,
            String rule,
            String relativePath
    ) {
        return new LearningItemSeed(
                english,
                null,
                chinese,
                "## 文件职责\n" + chinese + "。\n\n## 创建规范\n" + rule,
                projectUsageExample(english, relativePath),
                null
        );
    }

    private static LearningItemSeed learningItem(
            String prefix,
            String english,
            String chinese,
            String explanation,
            String rawUsageExample,
            String referenceCode
    ) {
        var usageExample = stripProjectReference(rawUsageExample);
        return new LearningItemSeed(
                english,
                prefix,
                chinese,
                explanation,
                usageExample,
                referenceCode
        );
    }

    private static String stripProjectReference(String rawUsageExample) {
        if (rawUsageExample == null) {
            return null;
        }

        var separator = rawUsageExample.indexOf('\n');
        var firstLine = separator < 0 ? rawUsageExample : rawUsageExample.substring(0, separator);
        if (!firstLine.startsWith("src/") && !"pom.xml".equals(firstLine)) {
            return rawUsageExample;
        }

        return separator < 0 ? null : rawUsageExample.substring(separator).stripLeading();
    }

    private static String withoutAt(String english) {
        return english.startsWith("@") ? english.substring(1) : english;
    }

    private static String projectUsageExample(String term, String relativePath) {
        return switch (term) {
            case "controller" -> """
                    controller/
                    └── UserController.java

                    @RestController
                    @RequestMapping("/users")
                    public class UserController {
                        private final UserService userService;

                        public UserController(UserService userService) {
                            this.userService = userService;
                        }

                        @GetMapping("/{id}")
                        public UserResponse detail(@PathVariable Long id) {
                            return userService.findById(id);
                        }
                    }
                    """;
            case "service" -> """
                    service/
                    ├── UserService.java
                    └── impl/
                        └── UserServiceImpl.java

                    public interface UserService {
                        UserResponse findById(Long id);
                    }

                    @Service
                    public class UserServiceImpl implements UserService {
                        public UserResponse findById(Long id) {
                            return new UserResponse(id, "Ada");
                        }
                    }
                    """;
            case "mapper" -> """
                    mapper/
                    ├── UserMapper.java
                    └── UserXmlMapper.java
                    resources/mapper/
                    └── UserXmlMapper.xml

                    @Mapper
                    public interface UserMapper {
                        @Select("SELECT * FROM user WHERE id = #{id}")
                        User findById(Long id);
                    }
                    """;
            case "entity" -> """
                    entity/
                    └── User.java

                    public class User {
                        private Long id;
                        private String username;
                        private String email;
                    }
                    """;
            case "dto" -> """
                    dto/
                    ├── request/UserCreateRequest.java
                    └── response/UserResponse.java

                    public record UserCreateRequest(
                            @NotBlank String username,
                            @Email String email
                    ) { }
                    """;
            case "config" -> """
                    config/
                    ├── MainMybatisConfiguration.java
                    ├── SecurityConfig.java
                    └── WebMvcConfig.java

                    @Configuration
                    public class WebMvcConfig implements WebMvcConfigurer {
                        public void addInterceptors(InterceptorRegistry registry) {
                            registry.addInterceptor(new AuthInterceptor());
                        }
                    }
                    """;
            case "repository" -> """
                    repository/
                    └── FeishuDocSyncRecordRepository.java

                    @Repository
                    public class FeishuDocSyncRecordRepository {
                        private final FeishuDocSyncRecordMapper mapper;

                        public Optional<FeishuDocSyncRecord> findByToken(String token) {
                            return Optional.ofNullable(mapper.selectByToken(token));
                        }
                    }
                    """;
            case "exception" -> """
                    common/exception/
                    ├── BusinessException.java
                    └── GlobalExceptionHandler.java

                    public class BusinessException extends RuntimeException {
                        public BusinessException(String message) { super(message); }
                    }
                    """;
            case "common" -> """
                    common/
                    ├── constant/CommonConstant.java
                    └── result/
                        ├── ErrorCode.java
                        └── Result.java

                    public record Result<T>(int code, String message, T data) { }
                    """;
            case "resources" -> """
                    src/main/resources/
                    ├── application.yaml
                    ├── mapper/UserXmlMapper.xml
                    └── agent/frontend-navigation-catalog.json
                    """;
            case "test" -> """
                    src/test/
                    ├── java/com/example/learn/LearnApplicationTests.java
                    └── resources/mockito-extensions/org.mockito.plugins.MockMaker

                    @SpringBootTest
                    class LearnApplicationTests {
                        @Test void contextLoads() { }
                    }
                    """;
            case "properties" -> """
                    properties/
                    └── JwtProperties.java

                    @ConfigurationProperties(prefix = "jwt")
                    public class JwtProperties {
                        private String secret;
                        private Duration expiration;
                    }
                    """;
            case "annotation" -> """
                    annotation/
                    ├── LoginRequired.java
                    ├── OperationLog.java
                    └── RateLimit.java

                    @Target(ElementType.METHOD)
                    @Retention(RetentionPolicy.RUNTIME)
                    public @interface LoginRequired { }
                    """;
            case "aspect" -> """
                    aspect/
                    ├── LogAspect.java
                    └── RateLimitAspect.java

                    @Aspect
                    @Component
                    public class LogAspect {
                        @Around("@annotation(OperationLog)")
                        public Object log(ProceedingJoinPoint point) throws Throwable {
                            return point.proceed();
                        }
                    }
                    """;
            case "filter" -> """
                    filter/
                    └── TraceIdFilter.java

                    @Component
                    public class TraceIdFilter extends OncePerRequestFilter {
                        protected void doFilterInternal(
                                HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain chain
                        ) throws IOException, ServletException {
                            chain.doFilter(request, response);
                        }
                    }
                    """;
            case "interceptor" -> """
                    interceptor/
                    └── AuthInterceptor.java

                    public class AuthInterceptor implements HandlerInterceptor {
                        public boolean preHandle(
                                HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler
                        ) {
                            return request.getHeader("Authorization") != null;
                        }
                    }
                    """;
            case "event" -> """
                    event/
                    └── UserRegisterEvent.java

                    public record UserRegisterEvent(Long userId, String email) { }
                    """;
            case "listener" -> """
                    listener/
                    └── UserRegisterListener.java

                    @Component
                    public class UserRegisterListener {
                        @EventListener
                        public void onRegistered(UserRegisterEvent event) {
                            sendWelcomeMail(event.email());
                        }
                    }
                    """;
            case "task" -> """
                    task/
                    └── OrderTimeoutTask.java

                    @Component
                    public class OrderTimeoutTask {
                        @Scheduled(cron = "0 */5 * * * *")
                        public void closeTimeoutOrders() {
                            orderService.closeTimeoutOrders();
                        }
                    }
                    """;
            case "application.yaml" -> """
                    spring:
                      application:
                        name: learn
                      datasource:
                        url: jdbc:mysql://localhost:3306/learn
                        username: root

                    server:
                      port: 8080
                    """;
            case "pom.xml" -> """
                    <project>
                      <modelVersion>4.0.0</modelVersion>
                      <groupId>com.example</groupId>
                      <artifactId>learn</artifactId>
                      <properties>
                        <java.version>17</java.version>
                      </properties>
                      <dependencies>
                        <dependency>
                          <groupId>org.springframework.boot</groupId>
                          <artifactId>spring-boot-starter-web</artifactId>
                        </dependency>
                      </dependencies>
                    </project>
                    """;
            default -> relativePath + "\n└── " + term + " example";
        };
    }

    record LearningSectionSeed(
            String title,
            String description,
            List<LearningItemSeed> items
    ) {}

    record LearningItemSeed(
            String english,
            String prefix,
            String chinese,
            String explanation,
            String usageExample,
            String referenceCode
    ) {}
}
