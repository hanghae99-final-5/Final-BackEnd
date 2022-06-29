package com.hanghae.todoli.repository;

import com.hanghae.todoli.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
