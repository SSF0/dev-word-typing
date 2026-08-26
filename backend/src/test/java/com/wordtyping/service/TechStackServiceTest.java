package com.wordtyping.service;

import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.repository.NodeRepository;
import com.wordtyping.repository.StatementRepository;
import com.wordtyping.repository.TechStackRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TechStackServiceTest {

    @Test
    void mapsLearningDetailsFromAStatement() {
        var statement = new Statement();
        statement.setId(3L);
        statement.setSortOrder(0);
        statement.setPrefix("@");
        statement.setEnglish("RestController");
        statement.setChinese("REST 控制器注解");
        statement.setExplanation("直接返回 JSON 响应");
        statement.setUsageExample("@RestController\npublic class UserController { }");
        statement.setReferenceCode("@Controller\n@ResponseBody");
        statement.setNote("当前注解笔记");

        var dto = Assemblers.toStatement(statement);

        assertThat(dto.prefix()).isEqualTo("@");
        assertThat(dto.english()).isEqualTo("RestController");
        assertThat(dto.explanation()).isEqualTo("直接返回 JSON 响应");
        assertThat(dto.usageExample()).contains("public class UserController");
        assertThat(dto.referenceCode()).isEqualTo("@Controller\n@ResponseBody");
        assertThat(dto.note()).isEqualTo("当前注解笔记");
    }

    @Test
    void updatesTheNoteOnTheRequestedLearningItem() {
        var node = new Node();
        node.setId(2L);
        node.setStackId(1L);

        var statement = new Statement();
        statement.setId(3L);
        statement.setNodeId(2L);
        statement.setNode(node);
        statement.setSortOrder(0);
        statement.setEnglish("@RestController");
        statement.setChinese("REST 控制器注解");

        var savedStatement = new AtomicReference<Statement>();
        var statementRepository = statementRepository(statement, savedStatement);
        var service = new TechStackService(
                unusedRepository(TechStackRepository.class),
                unusedRepository(NodeRepository.class),
                statementRepository
        );

        var updated = service.updateStatementNote(1L, 2L, 3L, "新的理解");

        assertThat(updated.note()).isEqualTo("新的理解");
        assertThat(savedStatement.get()).isSameAs(statement);
    }

    @SuppressWarnings("unchecked")
    private StatementRepository statementRepository(
            Statement statement,
            AtomicReference<Statement> savedStatement
    ) {
        return (StatementRepository) Proxy.newProxyInstance(
                StatementRepository.class.getClassLoader(),
                new Class<?>[]{StatementRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findById" -> Optional.of(statement);
                    case "save" -> {
                        var saved = (Statement) args[0];
                        savedStatement.set(saved);
                        yield saved;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T unusedRepository(Class<T> repositoryType) {
        return (T) Proxy.newProxyInstance(
                repositoryType.getClassLoader(),
                new Class<?>[]{repositoryType},
                (proxy, method, args) -> {
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
