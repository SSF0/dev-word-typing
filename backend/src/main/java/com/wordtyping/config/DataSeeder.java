package com.wordtyping.config;

import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.entity.TechStack;
import com.wordtyping.repository.TechStackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 首次启动时，若数据库为空则写入种子数据。
 * 「Java 技术栈」每个节点 = 一个注解/知识点；practiceType=WORD 表示进去反复打该知识点相关的英文单词。
 * 每个单词同时可携带中文释义，练习页右侧展示注解源码，左侧打单词。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final TechStackRepository stackRepo;

    public DataSeeder(TechStackRepository stackRepo) {
        this.stackRepo = stackRepo;
    }

    @Override
    public void run(String... args) {
        if (stackRepo.count() > 0) {
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
                "@Controller 标注一个类为 Spring MVC 控制器，负责接收 HTTP 请求并映射到处理方法。",
                "WORD",
                sts(
                        st(0, "controller", "控制器；控制层组件"),
                        st(1, "receive", "接收；收到"),
                        st(2, "request", "请求；HTTP 请求"),
                        st(3, "handler", "处理器；处理方法"),
                        st(4, "annotate", "给...加注解"),
                        st(5, "component", "组件；部件")
                )
        ));

        java.getNodes().add(node(java, "@RestController", 1,
                "REST 风格控制器（@Controller + @ResponseBody）",
                "@Target({ElementType.TYPE})\n@Retention(RetentionPolicy.RUNTIME)\n@Documented\n@Controller\n@ResponseBody\npublic @interface RestController {\n    @AliasFor(annotation = Controller.class)\n    String value() default \"\";\n}",
                "@RestController 返回的对象会直接序列化为 JSON。",
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
                "@Autowired 自动注入 Spring 管理的 Bean 依赖。",
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