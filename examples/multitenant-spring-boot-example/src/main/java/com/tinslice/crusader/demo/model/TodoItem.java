package com.tinslice.crusader.demo.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "todo_item")
public class TodoItem extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todo_item_id_generator")
    @SequenceGenerator(name = "todo_item_id_generator", sequenceName = "todo_item_id_sequence", allocationSize = 1)
    private Long id;

    @Column(columnDefinition = "text")
    private String value;

    @Column(columnDefinition = "boolean default false")
    private Boolean completed;

    public TodoItem() {
    }

    public TodoItem(String value, Boolean completed) {
        this.value = value;
        this.completed = completed;
    }

    public TodoItem(TodoItem item) {
        this.id = item.id;
        this.value = item.value;
        this.completed = item.completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoItem todoItem = (TodoItem) o;

        return Objects.equals(id, todoItem.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

