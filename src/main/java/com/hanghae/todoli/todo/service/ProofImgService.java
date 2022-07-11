package com.hanghae.todoli.todo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.todoli.alarm.Alarm;
import com.hanghae.todoli.alarm.AlarmRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.model.Todo;
import com.hanghae.todoli.todo.repository.TodoRepository;
import com.hanghae.todoli.todo.dto.ProofImgRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.hanghae.todoli.alarm.AlarmType.AUTHENTICATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProofImgService {

    /**
     * 인증 사진 등록
     * - 사진 재등록시 기존 이미지 삭제
     * - 사진 등록시 인증 날짜 = 종료일 + 3
     */

    private final AmazonS3 amazonS3;
    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final AlarmRepository alarmRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //사진 등록
    @Transactional
    public void imgRegister(Long id, ProofImgRequestDto imgRequestDto, UserDetailsImpl userDetails) {
        // 개시글 조회
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_TODO));

        // 로그인 사용자 가져와서 작성자와 일치하는지 확인
        Long myId = userDetails.getMember().getId();
        Member myInfo = memberRepository.findById(myId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        if (!todo.getWriter().getId().equals(myId)) {
            throw new CustomException(ErrorCode.NOT_TODO_WRITER);
        }

        // url이  not null 일 때
        if (todo.getProofImg() != null) {
            // S3에서 기존 이미지 삭제
//          https://twodo-li.s3.ap-northeast-2.amazonaws.com/
            String imgUrl = todo.getProofImg();
            String imgName = imgUrl.substring(49);

            // S3 저장된 기존 이미지 삭제
            deleteFile(imgName);
        }

        // 이미지 url을 String으로 변환
        String proofImgUrl = getImgUrl(imgRequestDto.getProofImg());

        /**
         * 인증 가능 날짜 = 종료일 + 3
         * 이미 url이 존재 하는 경우에는 증가 X
         * url 초기 등록시만 +3
         */
        if (todo.getProofImg() == null){
            todo.setConfirmDate(todo.getEndDate().plusDays(3));
        }

        // TODO : 2022-07-08 AlarmService로 옮겨서 리팩토링해도 될듯
        //자신이 매칭되어 있고, 매칭투두일때
        if (myInfo.getMatchingState() && todo.getTodoType()==1) {
            Matching matching =matchingRepository.getMatching(myId).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_MATCHING));

            Long partnerId = myId.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();
            Member partner = memberRepository.findById(partnerId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));


            //현재 날짜 출력
            LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            Alarm alarm = Alarm.builder()
                    .alarmDate(now)
                    .alarmType(AUTHENTICATION)
                    .member(partner)
                    .alarmState(0L)
                    .senderId(myInfo.getId())
                    .message(myInfo.getNickname() + "님이 인증을 요청하셨습니다.")
                    .todoId(todo.getId())
                    .build();

            alarmRepository.save(alarm);
        }

        // 이미지 url 저장
        todo.setProofImg(proofImgUrl);
    }

    private String getImgUrl(MultipartFile imageUrl) {
        String fileName = createFileName(imageUrl.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageUrl.getSize());
        objectMetadata.setContentType(imageUrl.getContentType());

        try (InputStream inputStream = imageUrl.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    public String deleteFile(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
        amazonS3.deleteObject(request);
        return "삭제완료";
    }
}
