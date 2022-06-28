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

}
