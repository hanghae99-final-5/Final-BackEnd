package com.hanghae.todoli.todo.repository;

import com.hanghae.todoli.todo.dto.TodoDetailsResponseDto;
import com.hanghae.todoli.todo.dto.TodoDetailsResponseMonthlyDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepositoryCustom {
    List<TodoDetailsResponseDto> findTodoDetails(@Param("start") LocalDate start,
                                                      @Param("now") LocalDate now,
                                                      @Param("id")Long id,
                                                      Pageable pageable);

    List<TodoDetailsResponseMonthlyDto> findTodoDetailsMonthly(@Param("startMonth") LocalDate startMonth,
                                                                   @Param("lastMonth") LocalDate lastMonth,
                                                                   @Param("id")Long id,
                                                                   Pageable pageable);
}
