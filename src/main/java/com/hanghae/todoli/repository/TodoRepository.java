package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByWriterId(Long id);

    void deleteAllByWriterId(Long id);
    void deleteAllByWriterIdAndTodoType(Long writerId,int todoType);
}
