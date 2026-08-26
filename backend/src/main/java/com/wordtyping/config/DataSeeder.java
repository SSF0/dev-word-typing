package com.wordtyping.config;

import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.entity.TechStack;
import com.wordtyping.repository.TechStackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 首次启动时，若数据库为空则写入种子数据。
 * 「Java 技术栈」每个节点 = 一个注解/知识点；practiceType=WORD 表示进去反复打该知识点相关的英文单词。
 * 每个单词同时可携带中文释义，练习页右侧展示注解源码，左侧打单词。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String CONTROLLER_GUIDE = """
            ## 核心作用
            @Controller 是 Spring MVC 的类级注解。它把一个类注册为 Web 层控制器，使类中的处理方法可以参与请求映射。它本身不指定 URL，也不会自动把返回值写成 JSON。

            ## 向下展开一层
            1. @RequestMapping、@GetMapping 等注解把请求路径和 HTTP 方法映射到 handler method。
            2. 处理方法通过参数接收路径变量、查询参数或请求体，再调用 Service 完成业务处理。
            3. 返回 String 时通常表示视图名；需要直接返回 JSON 时，配合 @ResponseBody 或改用 @RestController。

            ## 常见用法
            Controller 负责接收输入、调用业务层并组织返回结果。业务规则应放在 Service 中，避免把控制器写得过重。

            ## 源码拆解
            @Target(TYPE)：说明 @Controller 只能标在类型上。
            @Retention(RUNTIME)：注解在运行时仍然存在，Spring 才能通过反射识别。
            @Documented：生成 Javadoc 时保留这个注解信息。
            @Component：让控制器具备组件语义，可被组件扫描发现并注册为 Bean。
            @AliasFor：Controller.value 是 Component.value 的别名，可用于指定 Bean 名称。""";

    private static final String REST_CONTROLLER_GUIDE = """
            @RestController = @Controller + @ResponseBody，适合编写 REST endpoint。

            记忆关联：方法返回对象后，Spring 会 serialize 成 JSON，并写入 response body。

            项目位置：request → endpoint → controller → JSON response。""";

    private static final String AUTOWIRED_GUIDE = """
            @Autowired 让 Spring 自动 inject 所需的 Bean dependency，避免在类中手动 new。

            记忆关联：容器负责 wire dependency；可以注入 constructor 或 field，多个同类型 Bean 时用 qualifier 区分。

            实践建议：优先使用构造器注入，让依赖更明确、也更容易测试。""";

    private static final Map<String, String> BUILT_IN_GUIDES = Map.of(
            "@Controller", CONTROLLER_GUIDE,
            "@RestController", REST_CONTROLLER_GUIDE,
            "@Autowired", AUTOWIRED_GUIDE
    );

    private static final List<PracticeWord> CONTROLLER_WORDS = List.of(
            new PracticeWord("controller", "控制器；接收请求并协调处理"),
            new PracticeWord("mapping", "映射；把请求路径绑定到处理方法"),
            new PracticeWord("handler", "处理器；实际处理请求的方法"),
            new PracticeWord("model", "模型；传递给视图的数据"),
            new PracticeWord("view", "视图；MVC 中负责页面呈现"),
            new PracticeWord("response", "响应；控制器处理后的返回结果")
    );

    private static final Map<String, List<PracticeWord>> BUILT_IN_PRACTICE_WORDS = Map.of(
            "@Controller", CONTROLLER_WORDS
    );

    private final TechStackRepository stackRepo;

    public DataSeeder(TechStackRepository stackRepo) {
        this.stackRepo = stackRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (stackRepo.count() > 0) {
            refreshBuiltInLearningContent();
            return;
        }

        var java = new TechStack();
        java.setTitle("Java 技术栈");
        java.setDescription("Java 核心注解与依赖注入单词，边打边记技术词汇，配套注解源码。");
        java.setCover("");
        java.setFree(true);
        java.setSortOrder(0);

        java.getNodes().add(node(java, "@Controller", 0,
                "Spring MVC 控制器注解",
                "@Target({ElementType.TYPE})\n@Retention(RetentionPolicy.RUNTIME)\n@Documented\n@Component\npublic @interface Controller {\n    @AliasFor(annotation = Component.class)\n    String value() default \"\";\n}",
                CONTROLLER_GUIDE,
                "WORD",
                sts(
                        st(0, "controller", "控制器；接收请求并协调处理"),
                        st(1, "mapping", "映射；把请求路径绑定到处理方法"),
                        st(2, "handler", "处理器；实际处理请求的方法"),
                        st(3, "model", "模型；传递给视图的数据"),
                        st(4, "view", "视图；MVC 中负责页面呈现"),
                        st(5, "response", "响应；控制器处理后的返回结果")
                )
        ));

        java.getNodes().add(node(java, "@RestController", 1,
                "REST 风格控制器（@Controller + @ResponseBody）",
                "@Target({ElementType.TYPE})\n@Retention(RetentionPolicy.RUNTIME)\n@Documented\n@Controller\n@ResponseBody\npublic @interface RestController {\n    @AliasFor(annotation = Controller.class)\n    String value() default \"\";\n}",
                REST_CONTROLLER_GUIDE,
                "WORD",
                sts(
                        st(0, "rest", "表现层状态转移（REST）"),
                        st(1, "serialize", "序列化"),
                        st(2, "json", "JS 对象表示法"),
                        st(3, "endpoint", "接口 / 端点"),
                        st(4, "response", "响应"),
                        st(5, "body", "请求体 / 响应体")
                )
        ));

        java.getNodes().add(node(java, "@Autowired", 2,
                "依赖注入注解",
                "@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})\n@Retention(RetentionPolicy.RUNTIME)\n@Documented\npublic @interface Autowired {\n    boolean required() default true;\n}",
                AUTOWIRED_GUIDE,
                "WORD",
                sts(
                        st(0, "inject", "注入"),
                        st(1, "dependency", "依赖"),
                        st(2, "field", "字段 / 域"),
                        st(3, "bean", "Bean（受管对象）"),
                        st(4, "wire", "装配 / 连接"),
                        st(5, "qualifier", "限定符")
                )
        ));

        stackRepo.save(java);
    }

    private void refreshBuiltInLearningContent() {
        var stacks = stackRepo.findAll();
        var changed = false;

        for (var stack : stacks) {
            for (var node : stack.getNodes()) {
                var guide = BUILT_IN_GUIDES.get(node.getTitle());
                if (guide != null && !guide.equals(node.getAnnotationExplain())) {
                    node.setAnnotationExplain(guide);
                    changed = true;
                }

                var practiceWords = BUILT_IN_PRACTICE_WORDS.get(node.getTitle());
                if (practiceWords != null && refreshPracticeWords(node, practiceWords)) {
                    changed = true;
                }
            }
        }

        if (changed) {
            stackRepo.saveAll(stacks);
        }
    }

    private boolean refreshPracticeWords(Node node, List<PracticeWord> words) {
        var statements = node.getStatements();
        var alreadyCurrent = statements.size() == words.size();

        for (var index = 0; alreadyCurrent && index < words.size(); index++) {
            var statement = statements.get(index);
            var word = words.get(index);
            alreadyCurrent = statement.getSortOrder() == index
                    && Objects.equals(statement.getEnglish(), word.english())
                    && Objects.equals(statement.getChinese(), word.chinese());
        }

        if (alreadyCurrent) {
            return false;
        }

        var desiredEnglish = new HashSet<String>();
        words.forEach(word -> desiredEnglish.add(word.english()));

        var existingByEnglish = new HashMap<String, Statement>();
        statements.forEach(statement -> existingByEnglish.put(statement.getEnglish(), statement));
        var reusable = new ArrayDeque<>(statements.stream()
                .filter(statement -> !desiredEnglish.contains(statement.getEnglish()))
                .toList());
        var refreshed = new ArrayList<Statement>();

        for (var index = 0; index < words.size(); index++) {
            var word = words.get(index);
            var statement = existingByEnglish.get(word.english());
            var isNewWord = statement == null;

            if (isNewWord) {
                statement = reusable.pollFirst();
                if (statement == null) {
                    statement = new Statement();
                }
                statement.setMastered(false);
            }

            statement.setNode(node);
            statement.setSortOrder(index);
            statement.setEnglish(word.english());
            statement.setChinese(word.chinese());
            statement.setSoundmark("");
            refreshed.add(statement);
        }

        statements.clear();
        statements.addAll(refreshed);
        return true;
    }

    private record PracticeWord(String english, String chinese) {}

    private Node node(TechStack stack, String title, int order, String desc, String code, String explain,
                      String practiceType, List<Statement> sts) {
        var n = new Node();
        n.setStack(stack);
        n.setTitle(title);
        n.setDescription(desc);
        n.setSortOrder(order);
        n.setAnnotationCode(code);
        n.setAnnotationExplain(explain);
        n.setPracticeType(practiceType);
        sts.forEach(s -> s.setNode(n));
        n.setStatements(sts);
        return n;
    }

    private List<Statement> sts(Statement... sts) {
        return new ArrayList<>(Arrays.asList(sts));
    }

    private Statement st(int order, String english, String chinese) {
        var s = new Statement();
        s.setSortOrder(order);
        s.setChinese(chinese);
        s.setEnglish(english);
        s.setSoundmark("");
        s.setMastered(false);
        return s;
    }
}
