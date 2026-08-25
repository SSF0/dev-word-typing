package com.wordtyping.dto;

public record StatementDto(
        Long id,
        Integer order,
        String chinese,
        String english,
        String soundmark,
        boolean isMastered
) {
}