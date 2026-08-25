package com.wordtyping.repository;

import com.wordtyping.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    List<TechStack> findAllByOrderBySortOrderAsc();
}