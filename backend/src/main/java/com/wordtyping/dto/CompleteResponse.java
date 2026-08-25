package com.wordtyping.dto;

/** 与前端 fetchCompleteCourse 返回结构对齐：{ nextCourse: NodeDto | null } */
public record CompleteResponse(NodeDto nextCourse) {
}