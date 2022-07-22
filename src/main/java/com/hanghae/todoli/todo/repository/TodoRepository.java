package com.hanghae.todoli.todo.repository;

import com.hanghae.todoli.todo.dto.TodoDetailsResponseDto;
import com.hanghae.todoli.todo.model.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByWriterIdOrderByIdDesc(Long id);

    void deleteAllByWriterIdAndTodoType(Long writerId, int todoType);


    //일간
    @Query("select new com.hanghae.todoli.todo.dto.TodoDetailsResponseDto(count(t), sum(t.difficulty)) " +
            "from Todo t join t.writer m " +
            "where t.completionState = true and m.id =:id and " +
            "t.completionDate between :start and :now " +
            "group by t.createdAt")
    List<TodoDetailsResponseDto> findTodoCntAndExp(@Param("start") LocalDate start,
                                             @Param("now") LocalDate now,
                                             @Param("id")Long id,
                                             Pageable pageable);


//    @Query("select sum(t.difficulty) * 5 " +
//            "from Todo t join t.writer m " +
//            "where t.completionState = true and m.id =:id and " +
//            "t.createdAt between :start and :now " +
//            "group by t.createdAt")
//    List<Integer> findTodoExp(LocalDate start, LocalDate now, Long memberId, Pageable pageable);
}
/*
* 해당 날짜에 완료를 누른 투두들의 난이도를 가져와서 계산. difficulty * 5
* */