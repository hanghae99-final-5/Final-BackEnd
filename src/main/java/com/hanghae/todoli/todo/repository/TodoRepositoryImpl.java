package com.hanghae.todoli.todo.repository;

import com.hanghae.todoli.todo.dto.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import static com.hanghae.todoli.member.QMember.member;
import static com.hanghae.todoli.todo.model.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //completionDate 기준으로 오름차순과 limit7은 pageable을 통해 구현
    @Override
    public List<TodoDetailsResponseDto> findTododetailsDaily(@Param("start") LocalDate start,
                                                             @Param("now") LocalDate now,
                                                             @Param("id") Long id,
                                                             Pageable pageable) {

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
                        todo.completionDate.between(start, now), // start : 현재 날짜 기준 - 7 , now : 현재 날짜 기준 - 1
                        todo.todoType.eq(2)
                )
                .groupBy(todo.completionDate)
                .fetch();
    }

    @Override
    public List<TodoDetailsResponseMonthlyDto> findTodoDetailsMonthly(@Param("startMonth") LocalDate startMonth,
                                                                      @Param("now") LocalDate lastMonth,
                                                                      @Param("id") Long id,
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
                        todo.completionDate.between(startMonth, lastMonth),
                        todo.todoType.eq(2)
                )
                .groupBy(todo.completionDate.month())
                .fetch();
    }

    @Override
    public TodoDetailsResponseWeeklyDto findTodoDetailsWeekly(LocalDate startWeek, LocalDate lastWeek, Long id, Pageable pageable) {
        return queryFactory
                .select(new QTodoDetailsResponseWeeklyDto(
                        todo.count(),
                        todo.difficulty.sum())
                )
                .from(todo)
                .join(todo.writer, member)
                .where(
                        todo.completionState.eq(true),
                        member.id.eq(id),
                        todo.completionDate.between(startWeek, lastWeek),
                        todo.todoType.eq(2)
                ).fetchOne();
    }
}
