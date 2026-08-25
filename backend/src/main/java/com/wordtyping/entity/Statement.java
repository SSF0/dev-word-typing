package com.wordtyping.entity;

import jakarta.persistence.*;

/**
 * 练习句子 —— 对应前端「statement」。中文提示 → 英文打字，音标供听写模式用。
 */
@Entity
@Table(name = "statement")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "chinese", columnDefinition = "TEXT")
    private String chinese;

    @Column(name = "english", columnDefinition = "TEXT", nullable = false)
    private String english;

    @Column(length = 512)
    private String soundmark;

    @Column(name = "is_mastered", nullable = false)
    private boolean mastered = false;

    @Column(name = "node_id", nullable = false, insertable = false, updatable = false)
    private Long nodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id")
    private Node node;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getChinese() { return chinese; }
    public void setChinese(String chinese) { this.chinese = chinese; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getSoundmark() { return soundmark; }
    public void setSoundmark(String soundmark) { this.soundmark = soundmark; }

    public boolean isMastered() { return mastered; }
    public void setMastered(boolean mastered) { this.mastered = mastered; }

    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    public Node getNode() { return node; }
    public void setNode(Node node) { this.node = node; }
}