package com.wordtyping.repository;

import com.wordtyping.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Long> {

    // 明细读取依赖 service 层 @Transactional(readOnly=true) 作惰性加载
    List<Node> findByStackIdOrderBySortOrderAsc(Long stackId);
}