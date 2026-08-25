package com.wordtyping.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识点节点 —— 对应前端「课程」。一个节点 = 一个注解/知识点（如 @Controller）。
 * 额外携带该知识点的源码实现 + 使用场景说明，供练习页侧栏展示。
 */
@Entity
@Table(name = "node")
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 512)
    private String description;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    /** 注解/知识点源码实现，如 @interface RestController {...} */
    @Column(name = "annotation_code", columnDefinition = "TEXT")
    private String annotationCode;

    /** 源码/用法注释说明 */
    @Column(name = "annotation_explain", columnDefinition = "TEXT")
    private String annotationExplain;

    /** 练习模式：WORD(单词练习) / SENTENCE(整句练习) */
    @Column(name = "practice_type", nullable = false, length = 16)
    private String practiceType = "SENTENCE";

    /** 个人见解/学习笔记（可编辑，存库） */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "stack_id", nullable = false, insertable = false, updatable = false)
    private Long stackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id")
    private TechStack stack;

    @OneToMany(mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Statement> statements = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getAnnotationCode() { return annotationCode; }
    public void setAnnotationCode(String annotationCode) { this.annotationCode = annotationCode; }

    public String getAnnotationExplain() { return annotationExplain; }
    public void setAnnotationExplain(String annotationExplain) { this.annotationExplain = annotationExplain; }

    public String getPracticeType() { return practiceType; }
    public void setPracticeType(String practiceType) { this.practiceType = practiceType; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getStackId() { return stackId; }
    public void setStackId(Long stackId) { this.stackId = stackId; }

    public TechStack getStack() { return stack; }
    public void setStack(TechStack stack) { this.stack = stack; }

    public List<Statement> getStatements() { return statements; }
    public void setStatements(List<Statement> statements) { this.statements = statements; }
}