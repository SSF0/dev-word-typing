package com.wordtyping.dto;

public record StatementDto(
        Long id,
        Integer order,
        String chinese,
        String english,
        String prefix,
        String soundmark,
        String explanation,
        String usageExample,
        String referenceCode,
        String note,
        boolean isMastered
) {
}
