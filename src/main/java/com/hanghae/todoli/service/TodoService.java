package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.TodoRequestDto;
import com.hanghae.todoli.models.Member;
import com.hanghae.todoli.models.Todo;
import com.hanghae.todoli.repository.TodoRepository;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    // 투두 등록
    @Transactional
    public void registerTodo(TodoRequestDto requestDto, UserDetailsImpl userDetails) {

        // 작성자 정보
        final Member member = userDetails.getMember();

        // 새로운 투두
        final Todo todo = new Todo();

        // 투두 데이터
        todo.setWriter(member);
        todo.setContent(requestDto.getContent());
        todo.setStartDate(requestDto.getStartDate());
        todo.setEndDate(requestDto.getEndDate());
        todo.setDifficulty(requestDto.getDifficulty());

        todoRepository.save(todo);
    }

    // 투두 삭제
    @Transactional
    public void deleteTodo(Long id, UserDetailsImpl userDetails) {
        // 로그인 유저와 작성자가 일치?
        // 불일치시 메시지
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo가 존재하지 않습니다!"));

        if (!todo.getWriter().equals(userDetails.getMember())) {
            throw new IllegalArgumentException("Todo 작성자가 아닙니다!");
        }

        todoRepository.deleteById(id);
    }
}
