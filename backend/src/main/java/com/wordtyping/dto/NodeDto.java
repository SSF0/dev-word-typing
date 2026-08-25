package com.wordtyping.dto;

import java.util.List;

/**
 * 知识点节点 DTO —— 对应前端 Course。
 * 在前端期望字段上额外附加 annotationCode / annotationExplain（新增），供练习页侧栏展示源码。
 */
public record NodeDto(
        Long id,
        String title,
        String description,
        Integer order,
        Long coursePackId,
        Integer completionCount,
        Integer statementIndex,
        String video,
        String annotationCode,
        String annotationExplain,
        String practiceType,
        String note,
        List<StatementDto> statements
) {
}