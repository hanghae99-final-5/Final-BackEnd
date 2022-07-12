package com.hanghae.todoli.todo.repository;

import com.hanghae.todoli.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {


    List<Todo> findAllByWriterIdOrderByIdDesc(Long id);

    void deleteAllByWriterIdAndTodoType(Long writerId,int todoType);
}
