package com.wordtyping.service;

import com.wordtyping.dto.CoursePackDto;
import com.wordtyping.dto.CoursePackListItemDto;
import com.wordtyping.dto.NodeDto;
import com.wordtyping.dto.StatementDto;
import com.wordtyping.entity.Node;
import com.wordtyping.entity.TechStack;
import com.wordtyping.repository.NodeRepository;
import com.wordtyping.repository.StatementRepository;
import com.wordtyping.repository.TechStackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TechStackService {

    private final TechStackRepository stackRepo;
    private final NodeRepository nodeRepo;
    private final StatementRepository statementRepo;

    public TechStackService(
            TechStackRepository stackRepo,
            NodeRepository nodeRepo,
            StatementRepository statementRepo
    ) {
        this.stackRepo = stackRepo;
        this.nodeRepo = nodeRepo;
        this.statementRepo = statementRepo;
    }

    /** GET /course-pack */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<CoursePackListItemDto> list() {
        return stackRepo.findAllByOrderBySortOrderAsc().stream()
                .map(Assemblers::toListItem)
                .toList();
    }

    /** GET /course-pack/{stackId} */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CoursePackDto detail(Long stackId) {
        TechStack stack = stackRepo.findById(stackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "pack not found"));
        return Assemblers.toDetail(stack);
    }

    /** GET /course-pack/{stackId}/courses/{nodeId} */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public NodeDto node(Long stackId, Long nodeId) {
        Node node = nodeRepo.findById(nodeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "course not found"));
        if (!node.getStackId().equals(stackId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "course not in pack");
        }
        return Assemblers.toNode(node);
    }

    /** POST /course-pack/{stackId}/courses/{nodeId}/complete —— 返回下一知识点节点，已完成则返回卡片体 */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public NodeDto complete(Long stackId, Long nodeId) {
        List<com.wordtyping.entity.Node> nodes = nodeRepo.findByStackIdOrderBySortOrderAsc(stackId);
        int idx = nodes.stream().anyMatch(n -> n.getId().equals(nodeId))
                ? indexOf(nodes, nodeId)
                : 0;
        // 当前节点之后的第一个未完成节点（此处种子数据均未完成，直接取下一个）
        Node next = (idx >= 0 && idx < nodes.size() - 1) ? nodes.get(idx + 1) : null;
        if (next == null) {
            return null;
        }
        return Assemblers.toNode(next);
    }

    private int indexOf(List<com.wordtyping.entity.Node> nodes, Long nodeId) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getId().equals(nodeId)) {
                return i;
            }
        }
        return -1;
    }

    /** PUT /course-pack/{stackId}/courses/{nodeId} —— 更新节点（此处保存个人笔记 note） */
    @org.springframework.transaction.annotation.Transactional
    public NodeDto updateNodeNote(Long stackId, Long nodeId, String note) {
        Node node = nodeRepo.findById(nodeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "course not found"));
        if (!node.getStackId().equals(stackId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "course not in pack");
        }
        node.setNote(note);
        Node saved = nodeRepo.save(node);
        return Assemblers.toNode(saved);
    }

    /** 保存小节中某个练习项的个人笔记。 */
    @org.springframework.transaction.annotation.Transactional
    public StatementDto updateStatementNote(
            Long stackId,
            Long nodeId,
            Long statementId,
            String note
    ) {
        var statement = statementRepo.findById(statementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "statement not found"));
        var node = statement.getNode();
        if (node == null
                || !statement.getNodeId().equals(nodeId)
                || !node.getStackId().equals(stackId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "statement not in course");
        }

        statement.setNote(note);
        return Assemblers.toStatement(statementRepo.save(statement));
    }
}
