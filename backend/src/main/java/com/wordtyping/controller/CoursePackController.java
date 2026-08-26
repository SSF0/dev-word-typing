package com.wordtyping.controller;

import com.wordtyping.dto.CoursePackDto;
import com.wordtyping.dto.CoursePackListItemDto;
import com.wordtyping.dto.NodeDto;
import com.wordtyping.service.TechStackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-pack")
public class CoursePackController {

    private final TechStackService service;

    public CoursePackController(TechStackService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoursePackListItemDto> list() {
        return service.list();
    }

    @GetMapping("/{stackId}")
    public CoursePackDto detail(@PathVariable Long stackId) {
        return service.detail(stackId);
    }

    @GetMapping("/{stackId}/courses/{nodeId}")
    public NodeDto node(@PathVariable Long stackId, @PathVariable Long nodeId) {
        return service.node(stackId, nodeId);
    }

    @PostMapping("/{stackId}/courses/{nodeId}/complete")
    public com.wordtyping.dto.CompleteResponse complete(@PathVariable Long stackId, @PathVariable Long nodeId) {
        NodeDto next = service.complete(stackId, nodeId);
        return new com.wordtyping.dto.CompleteResponse(next);
    }

    @PutMapping("/{stackId}/courses/{nodeId}")
    public NodeDto updateNote(@PathVariable Long stackId, @PathVariable Long nodeId,
                              @RequestBody(required = false) com.wordtyping.dto.UpdateNoteRequest req) {
        String note = (req == null) ? "" : (req.note() == null ? "" : req.note());
        return service.updateNodeNote(stackId, nodeId, note);
    }

    @PutMapping("/{stackId}/courses/{nodeId}/statements/{statementId}")
    public com.wordtyping.dto.StatementDto updateStatementNote(
            @PathVariable Long stackId,
            @PathVariable Long nodeId,
            @PathVariable Long statementId,
            @RequestBody(required = false) com.wordtyping.dto.UpdateStatementNoteRequest req
    ) {
        String note = (req == null || req.note() == null) ? "" : req.note();
        return service.updateStatementNote(stackId, nodeId, statementId, note);
    }
}
