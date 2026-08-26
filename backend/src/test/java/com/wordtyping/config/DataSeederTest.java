package com.wordtyping.config;

import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.entity.TechStack;
import com.wordtyping.repository.TechStackRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class DataSeederTest {

    private static final Pattern CHINESE_COMMENT = Pattern.compile(
            ".*(//|#|<!--).*\\p{IsHan}.*"
    );

    @Test
    void migratesLegacyAnnotationNodesIntoThreeOrderedLearningSections() {
        var stack = javaStackWithLegacyNodes();
        var savedStacks = new AtomicReference<List<TechStack>>();
        var stackRepo = repositoryWithExistingStack(stack, savedStacks);

        new DataSeeder(stackRepo).run();

        assertThat(stack.getNodes())
                .extracting(Node::getTitle)
                .containsExactly("Java 常用注解", "MyBatis", "Java 项目结构");

        var annotationSection = section(stack, "Java 常用注解");
        assertThat(annotationSection.getStatements())
                .extracting(Statement::getEnglish)
                .startsWith(
                        "RestController",
                        "Controller",
                        "Service",
                        "GetMapping",
                        "PostMapping"
                );
        assertThat(annotationSection.getStatements())
                .allSatisfy(statement -> assertThat(statement.getPrefix()).isEqualTo("@"));
        assertThat(statement(annotationSection, "RestController").getUsageExample())
                .contains("@RequestMapping", "@GetMapping", "findById");
        var requestBody = statement(annotationSection, "RequestBody");
        assertThat(requestBody.getExplanation())
                .contains("通常接收 DTO 而不是 Entity；与 @Valid 配合执行参数校验。");
        assertThat(requestBody.getUsageExample())
                .contains("@Valid @RequestBody", "DTO", "参数校验");

        var mybatisSection = section(stack, "MyBatis");
        assertThat(mybatisSection.getStatements())
                .extracting(Statement::getEnglish)
                .startsWith("mapper", "Mapper", "MapperScan", "Select", "Insert");
        assertThat(statement(mybatisSection, "mapper").getPrefix()).isNull();
        assertThat(statement(mybatisSection, "Mapper").getPrefix()).isEqualTo("@");

        var structureSection = section(stack, "Java 项目结构");
        assertThat(structureSection.getStatements())
                .extracting(Statement::getEnglish)
                .startsWith("controller", "service", "mapper", "entity", "dto", "config");
        assertThat(structureSection.getStatements())
                .allSatisfy(statement -> {
                    assertThat(statement.getUsageExample())
                            .doesNotContain("/Users/mac/project/java/learn/")
                            .contains("\n");
                });

        assertThat(stack.getNodes())
                .flatExtracting(Node::getStatements)
                .allSatisfy(statement -> {
                    assertThat(statement.getExplanation()).isNotBlank();
                    assertThat(statement.getUsageExample()).isNotBlank();
                    assertCommentedMarkdownExample(statement);
                });
        assertThat(statement(annotationSection, "Controller").getNote())
                .isEqualTo("记住我自己的 Controller 笔记");
        assertThat(savedStacks.get()).containsExactly(stack);
    }

    private void assertCommentedMarkdownExample(Statement statement) {
        var lines = statement.getUsageExample().lines().toList();
        assertThat(lines)
                .as("%s 的使用示例应使用 Markdown 代码块", statement.getEnglish())
                .hasSizeGreaterThanOrEqualTo(3);
        assertThat(lines.get(0)).startsWith("```");
        assertThat(lines.get(lines.size() - 1)).isEqualTo("```");
        assertThat(lines.subList(1, lines.size() - 1))
                .filteredOn(line -> !line.isBlank())
                .as("%s 的每行示例代码都应有中文注释", statement.getEnglish())
                .allMatch(line -> CHINESE_COMMENT.matcher(line).matches());
    }

    @Test
    void refreshIsIdempotentAndPreservesProgressAndStatementNotes() {
        var stack = javaStackWithLegacyNodes();
        var savedStacks = new AtomicReference<List<TechStack>>();
        var stackRepo = repositoryWithExistingStack(stack, savedStacks);
        var seeder = new DataSeeder(stackRepo);
        seeder.run();
        var controller = statement(section(stack, "Java 常用注解"), "Controller");
        controller.setMastered(true);
        controller.setNote("迁移后继续记录");

        seeder.run();

        var refreshedController = statement(section(stack, "Java 常用注解"), "Controller");
        assertThat(refreshedController).isSameAs(controller);
        assertThat(refreshedController.isMastered()).isTrue();
        assertThat(refreshedController.getNote()).isEqualTo("迁移后继续记录");
    }

    @SuppressWarnings("unchecked")
    private TechStackRepository repositoryWithExistingStack(
            TechStack stack,
            AtomicReference<List<TechStack>> savedStacks
    ) {
        return (TechStackRepository) Proxy.newProxyInstance(
                TechStackRepository.class.getClassLoader(),
                new Class<?>[]{TechStackRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "count" -> 1L;
                    case "findAll" -> List.of(stack);
                    case "saveAll" -> {
                        var stacks = (List<TechStack>) args[0];
                        savedStacks.set(stacks);
                        yield stacks;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private TechStack javaStackWithLegacyNodes() {
        var stack = new TechStack();
        stack.setTitle("Java 技术栈");
        stack.setDescription("旧说明");
        stack.getNodes().addAll(List.of(
                legacyNode(stack, "@Controller", "记住我自己的 Controller 笔记"),
                legacyNode(stack, "@RestController", null),
                legacyNode(stack, "@Autowired", null)
        ));
        return stack;
    }

    private Node legacyNode(TechStack stack, String title, String note) {
        var node = new Node();
        node.setStack(stack);
        node.setTitle(title);
        node.setAnnotationExplain("旧说明");
        node.setNote(note);
        return node;
    }

    private Node section(TechStack stack, String title) {
        return stack.getNodes().stream()
                .filter(node -> title.equals(node.getTitle()))
                .findFirst()
                .orElseThrow();
    }

    private Statement statement(Node node, String english) {
        return node.getStatements().stream()
                .filter(statement -> english.equals(statement.getEnglish()))
                .findFirst()
                .orElseThrow();
    }
}
