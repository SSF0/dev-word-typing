package com.wordtyping.dto;

/** 与前端 fetchCoursePacks 列表项对齐（不含 courses） */
public record CoursePackListItemDto(
        Long id,
        String title,
        String description,
        boolean isFree,
        String cover
) {
}