package com.wordtyping.service;

import com.wordtyping.dto.CoursePackDto;
import com.wordtyping.dto.CoursePackListItemDto;
import com.wordtyping.dto.NodeDto;
import com.wordtyping.dto.StatementDto;
import com.wordtyping.entity.Node;
import com.wordtyping.entity.Statement;
import com.wordtyping.entity.TechStack;

public final class Assemblers {

    private Assemblers() {}

    public static CoursePackListItemDto toListItem(TechStack stack) {
        return new CoursePackListItemDto(
                stack.getId(),
                stack.getTitle(),
                stack.getDescription(),
                stack.isFree(),
                stack.getCover()
        );
    }

    public static CoursePackDto toDetail(TechStack stack) {
        return new CoursePackDto(
                stack.getId(),
                stack.getTitle(),
                stack.getDescription(),
                stack.isFree(),
                stack.getCover(),
                stack.getNodes().stream().map(Assemblers::toNode).toList()
        );
    }

    public static NodeDto toNode(Node node) {
        return new NodeDto(
                node.getId(),
                node.getTitle(),
                node.getDescription(),
                node.getSortOrder(),
                node.getStackId(),
                node.getStatements() == null ? null : node.getStatements().size(),
                node.getStatements() == null ? null : node.getStatements().size(),
                null,
                node.getAnnotationCode(),
                node.getAnnotationExplain(),
                node.getPracticeType(),
                node.getNote(),
                node.getStatements() == null ? java.util.List.of()
                        : node.getStatements().stream().map(Assemblers::toStatement).toList()
        );
    }

    public static StatementDto toStatement(Statement st) {
        return new StatementDto(
                st.getId(),
                st.getSortOrder(),
                st.getChinese(),
                st.getEnglish(),
                st.getPrefix(),
                st.getSoundmark(),
                st.getExplanation(),
                st.getUsageExample(),
                st.getReferenceCode(),
                st.getNote(),
                st.isMastered()
        );
    }
}
