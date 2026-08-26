package com.wordtyping.config;

import com.wordtyping.config.JavaLearningCatalog.LearningItemSeed;
import com.wordtyping.config.JavaLearningCatalog.LearningSectionSeed;
import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.entity.TechStack;
import com.wordtyping.repository.TechStackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 首次启动或内容版本变化时，写入并刷新内置 Java 学习目录。 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String JAVA_STACK_TITLE = "Java 技术栈";
    private static final String JAVA_STACK_DESCRIPTION =
            "Java 常用注解、MyBatis 与项目结构规范；按最常用内容优先练习。";

    private final TechStackRepository stackRepo;

    public DataSeeder(TechStackRepository stackRepo) {
        this.stackRepo = stackRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (stackRepo.count() == 0) {
            var javaStack = new TechStack();
            configureJavaStack(javaStack);
            refreshSections(javaStack);
            stackRepo.save(javaStack);
            return;
        }

        var stacks = stackRepo.findAll();
        var javaStacks = stacks.stream()
                .filter(stack -> JAVA_STACK_TITLE.equals(stack.getTitle()))
                .toList();
        if (javaStacks.isEmpty()) {
            return;
        }

        javaStacks.forEach(stack -> {
            configureJavaStack(stack);
            refreshSections(stack);
        });
        stackRepo.saveAll(stacks);
    }

    private void configureJavaStack(TechStack stack) {
        stack.setTitle(JAVA_STACK_TITLE);
        stack.setDescription(JAVA_STACK_DESCRIPTION);
        stack.setCover("");
        stack.setFree(true);
        stack.setSortOrder(0);
    }

    private void refreshSections(TechStack stack) {
        var legacyNotes = legacyNodeNotes(stack.getNodes());
        var existingSections = new HashMap<String, Node>();
        stack.getNodes().forEach(node -> existingSections.put(node.getTitle(), node));

        var refreshedSections = new ArrayList<Node>();
        var sectionSeeds = JavaLearningCatalog.sections();
        for (var index = 0; index < sectionSeeds.size(); index++) {
            var seed = sectionSeeds.get(index);
            var section = existingSections.get(seed.title());
            if (section == null) {
                section = new Node();
            }
            configureSection(stack, section, seed, index);
            refreshItems(section, seed.items(), legacyNotes);
            refreshedSections.add(section);
        }

        stack.getNodes().clear();
        stack.getNodes().addAll(refreshedSections);
    }

    private Map<String, String> legacyNodeNotes(List<Node> nodes) {
        var notes = new HashMap<String, String>();
        nodes.stream()
                .filter(node -> node.getTitle() != null && node.getTitle().startsWith("@"))
                .filter(node -> node.getNote() != null && !node.getNote().isBlank())
                .forEach(node -> notes.put(node.getTitle(), node.getNote()));
        return notes;
    }

    private void configureSection(
            TechStack stack,
            Node section,
            LearningSectionSeed seed,
            int order
    ) {
        section.setStack(stack);
        section.setTitle(seed.title());
        section.setDescription(seed.description());
        section.setSortOrder(order);
        section.setAnnotationCode(null);
        section.setAnnotationExplain(null);
        section.setPracticeType("WORD");
    }

    private void refreshItems(
            Node section,
            List<LearningItemSeed> itemSeeds,
            Map<String, String> legacyNotes
    ) {
        var existingItems = new HashMap<String, Statement>();
        section.getStatements().forEach(item -> existingItems.put(item.getEnglish(), item));
        var refreshedItems = new ArrayList<Statement>();

        for (var index = 0; index < itemSeeds.size(); index++) {
            var seed = itemSeeds.get(index);
            var item = existingItems.get(seed.english());
            if (item == null && seed.prefix() != null) {
                item = existingItems.get(seed.prefix() + seed.english());
            }
            if (item == null) {
                item = new Statement();
                item.setMastered(false);
                item.setNote(legacyNotes.get(displayTerm(seed)));
            }

            configureItem(section, item, seed, index);
            refreshedItems.add(item);
        }

        section.getStatements().clear();
        section.getStatements().addAll(refreshedItems);
    }

    private void configureItem(
            Node section,
            Statement item,
            LearningItemSeed seed,
            int order
    ) {
        item.setNode(section);
        item.setSortOrder(order);
        item.setEnglish(seed.english());
        item.setPrefix(seed.prefix());
        item.setChinese(seed.chinese());
        item.setSoundmark("");
        item.setExplanation(seed.explanation());
        item.setUsageExample(seed.usageExample());
        item.setReferenceCode(seed.referenceCode());
    }

    private String displayTerm(LearningItemSeed seed) {
        return (seed.prefix() == null ? "" : seed.prefix()) + seed.english();
    }
}
