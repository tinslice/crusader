package com.tinslice.crusader.demo.controller;

import com.tinslice.crusader.demo.exceptions.ResourceNotFoundException;
import com.tinslice.crusader.demo.model.TodoItem;
import com.tinslice.crusader.demo.repository.TodoItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TodoController {
    private final TodoItemRepository todoItemRepository;

    public TodoController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @GetMapping("/todos")
    public List<TodoItem> getAllItems() {
        return todoItemRepository.findAll();
    }

    @PostMapping("/todos")
    public TodoItem createTodoItem(@Valid @RequestBody TodoItem item) {
        return todoItemRepository.save(item);
    }

    @PutMapping("/todos/{itemId}")
    public TodoItem updateTodoItem(@PathVariable Long itemId,
                                   @Valid @RequestBody TodoItem request) {
        return todoItemRepository.findById(itemId)
                .map(todoItem -> {
                    todoItem.setCompleted(request.getCompleted());
                    todoItem.setValue(request.getValue());
                    return todoItemRepository.save(todoItem);
                }).orElseThrow(() -> new ResourceNotFoundException("Todo item not found for id " + itemId));
    }

    @PutMapping("/todos")
    public List<TodoItem> updateTodoItems(@Valid @RequestBody List<TodoItem> requestItems) {
        List<TodoItem> persistedItems = todoItemRepository.findAll();

        List<TodoItem> removeItems = persistedItems.stream().filter(item -> !requestItems.contains(item)).collect(Collectors.toList());
        todoItemRepository.deleteAll(removeItems);

        List<TodoItem> updatedItems = new ArrayList<>();

        for (TodoItem requestItem : requestItems) {
            TodoItem updatedItem;
            if (requestItem.getId() != null && requestItem.getId() > 0) {
                updatedItem = todoItemRepository.findById(requestItem.getId())
                        .map(todoItem -> {
                            todoItem.setCompleted(requestItem.getCompleted());
                            todoItem.setValue(requestItem.getValue());
                            return todoItemRepository.save(todoItem);
                        }).orElseThrow(() -> new ResourceNotFoundException("Todo item not found for id " + requestItem.getId()));
            } else {
                updatedItem = todoItemRepository.save(new TodoItem(requestItem.getValue(), requestItem.getCompleted()));
            }

            updatedItems.add(updatedItem);
        }

        return updatedItems;
    }

    @DeleteMapping("/todos/{itemId}")
    public ResponseEntity<?> deleteTodoItem(@PathVariable Long itemId) {
        return todoItemRepository.findById(itemId)
                .map(question -> {
                    todoItemRepository.delete(question);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Todo item not found for id " + itemId));
    }
}
