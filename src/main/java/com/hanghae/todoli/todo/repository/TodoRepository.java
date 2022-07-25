package com.hanghae.todoli.todo.repository;

import com.hanghae.todoli.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;



public interface TodoRepository extends JpaRepository<Todo, Long> , TodoRepositoryCustom{

    //멤버 아이디로 투두 역순 조회
    List<Todo> findAllByWriterIdOrderByIdDesc(Long id);

    void deleteAllByWriterIdAndTodoType(Long writerId, int todoType);

    //스케쥴러 함수
//    @Query(value = "insert into TimeData(date) values (:localDate)", nativeQuery = true)
//    void updateDate(@Param("localDate")LocalDate localDate);


    //일간
//    @Query("select new com.hanghae.todoli.todo.dto.TodoDetailsResponseDto(count(t), sum(t.difficulty)) " +
//            "from Todo t join t.writer m " +
//            "where t.completionState = true and m.id =:id and " +
//            "t.completionDate between :start and :now " +
//            "group by t.createdAt")
//    List<TodoDetailsResponseDto> findTodoCntAndExp(@Param("start") LocalDate start,
//                                             @Param("now") LocalDate now,
//                                             @Param("id")Long id,
//                                             Pageable pageable);


//    //QueryDsl 적용
//    @Query()
//    List<TodoDetailsResponseDto> findTodoCntAndExpDsl(@Param("start") LocalDate start,
//                                                   @Param("now") LocalDate now,
//                                                   @Param("id")Long id,
//                                                   Pageable pageable);
}
