package com.wordtyping.dto;

import java.util.List;

/** 与前端 fetchCoursePack 返回结构对齐：{ id, title, description, cover, courses: NodeDto[] } */
public record CoursePackDto(
        Long id,
        String title,
        String description,
        boolean isFree,
        String cover,
        List<NodeDto> courses
) {
}