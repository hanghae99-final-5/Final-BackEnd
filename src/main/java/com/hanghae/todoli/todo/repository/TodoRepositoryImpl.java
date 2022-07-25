package com.hanghae.todoli.todo.repository;

import com.hanghae.todoli.member.QMember;
import com.hanghae.todoli.todo.dto.QTodoDetailsResponseDto;
import com.hanghae.todoli.todo.dto.QTodoDetailsResponseMonthlyDto;
import com.hanghae.todoli.todo.dto.TodoDetailsResponseDto;
import com.hanghae.todoli.todo.dto.TodoDetailsResponseMonthlyDto;
import com.hanghae.todoli.todo.model.QTodo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.hanghae.todoli.member.QMember.*;
import static com.hanghae.todoli.todo.model.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    //completionDate 기준으로 오름차순과 limit7은 pageable을 통해 구현
    @Override
    public List<TodoDetailsResponseDto> findTodoDetails(@Param("start") LocalDate start,
                                                             @Param("now") LocalDate now,
                                                             @Param("id")Long id,
                                                             Pageable pageable){

        return queryFactory
                .select(new QTodoDetailsResponseDto(
                        todo.completionDate,
                        todo.count(),
                        todo.difficulty.sum())
                )
                .from(todo)
                .join(todo.writer, member)
                .where(
                        todo.completionState.eq(true),
                        member.id.eq(id),
                        todo.completionDate.between(start, now) // start : 현재 날짜 기준 - 7 , now : 현재 날짜 기준 - 1
                        )
                .groupBy(todo.completionDate)
                .fetch();
    }

    @Override
    public List<TodoDetailsResponseMonthlyDto> findTodoDetailsMonthly(@Param("startMonth") LocalDate startMonth,
                                                                      @Param("now") LocalDate lastMonth,
                                                                      @Param("id")Long id,
                                                                      Pageable pageable) {

        return queryFactory
                .select(new QTodoDetailsResponseMonthlyDto(
                        todo.completionDate.month(),
                        todo.count(),
                        todo.difficulty.sum())
                )
                .from(todo)
                .join(todo.writer, member)
                .where(
                        todo.completionState.eq(true),
                        member.id.eq(id),
                        todo.completionDate.between(startMonth, lastMonth)
                )
                .groupBy(todo.completionDate.month())
                .fetch();
    }
}
