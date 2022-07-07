package com.hanghae.todoli.todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByWriterId(Long id);

    void deleteAllByWriterIdAndTodoType(Long writerId,int todoType);
}
