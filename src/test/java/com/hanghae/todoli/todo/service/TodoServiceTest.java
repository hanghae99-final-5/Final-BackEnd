package com.hanghae.todoli.todo.service;

import com.hanghae.todoli.alarm.AlarmRepository;
import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.dto.*;
import com.hanghae.todoli.todo.model.Todo;
import com.hanghae.todoli.todo.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@Transactional
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private AlarmRepository alarmRepository;

    TodoService todoService;

    String content;
    LocalDate startDate;
    LocalDate endDate;
    int difficulty;
    int todoType;

    LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    EquipItem equipItem = new EquipItem(
            1L,
            null,
            null,
            null
    );
    Character character = new Character(
            1L,
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/spon.png",
            100,
            0,
            100,
            0,
            1,
            0,
            null,
            equipItem
    );
    Member existMember = new Member(
            "test@naver.com",
            "test",
            "password",
            true,
            character
    );
    Member existMember2 = new Member(
            "test2@naver.com",
            "test2",
            "password",
            true,
            character
    );

    Matching matching = new Matching(
            1L, 2L
    );

    //1번이 작성
    Todo existTodo1 = new Todo(
            1L, "imgUrl", "todoContent",
            now, LocalDate.now().plusDays(2), LocalDate.now().plusDays(2),
            3, 2, false, false, existMember
    );

    //2번이 작성
    Todo existTodo2 = new Todo(
            2L, "imgUrl", "todoContent",
            now, LocalDate.now().plusDays(2), LocalDate.now().plusDays(2),
            3, 2, false, false, existMember2
    );

    UserDetailsImpl userDetails = new UserDetailsImpl(existMember);

    @BeforeEach
    void setUp() {
        existMember.setId(1L);
        existMember2.setId(2L);
        content = "content";
        startDate = now;
        endDate = LocalDate.now().plusDays(2);
        difficulty = 3;
        todoType = 1;

        todoService = new TodoService(
                todoRepository,
                memberRepository,
                matchingRepository,
                alarmRepository
        );

    }

    @Nested
    @DisplayName("투두 등록")
    class register {

        @Test
        @DisplayName("투두 등록 성공")
        void registerTodo() {
            //given
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            String result = todoService.registerTodo(todoRegisterDto, userDetails);

            //then
            Assertions.assertEquals("투두 등록 성공", result);
        }

        @Test
        @DisplayName("투두 등록 실패 - 매칭아닌데 매칭투두 작성")
        void registerTodoFail1() {
            //given
            existMember.setMatchingState(false);
            todoType = 2;
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("자신이 매칭되어있지 않습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 - content 미기입, 빈 문자열")
        void registerTodoFail2() {
            //given
            content = null;
            TodoRegisterDto todoRegisterDto1 = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);

            content = "";
            TodoRegisterDto todoRegisterDto2 = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception1 = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto1, userDetails));
            CustomException exception2 = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto2, userDetails));

            //then
            Assertions.assertEquals("Todo 내용을 입력해주세요", exception1.getErrorCode().getMessage());
            Assertions.assertEquals("Todo 내용을 입력해주세요", exception2.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 - startDate 미기입")
        void registerTodoFail3() {
            //given
            startDate = null;
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("시작날짜를 선택해주세요", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 - endDate 미기입")
        void registerTodoFail4() {
            //given
            endDate = null;
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("종료날짜를 선택해주세요", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 - difficulty 미기입")
        void registerTodoFail5() {
            //given
            difficulty = 0;
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("난이도를 선택해주세요", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 - todoType 미기입")
        void registerTodoFail6() {
            //given
            todoType = 0;
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("Todo 타입을 선택해주세요", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 -  미기입")
        void registerTodoFail7() {
            //given
            todoType = 0;
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("Todo 타입을 선택해주세요", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 -  startDate 오늘보다 이전")
        void registerTodoFail8() {
            //given
            startDate = LocalDate.now().minusDays(1);
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("시작날짜를 현재날짜 이후로 설정해주세요.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 등록 실패 -  endDate 가 StartDate 보다 이전")
        void registerTodoFail9() {
            //given
            endDate = startDate.minusDays(1);
            TodoRegisterDto todoRegisterDto = new TodoRegisterDto(
                    content,
                    startDate,
                    endDate,
                    difficulty,
                    todoType);
            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.registerTodo(todoRegisterDto, userDetails));

            //then
            Assertions.assertEquals("종료날짜를 시작날짜 이후로 설정해주세요.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("투두 인증해주기")
    class confirm {
        @Test
        @DisplayName("투두 인증 성공")
        void confirmTodo() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();

            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(matchingRepository.getMatching(member.getId())).willReturn(Optional.ofNullable(matching));

            //when
            TodoConfirmDto todoConfirmDto = todoService.confirmTodo(id, userDetails);

            //then
            Assertions.assertEquals(existTodo2.getId(), todoConfirmDto.getTodoId());
            Assertions.assertEquals(true, todoConfirmDto.getConfirmState());
        }

        @Test
        @DisplayName("투두 인증 실패 - 사진 인증 안했는데, 인증해주려는 경우")
        void confirmTodoFail1() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();
            existTodo2.setProofImg("");
            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(matchingRepository.getMatching(member.getId())).willReturn(Optional.ofNullable(matching));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.confirmTodo(id, userDetails));

            //then
            Assertions.assertEquals("잘못된 접근입니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 인증 실패 - 중복된 인증")
        void confirmTodoFail2() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();
            existTodo2.setConfirmState(true);
            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(matchingRepository.getMatching(member.getId())).willReturn(Optional.ofNullable(matching));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.confirmTodo(id, userDetails));

            //then
            Assertions.assertEquals("이미 인증된 Todo입니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("투두 완료(돈 획득)")
    class completion {

        @Test
        @DisplayName("투두 완료 성공")
        void completionTodo1() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();
            existTodo2.setConfirmState(true);

            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(existMember));

            //when
            TodoCompletionDto todoCompletionDto = todoService.completionTodo(id, userDetails);

            //then
            Assertions.assertEquals(existTodo2.getId(), todoCompletionDto.getTodoId());
            Assertions.assertEquals(true, todoCompletionDto.getCompletionState());
        }

        @Test
        @DisplayName("경험치 돈 올바르게 들어가는 것 성공(4)")
        void completionTodo2() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();
            existTodo2.setConfirmState(true);
            existTodo2.setDifficulty(4);

            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(existMember));

            //when
            TodoCompletionDto todoCompletionDto = todoService.completionTodo(id, userDetails);

            //then
            Assertions.assertEquals(20, userDetails.getMember().getCharacter().getExp());
            Assertions.assertEquals(40, userDetails.getMember().getCharacter().getMoney());
            Assertions.assertEquals(existTodo2.getId(), todoCompletionDto.getTodoId());
            Assertions.assertEquals(true, todoCompletionDto.getCompletionState());
        }

        @Test
        @DisplayName("투두 실패 - 인증된 Todo 아님")
        void completionTodoFail1() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();
            existTodo2.setConfirmState(false);

            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.completionTodo(id, userDetails));
            //then
            Assertions.assertEquals("인증된 Todo가 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("투두 실패 - 이미 인증된 투두")
        void completionTodoFail2() {
            //given
            Long id = existTodo2.getId();
            Member member = userDetails.getMember();
            existTodo2.setConfirmState(true);
            existTodo2.setCompletionState(true);

            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo2));
            given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.completionTodo(id, userDetails));
            //then
            Assertions.assertEquals("이미 인증된 Todo입니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("투두 삭제")
    class delete {

        @Test
        @DisplayName("투두 삭제 성공")
        void deleteTodo() {
            //given
            Long id = existTodo1.getId();

            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo1));

            //when
            String result = todoService.deleteTodo(id, userDetails);

            //then
            Assertions.assertEquals("투두 삭제 성공", result);
        }

        @Test
        @DisplayName("투두 삭제 실패 - 본인이 작성안함")
        void deleteTodoFail() {
            //given
            Long id = existTodo1.getId();
            userDetails = new UserDetailsImpl(existMember2);
            given(todoRepository.findById(id)).willReturn(Optional.ofNullable(existTodo1));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    () -> todoService.deleteTodo(id, userDetails));


            //then
            Assertions.assertEquals("Todo 작성자가 아닙니다.", exception.getErrorCode().getMessage());
        }
    }


    @Nested
    @DisplayName("상대방 투두 조회")
    class getPairTodosTest {

        @Test
        @DisplayName("상대방 투두 조회 성공")
        void getPairTodos() {
            //given
            Member member = userDetails.getMember();

            given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(existMember));
            given(matchingRepository.getMatching(member.getId())).willReturn(Optional.ofNullable(matching));
            Long partnerId = member.getId().equals(matching.getRequesterId())
                    ? matching.getRespondentId()
                    : matching.getRequesterId();
            List<Todo> list = new ArrayList<>();
            list.add(existTodo2);
            given(todoRepository.findAllByWriterIdOrderByIdDesc(partnerId)).willReturn(list);

            //when
            PairTodoResponseDto pairTodos = todoService.getPairTodos(userDetails);

            //then
            Assertions.assertEquals(matching.getRespondentId(), pairTodos.getMember().get(0).getPartnerId());
            Assertions.assertEquals(existTodo2.getId(), pairTodos.getTodos().get(0).getTodoId());
            Assertions.assertEquals(existTodo2.getContent(), pairTodos.getTodos().get(0).getContent());
        }
    }

    @Nested
    @DisplayName("자신의 투두 조회")
    class getMyTodosTest {

        @Test
        @DisplayName("자신의 투두 조회 성공")
        void getMyTodos() {
            //given
            Member member = userDetails.getMember();

            given(memberRepository.findById(member.getId())).willReturn(Optional.ofNullable(existMember));
            List<Todo> list = new ArrayList<>();
            list.add(existTodo1);
            given(todoRepository.findAllByWriterIdOrderByIdDesc(member.getId())).willReturn(list);

            //when
            TodoResponseDto myTodos = todoService.getMyTodos(userDetails);

            //then
            Assertions.assertEquals(true, myTodos.getMember().get(0).getMatchingState());
            Assertions.assertEquals(existTodo1.getId(), myTodos.getTodos().get(0).getTodoId());
            Assertions.assertEquals(existTodo1.getContent(), myTodos.getTodos().get(0).getContent());
        }
    }

    @Nested
    @DisplayName("투두 수정")
    class todoModifyTest {

        @Test
        @DisplayName("투두 수정 성공")
        void todoModify() {
            //given
            Long id = existTodo1.getId();
            TodoModifyDto todoModifyDto = new TodoModifyDto(
                    "modifytest",
                    4,
                    2
            );
            given(memberRepository.findById(id)).willReturn(Optional.ofNullable(existMember));
            given(todoRepository.findById(existTodo1.getId())).willReturn(Optional.ofNullable(existTodo1));

            //when
            Todo todo = todoService.todoModify(id, todoModifyDto, userDetails);

            //then
            Assertions.assertEquals("modifytest", todo.getContent());
            Assertions.assertEquals(4, todo.getDifficulty());
            Assertions.assertEquals(2, todo.getTodoType());
        }

        @Test
        @DisplayName("투두 수정 실패 - 투두 작성자 아님")
        void todoModifyFail1() {
            //given
            Long id = existTodo1.getId();

            Member member = new Member();
            member.setId(3L);
            existTodo1.setWriter(member);

            TodoModifyDto todoModifyDto = new TodoModifyDto(
                    "modifyTest",
                    4,
                    2
            );
            given(memberRepository.findById(id)).willReturn(Optional.ofNullable(existMember));
            given(todoRepository.findById(existTodo1.getId())).willReturn(Optional.ofNullable(existTodo1));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    ()->todoService.todoModify(id, todoModifyDto, userDetails));
            //then
            Assertions.assertEquals("Todo 작성자가 아닙니다.",exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("투두 수정 실패 - 수정 투두 내용없음")
        void todoModifyFail2() {
            //given
            Long id = existTodo1.getId();

            TodoModifyDto todoModifyDto = new TodoModifyDto(
                    "",
                    4,
                    2
            );
            given(memberRepository.findById(id)).willReturn(Optional.ofNullable(existMember));
            given(todoRepository.findById(existTodo1.getId())).willReturn(Optional.ofNullable(existTodo1));

            //when
            CustomException exception = Assertions.assertThrows(CustomException.class,
                    ()->todoService.todoModify(id, todoModifyDto, userDetails));
            //then
            Assertions.assertEquals("Todo 내용을 입력해주세요",exception.getErrorCode().getMessage());
        }
    }


    @Nested
    @DisplayName("투두 수정 조회")
    class getModifyTodoTest {

        @Test
        @DisplayName("투두 수정 조회 성공")
        void getModifyTodo() {
            //given
            given(todoRepository.findById(existTodo1.getId())).willReturn(Optional.ofNullable(existTodo1));
            //when
            TodoModifyDto modifyTodo = todoService.getModifyTodo(existTodo1.getId());
            //then
            Assertions.assertEquals("todoContent",modifyTodo.getContent());
            Assertions.assertEquals(3,modifyTodo.getDifficulty());
            Assertions.assertEquals(2,modifyTodo.getTodoType());
        }
    }

}