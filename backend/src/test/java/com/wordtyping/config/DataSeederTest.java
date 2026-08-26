package com.wordtyping.config;

import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.entity.TechStack;
import com.wordtyping.repository.TechStackRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class DataSeederTest {

    @Test
    void refreshesBuiltInLearningGuidesWithoutReplacingPersonalNotes() {
        var controller = node("@Controller", "旧说明", "记住我自己的笔记");
        controller.getStatements().addAll(List.of(
                statement(controller, 0, "controller", "旧解释", true),
                statement(controller, 1, "receive", "旧解释", false),
                statement(controller, 2, "request", "旧解释", false),
                statement(controller, 3, "handler", "旧解释", true),
                statement(controller, 4, "annotate", "旧解释", false),
                statement(controller, 5, "component", "旧解释", false)
        ));
        var restController = node("@RestController", "旧说明", null);
        var autowired = node("@Autowired", "旧说明", null);
        var stack = new TechStack();
        stack.getNodes().addAll(List.of(controller, restController, autowired));
        var savedStacks = new AtomicReference<List<TechStack>>();
        var stackRepo = repositoryWithExistingStack(stack, savedStacks);

        new DataSeeder(stackRepo).run();

        assertThat(controller.getAnnotationExplain())
                .contains("核心作用", "向下展开一层", "常见用法", "源码拆解")
                .contains("@RequestMapping", "@ResponseBody", "@AliasFor");
        assertThat(controller.getStatements())
                .extracting(Statement::getEnglish)
                .containsExactly("controller", "mapping", "handler", "model", "view", "response");
        assertThat(controller.getStatements().get(0).isMastered()).isTrue();
        assertThat(controller.getStatements().get(2).isMastered()).isTrue();
        assertThat(controller.getStatements().get(1).isMastered()).isFalse();
        assertThat(restController.getAnnotationExplain())
                .contains("endpoint", "serialize", "JSON", "response body");
        assertThat(autowired.getAnnotationExplain())
                .contains("inject", "dependency", "Bean", "qualifier");
        assertThat(controller.getNote()).isEqualTo("记住我自己的笔记");
        assertThat(savedStacks.get()).containsExactly(stack);
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

    private Node node(String title, String explain, String note) {
        var node = new Node();
        node.setTitle(title);
        node.setAnnotationExplain(explain);
        node.setNote(note);
        return node;
    }

    private Statement statement(
            Node node,
            int order,
            String english,
            String chinese,
            boolean mastered
    ) {
        var statement = new Statement();
        statement.setNode(node);
        statement.setSortOrder(order);
        statement.setEnglish(english);
        statement.setChinese(chinese);
        statement.setSoundmark("");
        statement.setMastered(mastered);
        return statement;
    }
}
